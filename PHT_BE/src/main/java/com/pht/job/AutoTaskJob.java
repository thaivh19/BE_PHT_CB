package com.pht.job;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

import com.pht.entity.SthamSo;
import com.pht.entity.StoKhai;
import com.pht.entity.StoKhaiCt;
import com.pht.service.SthamSoService;
import com.pht.repository.ToKhaiThongTinRepository;
import com.pht.repository.ToKhaiThongTinChiTietRepository;
import com.pht.repository.SysUserRepository;
import com.pht.entity.SysUser;
import com.pht.controller.FptEInvoiceController;
import com.pht.model.request.CreateIcrRequest;
import com.pht.model.request.SearchInvoiceRequest;
import com.pht.utils.ConvertNumberToString;
import com.pht.controller.SBienLaiController;
import com.pht.model.request.SBienLaiCreateRequest;
import com.pht.model.request.SBienLaiCtCreateRequest;
import com.pht.model.request.UpdateTrangThaiPhatHanhRequest;
import com.pht.service.ToKhaiThongTinService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AutoTaskJob {

	@Value("${auto.job.fixedDelayMs:300000}")
	private String fixedDelayMs;

	@Autowired
	private SthamSoService sthamSoService;

	@Autowired
	private ToKhaiThongTinRepository toKhaiThongTinRepository;

	@Autowired
	private FptEInvoiceController fptEInvoiceController;

	@Autowired
	private ToKhaiThongTinChiTietRepository toKhaiThongTinChiTietRepository;

	@Autowired
	private SysUserRepository sysUserRepository;

	@Autowired
	private SBienLaiController sBienLaiController;

	@Autowired
	private ToKhaiThongTinService toKhaiThongTinService;

	private static final String PARAM_JOB_ENABLED = "BL_AUTO_JOB"; // 1: on, 0: off

    // Thông tin user FPT lấy từ STHAM_SO (MA_TS = BL_USER, BL_PW)

	/**
	 * Job tự động chạy định kỳ theo cấu hình fixedDelayMs (ms)
	 * Mặc định: 5 phút (300000 ms)
	 * Có thể tắt/bật qua tham số hệ thống trong DB: STHAM_SO(MA_TS = BL_AUTO_JOB)
	 */
	@Scheduled(fixedDelayString = "${auto.job.fixedDelayMs:300000}")
	public void runAutoTask() {
		try {
			if (!isJobEnabledFromDb()) {
				log.info("[AUTO-TASK] Job đang bị tắt qua DB (BL_AUTO_JOB=0), bỏ qua lần chạy");
				return;
			}

			log.info("[AUTO-TASK] Bắt đầu chạy job định kỳ (fixedDelayMs={})", fixedDelayMs);

			// Tìm các tờ khai có chi tiết với tt_nh = "02"
			processToKhaiWithTrangThaiNganHang02();

			log.info("[AUTO-TASK] Kết thúc chạy job định kỳ");
		} catch (Exception ex) {
			log.error("[AUTO-TASK] Lỗi khi thực thi job: ", ex);
		}
	}

	private boolean isJobEnabledFromDb() {
		SthamSo param = sthamSoService.findByMaTs(PARAM_JOB_ENABLED);
		if (param == null || param.getGiaTri() == null) {
			// Không có cấu hình trong DB thì coi như bật (mặc định on)
			return true;
		}
		String value = param.getGiaTri().trim();
		return !"0".equals(value) && !"false".equalsIgnoreCase(value);
	}

	/**
	 * Xử lý các tờ khai có trạng thái ngân hàng = "02" và idBienLai = null
	 */
	private void processToKhaiWithTrangThaiNganHang02() {
		try {
			log.info("[AUTO-TASK] Bắt đầu tìm kiếm tờ khai có TT_NH = '02' và idBienLai = null");
			
			// Tìm tất cả tờ khai có trạng thái ngân hàng = "02", idBienLai = null và tổng tiền phí < GH_TP (nếu có)
			java.math.BigDecimal gioiHanTongPhi = getSystemParamAsBigDecimal("GH_TP");
			List<StoKhai> toKhaiList = toKhaiThongTinRepository.findAuto("02", gioiHanTongPhi);
			log.info("[AUTO-TASK] GH_TP = {}. Tìm thấy {} tờ khai thỏa điều kiện.", gioiHanTongPhi, toKhaiList.size());
			
			if (toKhaiList == null || toKhaiList.isEmpty()) {
				log.info("[AUTO-TASK] Không tìm thấy tờ khai nào có TT_NH = '02' và idBienLai = null");
				return;
			}
			
			log.info("[AUTO-TASK] Tìm thấy {} tờ khai có TT_NH = '02' và idBienLai = null", toKhaiList.size());
			
			// Xử lý từng tờ khai
			for (StoKhai toKhai : toKhaiList) {
				processToKhai(toKhai);
			}
			
			log.info("[AUTO-TASK] Hoàn thành xử lý {} tờ khai có TT_NH = '02' và idBienLai = null", toKhaiList.size());
			
		} catch (Exception e) {
			log.error("[AUTO-TASK] Lỗi khi xử lý tờ khai có TT_NH = '02' và idBienLai = null: ", e);
		}
	}

	/**
	 * Xử lý từng tờ khai cụ thể
	 */
	private void processToKhai(StoKhai toKhai) {
		try {
			log.info("[AUTO-TASK] Xử lý tờ khai ID: {}, Số tờ khai: {}, TT_NH: {}", 
					toKhai.getId(), toKhai.getSoToKhai(), toKhai.getTrangThaiNganHang());

			// Gọi API nội bộ tạo ICR: /api/fpt-einvoice/create-icr
			CreateIcrRequest request = buildCreateIcrRequestFromToKhai(toKhai);
			ResponseEntity<?> response = fptEInvoiceController.createIcr(request);
			log.info("[AUTO-TASK] Kết quả tạo ICR cho toKhaiId {}: status={} body={}", toKhai.getId(), 
					response.getStatusCode(), response.getBody());

			String sid = extractSidFromFptResponse(response.getBody());
			String idtStr = extractIdtFromFptResponse(response.getBody());
			String form = extractFieldFromFptResponse(response.getBody(), "form");
			String serial = extractFieldFromFptResponse(response.getBody(), "serial");
			String seq = extractFieldFromFptResponse(response.getBody(), "seq");
			if (notBlank(sid)) {
				SBienLaiCreateRequest blReq = buildBienLaiCreateRequestFromToKhai(toKhai, sid, idtStr, form, serial, seq);
				ResponseEntity<?> blResp = sBienLaiController.createBienLai(blReq);
				log.info("[AUTO-TASK] Đã gọi tạo biên lai, status={}, body={}", blResp.getStatusCode(), blResp.getBody());


				// Sau khi insert thành công biên lai, gọi search-icr để lấy base64 và cập nhật
				int bodyLen = 0;
				try {
					SearchInvoiceRequest searchReq = buildSearchIcrRequest(toKhai, sid);
					ResponseEntity<?> searchResp = fptEInvoiceController.searchInvoice(searchReq);
					Object bodyObj = (searchResp != null) ? searchResp.getBody() : null;
					bodyLen = (bodyObj != null) ? bodyObj.toString().length() : 0;
					String statusStr = (searchResp != null) ? String.valueOf(searchResp.getStatusCode()) : "null";
					log.info("[AUTO-TASK] Đã gọi search-icr cho toKhaiId {}: status={} body-length={}", toKhai.getId(), statusStr, bodyLen);
				} catch (Exception e) {
					log.error("[AUTO-TASK] Lỗi khi gọi search-icr cho toKhaiId {}: ", toKhai.getId(), e);
				}

				// Chỉ khi bodyLen > 0 mới cập nhật trạng thái phát hành = "02"
				if (bodyLen > 0) {
					try {
						UpdateTrangThaiPhatHanhRequest updReq = new UpdateTrangThaiPhatHanhRequest();
						updReq.setId(toKhai.getId());
						updReq.setTrangThaiPhatHanh("02");
						toKhaiThongTinService.updateTrangThaiPhatHanh(updReq);
						log.info("[AUTO-TASK] Đã cập nhật tt_ph=02 cho toKhaiId {} (sau search-icr bodyLen>{})", toKhai.getId(), 0);
					} catch (Exception e) {
						log.error("[AUTO-TASK] Lỗi cập nhật tt_ph=02 cho toKhaiId {}: ", toKhai.getId(), e);
					}
				} else {
					log.warn("[AUTO-TASK] Bỏ qua cập nhật tt_ph=02 vì search-icr bodyLen={} cho toKhaiId {}", bodyLen, toKhai.getId());
				}
			} else {
				log.warn("[AUTO-TASK] Không trích xuất được sid từ FPT response, bỏ qua tạo biên lai");
			}

			log.info("[AUTO-TASK] Hoàn thành xử lý tờ khai ID: {}", toKhai.getId());
			
		} catch (Exception e) {
			log.error("[AUTO-TASK] Lỗi khi xử lý tờ khai ID {}: ", toKhai.getId(), e);
		}
	}

	/**
	 * Dựng CreateIcrRequest từ dữ liệu tờ khai.
	 * Lưu ý: Map 'receipt' cần mapping đầy đủ theo spec FPT. Hiện tại mới map các trường cơ bản.
	 */
	private CreateIcrRequest buildCreateIcrRequestFromToKhai(StoKhai toKhai) {
		CreateIcrRequest req = new CreateIcrRequest();
		java.util.Map<String, Object> user = new java.util.HashMap<>();
		String apiUsername = getSystemParamValue("BL_USER");
		String apiPassword = getSystemParamValue("BL_PW");
		user.put("username", apiUsername);
		user.put("password", apiPassword);
		req.setUser(user);

		// Receipt mapping theo mẫu yêu cầu
		java.util.Map<String, Object> receipt = new java.util.HashMap<>();
		// SID: ưu tiên dùng idPhatHanh nếu đã có; nếu chưa có thì sinh theo BL_PH + unique
		String currentSid = nullToEmpty(toKhai.getIdPhatHanh());
		if (!notBlank(currentSid)) {
			String blPhPrefix = defaultIfBlank(getSystemParamValue("BL_PH"), "BL");
			String unique = toKhai.getId() + "_" + System.currentTimeMillis();
			currentSid = blPhPrefix + "_" + unique;
		}
		receipt.put("sid", currentSid);
		receipt.put("idt", "");

		// Loại, mẫu, ký hiệu từ STHAM_SO
		String blType = getSystemParamValue("BL_TYPE");
		String blForm = getSystemParamValue("BL_FORM");
		String blSerial = getSystemParamValue("BL_SERIAL");
		if (notBlank(blType)) receipt.put("type", blType);
		if (notBlank(blForm)) receipt.put("form", blForm);
		if (notBlank(blSerial)) receipt.put("serial", blSerial);

		receipt.put("seq", "");

		// Thông tin người mua
		receipt.put("bname", nullToEmpty(toKhai.getTenDoanhNghiepXNK()));
		receipt.put("btax", nullToEmpty(toKhai.getMaDoanhNghiepXNK()));
		// Các field chưa có trong StoKhai => để trống
		receipt.put("buyer", "");
		receipt.put("bcode", "");
		receipt.put("baddr", nullToEmpty(toKhai.getDiaChiXNK()));
		// Lấy bmail, btel theo SysUser (username = maDoanhNghiepXNK)
		SysUser buyerUser = null;
		try {
			if (notBlank(toKhai.getMaDoanhNghiepXNK())) {
				buyerUser = sysUserRepository.findByUsername(toKhai.getMaDoanhNghiepXNK());
			}
		} catch (Exception ignore) {}
		receipt.put("btel", buyerUser != null && notBlank(buyerUser.getPhone()) ? buyerUser.getPhone() : "");
		receipt.put("bmail", buyerUser != null && notBlank(buyerUser.getMail()) ? buyerUser.getMail() : "");

		// Thanh toán / tiền tệ
		receipt.put("paym", defaultIfBlank(toKhai.getLoaiThanhToan(), "CK")); // mặc định CK nếu null/blank
		receipt.put("curr", "VND");
		receipt.put("exrt", 1);

		// Ghi chú
		receipt.put("note", nullToEmpty(toKhai.getGhiChuKhaiPhi()));

		// Tổng tiền
		java.math.BigDecimal tongTien = toKhai.getTongTienPhi() != null ? toKhai.getTongTienPhi() : java.math.BigDecimal.ZERO;
		long tong = tongTien.longValue();
		receipt.put("sumv", tong);
		receipt.put("sum", tong);
		receipt.put("totalv", tong);
		receipt.put("total", tong);
		String tienBangChu = ConvertNumberToString.converNumToString(String.valueOf(tong));
		receipt.put("word", tienBangChu);

		// Khác
		receipt.put("aun", 2);
		receipt.put("notsendmail", 0);
		receipt.put("sendfile", 1);
		receipt.put("sec", "");

		// Items từ StoKhaiCt
		java.util.List<StoKhaiCt> chiTietList = toKhaiThongTinChiTietRepository.findByToKhaiThongTinID(toKhai.getId());
		java.util.List<java.util.Map<String, Object>> items = new java.util.ArrayList<>();
		int line = 1;
		for (StoKhaiCt ct : chiTietList) {
			java.util.Map<String, Object> item = new java.util.HashMap<>();
			item.put("line", line++);
			item.put("code", nullToEmpty(ct.getSoHieu()));
			item.put("name", nullToEmpty(toKhai.getGhiChuKhaiPhi()));
			item.put("unit", nullToEmpty(ct.getDonViTinh() != null ? ct.getDonViTinh() : ct.getLoaiCont()));
			java.math.BigDecimal donGiaCt = ct.getDonGia() != null ? ct.getDonGia() : (ct.getSoTien() != null ? ct.getSoTien() : java.math.BigDecimal.ZERO);
			java.math.BigDecimal soTien = ct.getSoTien() != null ? ct.getSoTien() : donGiaCt;
			item.put("price", donGiaCt.longValue());
			item.put("quantity", 1);
			item.put("amount", soTien.longValue());
			items.add(item);
		}
		receipt.put("items", items);

		// STAX: theo mẫu sử dụng mã số thuế
		receipt.put("stax", nullToEmpty(toKhai.getMaDoanhNghiepKhaiPhi()));

		// Có thể cần items/chi tiết phí -> truy vấn StoKhaiCt nếu FPT yêu cầu

		req.setReceipt(receipt);
		req.setToKhaiId(toKhai.getId());
		return req;
	}

	private String getSystemParamValue(String maTs) {
		try {
			SthamSo param = sthamSoService.findByMaTs(maTs);
			return param != null ? param.getGiaTri() : null;
		} catch (Exception ignore) {
			return null;
		}
	}

	private boolean notBlank(String s) {
		return s != null && !s.trim().isEmpty();
	}

	private String nullToEmpty(Object obj) {
		return obj == null ? "" : obj.toString();
	}

	private String defaultIfBlank(String value, String defaultValue) {
		return (value == null || value.trim().isEmpty()) ? defaultValue : value;
	}

	private java.math.BigDecimal getSystemParamAsBigDecimal(String maTs) {
		try {
			String v = getSystemParamValue(maTs);
			if (!notBlank(v)) return null;
			return new java.math.BigDecimal(v.trim());
		} catch (Exception e) {
			log.warn("[AUTO-TASK] Giá trị tham số {} không hợp lệ: {}", maTs, e.getMessage());
			return null;
		}
	}

	private String extractSidFromFptResponse(Object body) {
		try {
			if (body == null) return null;
			if (body instanceof String) {
				com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
				Object obj = mapper.readValue((String) body, Object.class);
				return extractSidFromObject(obj);
			}
			return extractSidFromObject(body);
		} catch (Exception e) {
			return null;
		}
	}

	private String extractIdtFromFptResponse(Object body) {
		try {
			if (body == null) return null;
			if (body instanceof String) {
				com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
				Object obj = mapper.readValue((String) body, Object.class);
				return extractFieldFromObject(obj, "idt");
			}
			return extractFieldFromObject(body, "idt");
		} catch (Exception e) {
			log.warn("[AUTO-TASK] Không trích xuất được idt từ FPT response: {}", e.getMessage());
			return null;
		}
	}

	private String extractFieldFromFptResponse(Object body, String field) {
		try {
			if (body == null) return null;
			if (body instanceof String) {
				com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
				Object obj = mapper.readValue((String) body, Object.class);
				return extractFieldFromObject(obj, field);
			}
			return extractFieldFromObject(body, field);
		} catch (Exception e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	private String extractSidFromObject(Object obj) {
		if (obj instanceof java.util.Map) {
			java.util.Map<String, Object> map = (java.util.Map<String, Object>) obj;
			Object sid = map.get("sid");
			if (sid != null) return sid.toString();
			Object data = map.get("data");
			if (data instanceof java.util.Map) {
				Object sid2 = ((java.util.Map<String, Object>) data).get("sid");
				if (sid2 != null) return sid2.toString();
			}
		}
		if (obj instanceof java.util.List) {
			for (Object item : (java.util.List<Object>) obj) {
				String sid = extractSidFromObject(item);
				if (sid != null) return sid;
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private String extractFieldFromObject(Object obj, String field) {
		if (obj instanceof java.util.Map) {
			java.util.Map<String, Object> map = (java.util.Map<String, Object>) obj;
			Object v = map.get(field);
			if (v != null) return v.toString();
			Object data = map.get("data");
			if (data instanceof java.util.Map) {
				Object v2 = ((java.util.Map<String, Object>) data).get(field);
				if (v2 != null) return v2.toString();
			}
		}
		if (obj instanceof java.util.List) {
			for (Object item : (java.util.List<Object>) obj) {
				String v = extractFieldFromObject(item, field);
				if (v != null) return v;
			}
		}
		return null;
	}

	private SBienLaiCreateRequest buildBienLaiCreateRequestFromToKhai(StoKhai toKhai, String sid, String idtStr, String form, String serial, String seq) {
		SBienLaiCreateRequest req = new SBienLaiCreateRequest();
		req.setMst(toKhai.getMaDoanhNghiepXNK());
		req.setTenDvi(toKhai.getTenDoanhNghiepXNK());
		req.setDiaChi(toKhai.getDiaChiXNK());
		SysUser buyerUser = null;
		try { if (notBlank(toKhai.getMaDoanhNghiepXNK())) buyerUser = sysUserRepository.findByUsername(toKhai.getMaDoanhNghiepXNK()); } catch (Exception ignore) {}
		req.setEmail(buyerUser != null ? buyerUser.getMail() : null);
		req.setSdt(buyerUser != null ? buyerUser.getPhone() : null);
		// mabl: form-serial-seq ; sobl: seq
		if (notBlank(form) && notBlank(serial) && notBlank(seq)) {
			req.setMaBl(form + "-" + serial + "-" + seq);
			req.setSoBl(seq);
		} else {
			// fallback: dùng dữ liệu trên StoKhai nếu có
			req.setMaBl(toKhai.getKyHieuBienLai());
			req.setSoBl(toKhai.getSoBienLai());
		}
		req.setHthucTtoan(defaultIfBlank(toKhai.getLoaiThanhToan(), "CK"));
		java.time.LocalDateTime ngayBl = parseIdtToDateTime(idtStr);
		if (ngayBl != null) req.setNgayBl(ngayBl);
		req.setStb(toKhai.getSoThongBao());
		req.setIdPhatHanh(sid);
		req.setToKhaiId(toKhai.getId());
		// Map chi tiết từ StoKhaiCt sang SBienLaiCtCreateRequest
		java.util.List<StoKhaiCt> chiTietList = toKhaiThongTinChiTietRepository.findByToKhaiThongTinID(toKhai.getId());
		java.util.List<SBienLaiCtCreateRequest> blChiTiet = new java.util.ArrayList<>();
		for (StoKhaiCt ct : chiTietList) {
			SBienLaiCtCreateRequest ctReq = new SBienLaiCtCreateRequest();
			ctReq.setNdungTp(nullToEmpty(ct.getGhiChu()));
			ctReq.setDvt(ct.getDonViTinh());
			java.math.BigDecimal donGiaCt = ct.getDonGia();
			java.math.BigDecimal soTien = ct.getSoTien();
			ctReq.setSoLuong(java.math.BigDecimal.ONE);
			ctReq.setDonGia(donGiaCt);
			ctReq.setSoTien(soTien);
			blChiTiet.add(ctReq);
		}
		req.setChiTietList(blChiTiet);
		return req;
	}

	private SearchInvoiceRequest buildSearchIcrRequest(StoKhai toKhai, String sid) {
		SearchInvoiceRequest req = new SearchInvoiceRequest();
		// User từ STHAM_SO
		SearchInvoiceRequest.UserInfo user = new SearchInvoiceRequest.UserInfo();
		user.setUsername(getSystemParamValue("BL_USER"));
		user.setPassword(getSystemParamValue("BL_PW"));
		req.setUser(user);
		// stax: dùng MST doanh nghiệp khai phí theo quy ước trước đó
		req.setStax(toKhai.getMaDoanhNghiepKhaiPhi());
		// type: nếu có cấu hình riêng có thể lấy từ BL_TYPE, mặc định null để FPT xử lý
		req.setType("pdf");
		// sid từ tạo ICR
		req.setSid(sid);
		// toKhaiId để controller xử lý và lưu base64
		req.setToKhaiId(toKhai.getId());
		return req;
	}

	private java.time.LocalDateTime parseIdtToDateTime(String idtStr) {
		if (!notBlank(idtStr)) return null;
		try {
			java.time.format.DateTimeFormatter dtf = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			return java.time.LocalDateTime.parse(idtStr, dtf);
		} catch (Exception ignore) {
			try {
				return java.time.LocalDate.parse(idtStr).atStartOfDay();
			} catch (Exception e) {
				return null;
			}
		}
	}
}


