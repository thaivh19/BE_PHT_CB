package com.pht.service.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.pht.model.request.LayThongTinHaiQuanRequest;
import com.pht.model.request.ParseHaiQuanDataRequest;
import com.pht.model.response.ChiTietHaiQuanResponse;
import com.pht.model.response.ThongTinHaiQuanResponse;
import com.pht.repository.SbieuCuocRepository;
import com.pht.service.HaiQuanService;
import com.pht.util.FileReaderUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class HaiQuanServiceImpl implements HaiQuanService {

    private final FileReaderUtil fileReaderUtil;
    private final SbieuCuocRepository sbieuCuocRepository;

    @Override
    public List<ThongTinHaiQuanResponse> layThongTinHaiQuan(LayThongTinHaiQuanRequest request) {
        log.info("Lấy thông tin hải quan cho số tờ khai: {}, mã doanh nghiệp: {}", 
                request.getSoToKhaiHaiQuan(), request.getMaDoanhNghiep());
        
        // Validate input
        if (request.getMaDoanhNghiep() == null || request.getMaDoanhNghiep().trim().isEmpty()) {
            throw new IllegalArgumentException("Mã doanh nghiệp không được để trống");
        }
        // soToKhaiHaiQuan có thể null hoặc rỗng - sẽ dùng định dạng khác
        
        try {
            // Tạo tên file XML dựa trên thông tin request
            String fileName;
            String xmlContent;
            
            // Kiểm tra có số tờ khai hải quan không
            boolean hasSoToKhaiHaiQuan = request.getSoToKhaiHaiQuan() != null && 
                                        !request.getSoToKhaiHaiQuan().trim().isEmpty();
            
            if (hasSoToKhaiHaiQuan) {
                // Có cả mã doanh nghiệp và số tờ khai → dùng định dạng: 320_{maDoanhNghiep}_{soToKhaiHaiQuan}.xml
                fileName = String.format("320_%s_%s.xml", 
                        request.getMaDoanhNghiep().trim(), 
                        request.getSoToKhaiHaiQuan().trim());
                log.info("Có số tờ khai hải quan, tìm file XML với định dạng: {}", fileName);
            } else {
                // Chỉ có mã doanh nghiệp → dùng định dạng: 320_{maDoanhNghiep}.xml
                fileName = String.format("320_%s.xml", 
                        request.getMaDoanhNghiep().trim());
                log.info("Không có số tờ khai hải quan, tìm file XML với định dạng: {}", fileName);
            }
            
            // Đọc file XML từ đường dẫn
            String filePath = "C:\\IDA\\HQ\\" + fileName;
            xmlContent = fileReaderUtil.readFileContentByPath(filePath);

            if (xmlContent == null || xmlContent.isEmpty()) {
                log.warn("Không tìm thấy dữ liệu trong file: {}", fileName);
                return new ArrayList<>(); // Trả về list rỗng
            }

            // Parse XML content thành danh sách response objects
            List<ThongTinHaiQuanResponse> danhSachToKhai = parseXmlToListResponse(xmlContent);

            // Validate dữ liệu trong XML có khớp với request không
            validateXmlDataWithRequest(danhSachToKhai, request);

            log.info("Trả về thông tin hải quan thành công với {} tờ khai từ file: {}", 
                    danhSachToKhai.size(), fileName);
            
            return danhSachToKhai;

        } catch (IOException e) {
            log.error("Lỗi khi đọc file XML: ", e);
            throw new RuntimeException("Lỗi khi đọc dữ liệu từ file XML: " + e.getMessage());
        } catch (Exception e) {
            log.error("Lỗi khi xử lý dữ liệu hải quan: ", e);
            throw new RuntimeException("Lỗi khi xử lý dữ liệu hải quan: " + e.getMessage());
        }
    }

    @Override
    public ThongTinHaiQuanResponse parseHaiQuanResponse(ParseHaiQuanDataRequest request) {
        log.info("Parse response String từ Hải quan: {}", request.getHaiQuanResponse());
        
        try {
            String xmlResponse = request.getHaiQuanResponse();
            
            // Parse XML thành danh sách và lấy tờ khai đầu tiên
            List<ThongTinHaiQuanResponse> danhSachToKhai = parseXmlToListResponse(xmlResponse);
            
            if (danhSachToKhai.isEmpty()) {
                log.warn("Không tìm thấy ThongTinChungTu nào trong XML");
                return createEmptyResponse();
            }
            
            // Trả về tờ khai đầu tiên để tương thích với API cũ
            ThongTinHaiQuanResponse response = danhSachToKhai.get(0);
            
            log.info("Parse thành công với {} chi tiết từ tờ khai đầu tiên", 
                    response.getChiTietList() != null ? response.getChiTietList().size() : 0);
            return response;
            
        } catch (Exception e) {
            log.error("Lỗi khi parse response từ Hải quan: ", e);
            throw new RuntimeException("Lỗi khi parse dữ liệu từ Hải quan: " + e.getMessage());
        }
    }

    
    /**
     * Parse XML content thành danh sách response objects (mỗi ThongTinChungTu = 1 response)
     */
    private List<ThongTinHaiQuanResponse> parseXmlToListResponse(String xmlContent) {
        List<ThongTinHaiQuanResponse> danhSachToKhai = new ArrayList<>();
        
        // Tìm tất cả các ThongTinChungTu
        Pattern pattern = Pattern.compile("<ThongTinChungTu>(.*?)</ThongTinChungTu>", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(xmlContent);
        
        int index = 1;
        while (matcher.find()) {
            String thongTinChungTuXml = matcher.group(1);
            
            ThongTinHaiQuanResponse response = parseSingleThongTinChungTu(thongTinChungTuXml, index);
            danhSachToKhai.add(response);
            
            log.info("Parse ThongTinChungTu {}: SoToKhai={}, TongTienPhi={}, SoChiTiet={}", 
                    index, response.getSoToKhai(), response.getTongTienPhi(), 
                    response.getChiTietList() != null ? response.getChiTietList().size() : 0);
            
            index++;
        }
        
        log.info("Parse XML thành công với {} ThongTinChungTu", danhSachToKhai.size());
        return danhSachToKhai;
    }
    
    /**
     * Parse một ThongTinChungTu thành response object
     */
    private ThongTinHaiQuanResponse parseSingleThongTinChungTu(String thongTinChungTuXml, int index) {
        ThongTinHaiQuanResponse response = new ThongTinHaiQuanResponse();
        
        // Thông tin chính
        response.setId((long) index);
        response.setNguonTK(1); // Lấy từ hải quan
        
        // Parse doanh nghiệp
        response.setMaDoanhNghiepKhaiPhi(extractXmlValue(thongTinChungTuXml, "Ma_DV"));
        response.setTenDoanhNghiepKhaiPhi(extractXmlValue(thongTinChungTuXml, "Ten_DV"));
        response.setDiaChiKhaiPhi(extractXmlValue(thongTinChungTuXml, "DiaChi"));
        
        // Doanh nghiệp XNK giống doanh nghiệp khai phí
        response.setMaDoanhNghiepXNK(extractXmlValue(thongTinChungTuXml, "Ma_DV"));
        response.setTenDoanhNghiepXNK(extractXmlValue(thongTinChungTuXml, "Ten_DV"));
        response.setDiaChiXNK(extractXmlValue(thongTinChungTuXml, "DiaChi"));
        
        // Parse tờ khai hải quan
        response.setSoToKhai(extractXmlValue(thongTinChungTuXml, "So_TK_HQ"));
        response.setNgayToKhai(parseDate(extractXmlValue(thongTinChungTuXml, "Ngay_TK_HQ")));
        response.setMaHaiQuan(extractXmlValue(thongTinChungTuXml, "Ma_HQ"));
        response.setMaLoaiHinh(extractXmlValue(thongTinChungTuXml, "Ma_LH"));
        response.setMaLuuKho(extractXmlValue(thongTinChungTuXml, "Ma_LuuKho"));
        response.setNuocXuatKhau(extractXmlValue(thongTinChungTuXml, "Nuoc_XK"));
        
        // THÔNG TIN HÀNG HÓA
        response.setMaPhuongThucVC(extractXmlValue(thongTinChungTuXml, "Ma_PhuongThucVC"));
        response.setPhuongTienVC(extractXmlValue(thongTinChungTuXml, "PhuongTien_VC"));
        response.setMaDiaDiemXepHang(extractXmlValue(thongTinChungTuXml, "Ma_DD_XepHang"));
        response.setMaDiaDiemDoHang(extractXmlValue(thongTinChungTuXml, "Ma_DD_DoHang"));
        response.setMaPhanLoaiHangHoa(extractXmlValue(thongTinChungTuXml, "Ma_PhanLoaiHH"));
        response.setMucDichVC(extractXmlValue(thongTinChungTuXml, "MucDich_VC"));
        
        // Parse tờ khai phí
        response.setSoTiepNhanKhaiPhi(""); // Luôn để trống
        response.setNgayKhaiPhi(LocalDate.now());
        response.setNhomLoaiPhi(extractXmlValue(thongTinChungTuXml, "Ma_LoaiPhi"));
        response.setLoaiThanhToan(extractXmlValue(thongTinChungTuXml, "Loai_ThanhToan"));
        response.setGhiChuKhaiPhi(extractXmlValue(thongTinChungTuXml, "Ten_LoaiPhi"));
        
        // Parse thông tin thu phí - tính tổng từ các ThongTinNopTien trong ThongTinChungTu này
        BigDecimal tongTienPhi = calculateTotalAmountFromThongTinChungTu(thongTinChungTuXml);
        response.setTongTienPhi(tongTienPhi);
        
        response.setTrangThaiNganHang("00");//TRANG THAI CHUA GACH NO
        response.setSoThongBaoNopPhi(""); // Chưa có trong XML
        response.setSoBienLai(""); // Chưa có trong XML
        response.setNgayBienLai(null); // Chưa có trong XML
        response.setKyHieuBienLai(""); // Chưa có trong XML
        response.setMauBienLai(""); // Chưa có trong XML
        response.setMaTraCuuBienLai(""); // Chưa có trong XML
        response.setXemBienLai(""); // Chưa có trong XML
        response.setNgayTt(null); // Ngày thanh toán - chưa có trong XML
        
        // DANH MỤC LOẠI HÀNG MIỄN PHÍ
        response.setLoaiHangMienPhi(extractXmlValue(thongTinChungTuXml, "Loai_Hang_MienPhi"));
        response.setLoaiHang(extractXmlValue(thongTinChungTuXml, "Loai_Hang"));
        response.setTrangThai("00");
        
        // Parse danh sách chi tiết từ ThongTinNopTien trong ThongTinChungTu này
        List<ChiTietHaiQuanResponse> chiTietList = parseChiTietListFromThongTinChungTu(thongTinChungTuXml, index);
        response.setChiTietList(chiTietList);
        
        return response;
    }
    
    /**
     * Tạo response rỗng khi không tìm thấy dữ liệu
     */
    private ThongTinHaiQuanResponse createEmptyResponse() {
        ThongTinHaiQuanResponse response = new ThongTinHaiQuanResponse();
        response.setId(0L);
        response.setNguonTK(1);
        response.setTrangThai("Không tìm thấy dữ liệu");
        response.setChiTietList(new ArrayList<>());
        return response;
    }
    
    
    /**
     * Extract giá trị từ XML tag
     */
    private String extractXmlValue(String xml, String tagName) {
        Pattern pattern = Pattern.compile("<" + tagName + ">(.*?)</" + tagName + ">");
        Matcher matcher = pattern.matcher(xml);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }
    
    /**
     * Parse date từ string
     */
    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (Exception e) {
            log.warn("Không thể parse date: {}", dateStr);
            return null;
        }
    }
    
    /**
     * Tính tổng số tiền từ các ThongTinNopTien trong một ThongTinChungTu
     */
    private BigDecimal calculateTotalAmountFromThongTinChungTu(String thongTinChungTuXml) {
        BigDecimal total = BigDecimal.ZERO;
        int count = 0;
        
        // Tìm tất cả các ThongTinNopTien trong ThongTinChungTu này
        Pattern pattern = Pattern.compile("<ThongTinNopTien>(.*?)</ThongTinNopTien>", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(thongTinChungTuXml);
        
        while (matcher.find()) {
            String chiTietXml = matcher.group(1);
            String thanhTienStr = extractXmlValue(chiTietXml, "Thanh_Tien");
            
            count++;
            if (thanhTienStr != null && !thanhTienStr.isEmpty()) {
                try {
                    BigDecimal thanhTien = new BigDecimal(thanhTienStr);
                    total = total.add(thanhTien);
                    log.debug("ThongTinNopTien {}: Thanh_Tien = {}", count, thanhTien);
                } catch (NumberFormatException e) {
                    log.warn("Không thể parse số tiền từ ThongTinNopTien {}: {}", count, thanhTienStr);
                }
            } else {
                log.debug("ThongTinNopTien {}: Thanh_Tien rỗng", count);
            }
        }
        
        log.debug("Tính tổng số tiền từ {} ThongTinNopTien trong ThongTinChungTu: {}", count, total);
        
        return total;
    }

    /**
     * Parse danh sách chi tiết từ ThongTinNopTien trong một ThongTinChungTu
     */
    private List<ChiTietHaiQuanResponse> parseChiTietListFromThongTinChungTu(String thongTinChungTuXml, int toKhaiIndex) {
        List<ChiTietHaiQuanResponse> chiTietList = new ArrayList<>();
        
        // Tìm tất cả các ThongTinNopTien trong ThongTinChungTu này
        Pattern pattern = Pattern.compile("<ThongTinNopTien>(.*?)</ThongTinNopTien>", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(thongTinChungTuXml);
        
        int index = 1;
        while (matcher.find()) {
            String chiTietXml = matcher.group(1);
            
            ChiTietHaiQuanResponse chiTiet = new ChiTietHaiQuanResponse();
            chiTiet.setId((long) index);
            chiTiet.setToKhaiThongTinID((long) toKhaiIndex);
            chiTiet.setSoVanDon(extractXmlValue(chiTietXml, "So_VD"));
            chiTiet.setSoHieu(extractXmlValue(chiTietXml, "So_Hieu_Container"));
            
            // Query MA_LOAI_CONT và MA_TC_CONT từ bảng SBIEU_CUOC
            String maBieuCuoc = extractXmlValue(chiTietXml, "Ma_BieuCuoc");
            if (maBieuCuoc != null && !maBieuCuoc.isEmpty()) {
                queryMaLoaiContAndMaTcCont(chiTiet, maBieuCuoc);
            }
            
            String soLuongStr = extractXmlValue(chiTietXml, "So_Luong");
            if (soLuongStr != null && !soLuongStr.isEmpty()) {
                chiTiet.setTongTrongLuong(new BigDecimal(soLuongStr));
            }
            
            chiTiet.setDonViTinh(extractXmlValue(chiTietXml, "Don_Vi_Tinh"));
            chiTiet.setGhiChu(extractXmlValue(chiTietXml, "Ten_BieuCuoc"));
            
            chiTietList.add(chiTiet);
            
            log.debug("Parse chi tiết {} trong ThongTinChungTu {}: Ma_BieuCuoc={}, So_VD={}, So_Hieu={}, So_Luong={}", 
                    index, toKhaiIndex, maBieuCuoc, chiTiet.getSoVanDon(), chiTiet.getSoHieu(), soLuongStr);
            
            index++;
        }
        
        log.debug("Parse được {} chi tiết từ ThongTinChungTu {}", chiTietList.size(), toKhaiIndex);
        return chiTietList;
    }
    
    
    /**
     * Validate dữ liệu trong XML có khớp với request không
     */
    private void validateXmlDataWithRequest(List<ThongTinHaiQuanResponse> danhSachToKhai, LayThongTinHaiQuanRequest request) {
        log.info("Validate dữ liệu XML với request - maDoanhNghiep: {}, soToKhaiHaiQuan: {}", 
                request.getMaDoanhNghiep(), request.getSoToKhaiHaiQuan());
        
        // Kiểm tra mã doanh nghiệp cho tất cả tờ khai
        for (ThongTinHaiQuanResponse response : danhSachToKhai) {
            if (response.getMaDoanhNghiepKhaiPhi() != null && 
                !response.getMaDoanhNghiepKhaiPhi().equals(request.getMaDoanhNghiep().trim())) {
                log.warn("Mã doanh nghiệp trong XML ('{}') không khớp với request ('{}') cho tờ khai {}", 
                        response.getMaDoanhNghiepKhaiPhi(), request.getMaDoanhNghiep(), response.getSoToKhai());
            }
        }
        
        // Kiểm tra số tờ khai hải quan (chỉ kiểm tra tờ khai đầu tiên)
        if (!danhSachToKhai.isEmpty() && request.getSoToKhaiHaiQuan() != null) {
            ThongTinHaiQuanResponse firstResponse = danhSachToKhai.get(0);
            if (firstResponse.getSoToKhai() != null && 
                !firstResponse.getSoToKhai().equals(request.getSoToKhaiHaiQuan().trim())) {
                log.warn("Số tờ khai trong XML ('{}') không khớp với request ('{}')", 
                        firstResponse.getSoToKhai(), request.getSoToKhaiHaiQuan());
            }
        }
        
        log.info("Validation hoàn tất cho {} tờ khai", danhSachToKhai.size());
    }
    
    /**
     * Query MA_LOAI_CONT và MA_TC_CONT từ bảng SBIEU_CUOC theo mã biểu cước
     */
    private void queryMaLoaiContAndMaTcCont(ChiTietHaiQuanResponse chiTiet, String maBieuCuoc) {
        try {
            log.info("Query MA_LOAI_CONT và MA_TC_CONT cho mã biểu cước: '{}'", maBieuCuoc);
            
            // Query từ SBIEU_CUOC theo mã biểu cước chính xác
            List<com.pht.entity.SbieuCuoc> bieuCuocList = sbieuCuocRepository.findByMaBieuCuoc(maBieuCuoc);
            
            log.info("Kết quả query: {} biểu cước tìm được cho mã: '{}'", bieuCuocList.size(), maBieuCuoc);
            
            if (!bieuCuocList.isEmpty()) {
                com.pht.entity.SbieuCuoc bieuCuoc = bieuCuocList.get(0);
                chiTiet.setMaLoaiCont(bieuCuoc.getMaLoaiCont());
                chiTiet.setMaTcCont(bieuCuoc.getMaTcCont());
                
                log.info("Tìm thấy MA_LOAI_CONT: '{}', MA_TC_CONT: '{}' cho mã biểu cước: '{}'", 
                        bieuCuoc.getMaLoaiCont(), bieuCuoc.getMaTcCont(), maBieuCuoc);
            } else {
                log.warn("Không tìm thấy biểu cước với mã: '{}'", maBieuCuoc);
                
                // Debug: Kiểm tra tất cả biểu cước có trong database
                log.info("Debug: Kiểm tra tất cả biểu cước trong database...");
                List<com.pht.entity.SbieuCuoc> allBieuCuoc = sbieuCuocRepository.findAllActive();
                log.info("Debug: Tìm thấy {} biểu cước với trạng thái = '1'", allBieuCuoc.size());
                for (com.pht.entity.SbieuCuoc bc : allBieuCuoc) {
                    log.info("Debug: maBieuCuoc='{}', maLoaiCont='{}', maTcCont='{}', trangThai='{}'", 
                            bc.getMaBieuCuoc(), bc.getMaLoaiCont(), bc.getMaTcCont(), bc.getTrangThai());
                }
                
                chiTiet.setMaLoaiCont("");
                chiTiet.setMaTcCont("");
            }
            
        } catch (Exception e) {
            log.error("Lỗi khi query MA_LOAI_CONT và MA_TC_CONT cho mã biểu cước: '{}'", maBieuCuoc, e);
            chiTiet.setMaLoaiCont("");
            chiTiet.setMaTcCont("");
        }
    }
}