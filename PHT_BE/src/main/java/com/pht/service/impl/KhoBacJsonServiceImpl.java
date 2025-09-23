package com.pht.service.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pht.entity.SDoiSoat;
import com.pht.entity.SDoiSoatCt;
import com.pht.entity.SDoiSoatCtLech;
import com.pht.repository.SDoiSoatCtLechRepository;
import com.pht.repository.SDoiSoatRepository;
import com.pht.service.KhoBacJsonService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class KhoBacJsonServiceImpl implements KhoBacJsonService {

    @Autowired
    private SDoiSoatRepository doiSoatRepository;
    
    @Autowired
    private SDoiSoatCtLechRepository doiSoatCtLechRepository;
    
    private static final String KB_FOLDER_PATH = "C:\\IDA\\KB";
    private static final Random random = new Random();

    @Override
    public String taoJsonGuiKhoBac(SDoiSoat doiSoat, List<SDoiSoatCt> chiTietList) {
        log.info("Tạo JSON gửi kho bạc cho đối soát ID: {}", doiSoat.getId());
        
        try {
            StringBuilder json = new StringBuilder();
            json.append("{\n");
            json.append("  \"thongTinDoiSoat\": {\n");
            json.append("    \"id\": ").append(doiSoat.getId()).append(",\n");
            json.append("    \"soBk\": \"").append(doiSoat.getSoBk()).append("\",\n");
            json.append("    \"ngayBk\": \"").append(doiSoat.getNgayBk()).append("\",\n");
            json.append("    \"ngayDs\": \"").append(doiSoat.getNgayDs()).append("\",\n");
            json.append("    \"tongSo\": ").append(doiSoat.getTongSo()).append(",\n");
            json.append("    \"tongTien\": ").append(doiSoat.getTongTien()).append(",\n");
            json.append("    \"lanDs\": ").append(doiSoat.getLanDs()).append("\n");
            json.append("  },\n");
            json.append("  \"chiTietDoiSoat\": [\n");
            
            for (int i = 0; i < chiTietList.size(); i++) {
                SDoiSoatCt chiTiet = chiTietList.get(i);
                json.append("    {\n");
                json.append("      \"id\": ").append(chiTiet.getId()).append(",\n");
                json.append("      \"soToKhai\": \"").append(chiTiet.getSoToKhai()).append("\",\n");
                json.append("      \"ngayToKhai\": \"").append(chiTiet.getNgayToKhai()).append("\",\n");
                json.append("      \"soTnKp\": \"").append(chiTiet.getSoTnKp()).append("\",\n");
                json.append("      \"ngayTnKp\": \"").append(chiTiet.getNgayTnKp()).append("\",\n");
                json.append("      \"maDoanhNghiep\": \"").append(chiTiet.getMaDoanhNghiep()).append("\",\n");
                json.append("      \"tenDoanhNghiep\": \"").append(chiTiet.getTenDoanhNghiep()).append("\",\n");
                json.append("      \"tongTienPhi\": ").append(chiTiet.getTongTienPhi()).append("\n");
                json.append("    }");
                if (i < chiTietList.size() - 1) {
                    json.append(",");
                }
                json.append("\n");
            }
            
            json.append("  ]\n");
            json.append("}");
            
            String jsonContent = json.toString();
            log.info("Tạo JSON thành công, độ dài: {} ký tự", jsonContent.length());
            
            // TODO: Cần thêm cột DS_DS vào entity SDoiSoat để lưu JSON gửi đi
            // doiSoat.setDsDs(jsonContent);
            // doiSoatRepository.save(doiSoat);
            log.info("JSON đã được tạo cho đối soát ID: {}", doiSoat.getId());
            
            return jsonContent;
            
        } catch (Exception e) {
            log.error("Lỗi khi tạo JSON gửi kho bạc: ", e);
            throw new RuntimeException("Lỗi khi tạo JSON gửi kho bạc: " + e.getMessage());
        }
    }

    @Override
    public void xuLyJsonPhanHoiTuKhoBac(SDoiSoat doiSoat, String jsonPhanHoi) {
        log.info("Xử lý JSON phản hồi từ kho bạc cho đối soát ID: {}", doiSoat.getId());
        
        try {
            // Giả lập đọc JSON từ thư mục C:\IDA\KB
            String jsonContent = docJsonTuThuMucKB(doiSoat.getId());
            
            if (jsonContent == null || jsonContent.isEmpty()) {
                log.warn("Không tìm thấy JSON phản hồi từ kho bạc");
                capNhatTrangThaiDoiSoat(doiSoat, "99", "Không tìm thấy JSON phản hồi", "2", null);
                return;
            }
            
            // Phân tích JSON phản hồi
            if (jsonContent.contains("\"trangThai\": \"KHOP\"")) {
                // JSON khớp: trạng thái = 1, check_send_kb = 1
                log.info("JSON phản hồi: KHỚP");
                capNhatTrangThaiDoiSoat(doiSoat, "1", "Đối soát khớp với kho bạc", "1", jsonContent);
                
            } else if (jsonContent.contains("\"trangThai\": \"LECH\"")) {
                // JSON lệch: trạng thái = 0, check_send_kb = 1
                log.info("JSON phản hồi: LỆCH");
                xuLyChiTietLech(doiSoat, jsonContent);
                capNhatTrangThaiDoiSoat(doiSoat, "0", "Đối soát lệch với kho bạc", "1", jsonContent);
                
            } else {
                log.warn("JSON phản hồi không xác định được trạng thái");
                capNhatTrangThaiDoiSoat(doiSoat, "99", "JSON phản hồi không hợp lệ", "2", jsonContent);
            }
            
        } catch (Exception e) {
            log.error("Lỗi khi xử lý JSON phản hồi từ kho bạc: ", e);
            capNhatTrangThaiDoiSoat(doiSoat, "99", "Lỗi xử lý JSON: " + e.getMessage(), "2", null);
        }
    }

    private String docJsonTuThuMucKB(Long doiSoatId) {
        try {
            // Tạo tên file JSON phản hồi
            String fileName = String.format("KB_PHAN_HOI_%d.json", doiSoatId);
            Path filePath = Paths.get(KB_FOLDER_PATH, fileName);
            
            if (Files.exists(filePath)) {
                String content = Files.readString(filePath);
                log.info("Đọc JSON phản hồi từ file: {}", fileName);
                return content;
            } else {
                log.warn("Không tìm thấy file JSON phản hồi: {}", fileName);
                return null;
            }
            
        } catch (IOException e) {
            log.error("Lỗi khi đọc JSON từ thư mục KB: ", e);
            return null;
        }
    }

    private void xuLyChiTietLech(SDoiSoat doiSoat, String jsonContent) {
        log.info("Xử lý chi tiết lệch cho đối soát ID: {}", doiSoat.getId());
        
        try {
            // Giả lập tạo chi tiết lệch
            // Trong thực tế sẽ parse JSON để lấy thông tin lệch
            
            // Lấy danh sách chi tiết đối soát
            List<SDoiSoatCt> chiTietList = doiSoatRepository.findById(doiSoat.getId())
                    .map(s -> s.getChiTietList())
                    .orElse(List.of());
            
            // Tạo các loại lệch mẫu
            for (int i = 0; i < Math.min(3, chiTietList.size()); i++) {
                SDoiSoatCt chiTiet = chiTietList.get(i);
                
                // Tạo lệch tiền
                BigDecimal giaTriHeThong = chiTiet.getTongTienPhi();
                BigDecimal giaTriKhoBac = giaTriHeThong.multiply(new BigDecimal("0.95")); // Giảm 5%
                
                luuChiTietLech(
                    doiSoat.getId(),
                    chiTiet.getId(),
                    chiTiet.getSoToKhai(),
                    "TIEN",
                    "Lệch tiền phí",
                    giaTriHeThong,
                    giaTriKhoBac,
                    "THIEU"
                );
                
                // Tạo lệch thông tin
                if (random.nextBoolean()) {
                    luuChiTietLech(
                        doiSoat.getId(),
                        chiTiet.getId(),
                        chiTiet.getSoToKhai(),
                        "THONG_TIN",
                        "Thông tin doanh nghiệp không khớp",
                        BigDecimal.ZERO,
                        BigDecimal.ZERO,
                        "KHAC"
                    );
                }
            }
            
            // Tạo lệch thiếu tờ khai (tờ khai có trong kho bạc nhưng không có trong hệ thống)
            taoLechThieuToKhai(doiSoat);
            
            log.info("Đã tạo {} chi tiết lệch", doiSoatCtLechRepository.findByDoiSoatId(doiSoat.getId()).size());
            
        } catch (Exception e) {
            log.error("Lỗi khi xử lý chi tiết lệch: ", e);
        }
    }
    
    /**
     * Tạo lệch thiếu tờ khai - tờ khai có trong kho bạc nhưng không có trong hệ thống
     */
    private void taoLechThieuToKhai(SDoiSoat doiSoat) {
        log.info("Tạo lệch thiếu tờ khai cho đối soát ID: {}", doiSoat.getId());
        
        try {
            // Giả lập một số tờ khai có trong kho bạc nhưng không có trong hệ thống
            String[] soToKhaiThieu = {
                "KB_THIEU_001", 
                "KB_THIEU_002", 
                "KB_THIEU_003"
            };
            
            for (String soToKhai : soToKhaiThieu) {
                luuChiTietLech(
                    doiSoat.getId(),
                    null, // Không có doiSoatCtId vì tờ khai này không tồn tại trong hệ thống
                    soToKhai,
                    "THIEU_TO_KHAI",
                    "Tờ khai có trong kho bạc nhưng không có trong hệ thống",
                    BigDecimal.ZERO, // Hệ thống không có
                    new BigDecimal("2500000"), // Kho bạc có
                    "THIEU"
                );
            }
            
            log.info("Đã tạo {} lệch thiếu tờ khai", soToKhaiThieu.length);
            
        } catch (Exception e) {
            log.error("Lỗi khi tạo lệch thiếu tờ khai: ", e);
        }
    }

    @Override
    public void luuChiTietLech(Long doiSoatId, Long doiSoatCtId, String soToKhai, 
                               String loaiLech, String moTaLech, 
                               BigDecimal giaTriHeThong, 
                               BigDecimal giaTriKhoBac, 
                               String cheDoLech) {
        
        SDoiSoatCtLech chiTietLech = new SDoiSoatCtLech();
        chiTietLech.setDoiSoatId(doiSoatId);
        chiTietLech.setDoiSoatCtId(doiSoatCtId);
        chiTietLech.setSoToKhai(soToKhai);
        chiTietLech.setLoaiLech(loaiLech);
        chiTietLech.setMoTaLech(moTaLech);
        chiTietLech.setGiaTriHeThong(giaTriHeThong);
        chiTietLech.setGiaTriKhoBac(giaTriKhoBac);
        chiTietLech.setCheDoLech(cheDoLech);
        chiTietLech.setNgayTao(LocalDateTime.now());
        
        doiSoatCtLechRepository.save(chiTietLech);
        log.debug("Đã lưu chi tiết lệch: {} - {}", soToKhai, moTaLech);
    }

    @Override
    public void capNhatTrangThaiDoiSoat(SDoiSoat doiSoat, String trangThai, String msgOutbox) {
        capNhatTrangThaiDoiSoat(doiSoat, trangThai, msgOutbox, "0");
    }
    
    public void capNhatTrangThaiDoiSoat(SDoiSoat doiSoat, String trangThai, String msgOutbox, String checkSendKb) {
        capNhatTrangThaiDoiSoat(doiSoat, trangThai, msgOutbox, checkSendKb, null);
    }
    
    public void capNhatTrangThaiDoiSoat(SDoiSoat doiSoat, String trangThai, String msgOutbox, String checkSendKb, String jsonPhanHoi) {
        doiSoat.setTrangThai(trangThai);
        // TODO: Cần thêm các cột sau vào entity SDoiSoat:
        // - MSG_OUTBOX: Lưu thông báo gửi đi
        // - CHECK_SEND_KB: Trạng thái gửi kho bạc
        // - KQ_DS: Kết quả đối soát (JSON phản hồi)
        // doiSoat.setMsgOutbox(msgOutbox);
        // doiSoat.setCheckSendKb(checkSendKb);
        // doiSoat.setKqDs(jsonPhanHoi);
        
        doiSoatRepository.save(doiSoat);
        log.info("Đã cập nhật trạng thái đối soát ID: {} thành: {}, msgOutbox: {}, checkSendKb: {}", 
                doiSoat.getId(), trangThai, msgOutbox, checkSendKb);
    }
}
