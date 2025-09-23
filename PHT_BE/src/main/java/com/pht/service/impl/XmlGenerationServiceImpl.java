package com.pht.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pht.service.DatabaseCertificateService;
import com.pht.service.PfxCertificateService;
import com.pht.entity.StoKhai;
import com.pht.entity.StoKhaiCt;
import com.pht.exception.BusinessException;
import com.pht.repository.ToKhaiThongTinRepository;
import com.pht.service.XmlGenerationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class XmlGenerationServiceImpl implements XmlGenerationService {

    private final ToKhaiThongTinRepository toKhaiThongTinRepository;
    private final DatabaseCertificateService databaseCertificateService;
    private final PfxCertificateService pfxCertificateService;

    @Override
    @Transactional
    public String generateAndSaveXml(Long toKhaiId, Integer lanKy, String serialNumber) throws BusinessException {
        log.info("Bắt đầu tạo XML cho tờ khai ID: {}, lần ký: {}, serial number: {}", toKhaiId, lanKy, serialNumber);
        
        try {
            // Lấy thông tin tờ khai
            StoKhai toKhai = toKhaiThongTinRepository.findById(toKhaiId)
                    .orElseThrow(() -> new BusinessException("Không tìm thấy tờ khai với ID: " + toKhaiId));
            
            // Kiểm tra trạng thái hiện tại
            if (!"00".equals(toKhai.getTrangThai()) && lanKy == 1) {
                throw new BusinessException("Tờ khai không ở trạng thái có thể tạo XML. Trạng thái hiện tại: " + toKhai.getTrangThai());
            }
            
            // Tạo XML
            String xmlContent = generateXml(toKhai);
            
            // Ký XML với chữ ký số từ file PFX
            log.info("Ký XML với chữ ký số từ file PFX");
            xmlContent = pfxCertificateService.signXmlWithPfxCertificate(xmlContent, null, null);
            log.info("Hoàn thành ký XML với chữ ký số từ file PFX");
            
            // Log nội dung XML để kiểm tra
            log.info("=== XML CONTENT GENERATED ===");
            log.info("ToKhai ID: {}, LanKy: {}, SerialNumber: {}", toKhaiId, lanKy, serialNumber);
            log.info("XML Content:\n{}", xmlContent);
            log.info("=== END XML CONTENT ===");
            
            // Lưu XML vào trường tương ứng và cập nhật trạng thái dựa trên lanKy
            if (lanKy != null && lanKy == 1) {
                toKhai.setKylan1Xml(xmlContent);
                toKhai.setTrangThai("01");
                log.info("Lưu XML vào KYLAN1_XML và cập nhật trạng thái sang 01 cho tờ khai ID: {}", toKhaiId);
            } else {
                toKhai.setKylan2Xml(xmlContent);
                toKhai.setTrangThai("03");
                log.info("Lưu XML vào KYLAN2_XML và cập nhật trạng thái sang 03 cho tờ khai ID: {}", toKhaiId);
            }
            
            // Lưu vào database
            toKhaiThongTinRepository.save(toKhai);
            
            String trangThaiMoi = (lanKy != null && lanKy == 1) ? "01" : "03";
            log.info("Tạo XML thành công cho tờ khai ID: {}, lần ký: {}, serial number: {}, trạng thái mới: {}", 
                    toKhaiId, lanKy, serialNumber, trangThaiMoi);
            
            return xmlContent;
            
        } catch (BusinessException e) {
            log.error("Lỗi khi tạo XML: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Lỗi không xác định khi tạo XML: ", e);
            throw new BusinessException("Lỗi hệ thống khi tạo XML: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public String generateAndSaveXml(Long toKhaiId, Integer lanKy) throws BusinessException {
        log.info("Bắt đầu tạo XML cho tờ khai ID: {}, lần ký: {}", toKhaiId, lanKy);
        
        try {
            // Lấy thông tin tờ khai
            StoKhai toKhai = toKhaiThongTinRepository.findById(toKhaiId)
                    .orElseThrow(() -> new BusinessException("Không tìm thấy tờ khai với ID: " + toKhaiId));
            
            // Kiểm tra trạng thái hiện tại
            if (!"00".equals(toKhai.getTrangThai())) {
                throw new BusinessException("Tờ khai không ở trạng thái có thể tạo XML. Trạng thái hiện tại: " + toKhai.getTrangThai());
            }
            
            // Tạo XML
            String xmlContent = generateXml(toKhai);
            
            // Log nội dung XML để kiểm tra
            log.info("=== XML CONTENT GENERATED ===");
            log.info("ToKhai ID: {}, LanKy: {}", toKhaiId, lanKy);
            log.info("XML Content:\n{}", xmlContent);
            log.info("=== END XML CONTENT ===");
            
            // Lưu XML vào trường tương ứng và cập nhật trạng thái dựa trên lanKy
            if (lanKy != null && lanKy == 1) {
                toKhai.setKylan1Xml(xmlContent);
                toKhai.setTrangThai("01");
                log.info("Lưu XML vào KYLAN1_XML và cập nhật trạng thái sang 01 cho tờ khai ID: {}", toKhaiId);
            } else {
                toKhai.setKylan2Xml(xmlContent);
                toKhai.setTrangThai("03");
                log.info("Lưu XML vào KYLAN2_XML và cập nhật trạng thái sang 03 cho tờ khai ID: {}", toKhaiId);
            }
            
            // Lưu vào database
            toKhaiThongTinRepository.save(toKhai);
            
            String trangThaiMoi = (lanKy != null && lanKy == 1) ? "01" : "03";
            log.info("Tạo XML thành công cho tờ khai ID: {}, lần ký: {}, trạng thái mới: {}", toKhaiId, lanKy, trangThaiMoi);
            
            return xmlContent;
            
        } catch (BusinessException e) {
            log.error("Lỗi khi tạo XML: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Lỗi không xác định khi tạo XML: ", e);
            throw new BusinessException("Lỗi hệ thống khi tạo XML: " + e.getMessage());
        }
    }

    @Override
    public String generateXml(StoKhai toKhai) throws BusinessException {
        try {
            log.info("Bắt đầu tạo XML cho tờ khai ID: {}, Số tờ khai: {}", toKhai.getId(), toKhai.getSoToKhai());
            log.info("Thông tin tờ khai - Mã DN khai phí: {}, Tên DN: {}, Tổng tiền phí: {}", 
                    toKhai.getMaDoanhNghiepKhaiPhi(), toKhai.getTenDoanhNghiepKhaiPhi(), toKhai.getTongTienPhi());
            log.info("Số chi tiết: {}", toKhai.getChiTietList() != null ? toKhai.getChiTietList().size() : 0);
            
            StringBuilder xml = new StringBuilder();
            
            // XML Header
            xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            xml.append("<Customs>");
            
            // Header section
            xml.append("<Header>");
            xml.append("<Application_Name>Payment</Application_Name>");
            xml.append("<Application_Version>3.0</Application_Version>");
            xml.append("<Sender_Code>HQ</Sender_Code>");
            xml.append("<Sender_Name>Tổng cục Hải quan</Sender_Name>");
            xml.append("<Message_Version>3.0</Message_Version>");
            xml.append("<Message_Type>320</Message_Type>");
            xml.append("<Message_Name>Thông điệp gửi sang HQ</Message_Name>");
            xml.append("<Transaction_Date>").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))).append("</Transaction_Date>");
            xml.append("<Transaction_ID>").append(UUID.randomUUID().toString()).append("</Transaction_ID>");
            xml.append("<Request_ID>").append(UUID.randomUUID().toString().toUpperCase()).append("</Request_ID>");
            xml.append("</Header>");
            
            // Data section
            xml.append("<Data>");
            xml.append("<ThongTinChungTu>");
            
            // Thông tin chung từ tờ khai
            xml.append("<ID_CT>").append(escapeXml(toKhai.getSoTiepNhanKhaiPhi() != null ? toKhai.getSoTiepNhanKhaiPhi() : "PHT_" + toKhai.getId())).append("</ID_CT>");
            xml.append("<So_CT>").append(escapeXml(toKhai.getSoToKhai() != null ? toKhai.getSoToKhai() : String.valueOf(toKhai.getId()))).append("</So_CT>");
            xml.append("<KyHieu_CT>").append(escapeXml(toKhai.getKyHieuBienLai() != null ? toKhai.getKyHieuBienLai() : "PHT")).append("</KyHieu_CT>");
            xml.append("<Ngay_CT>").append(toKhai.getNgayToKhai() != null ? toKhai.getNgayToKhai().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))).append("</Ngay_CT>");
            
            // Thông tin doanh nghiệp
            xml.append("<Ma_DV>").append(escapeXml(toKhai.getMaDoanhNghiepKhaiPhi() != null ? toKhai.getMaDoanhNghiepKhaiPhi() : "")).append("</Ma_DV>");
            xml.append("<Ten_DV>").append(escapeXml(toKhai.getTenDoanhNghiepKhaiPhi() != null ? toKhai.getTenDoanhNghiepKhaiPhi() : "")).append("</Ten_DV>");
            xml.append("<Chuong_NS>000</Chuong_NS>");
            xml.append("<TieuMuc>2267</TieuMuc>");
            xml.append("<DiaChi>").append(escapeXml(toKhai.getDiaChiKhaiPhi() != null ? toKhai.getDiaChiKhaiPhi() : "")).append("</DiaChi>");
            
            // Thông tin phí
            xml.append("<Ma_LoaiPhi>").append(escapeXml(toKhai.getNhomLoaiPhi() != null ? toKhai.getNhomLoaiPhi() : "PHT01")).append("</Ma_LoaiPhi>");
            xml.append("<Ten_LoaiPhi>Phí sử dụng kết cấu hạ tầng cảng biển</Ten_LoaiPhi>");
            xml.append("<Ma_DV_ThuPhi>31</Ma_DV_ThuPhi>");
            xml.append("<Ma_CQT_DV_ThuPhi>STCHP</Ma_CQT_DV_ThuPhi>");
            xml.append("<Ten_DV_ThuPhi>TP Hồ Chí Minh</Ten_DV_ThuPhi>");
            
            // Thông tin hải quan
            xml.append("<So_TK_HQ>").append(escapeXml(toKhai.getSoToKhai() != null ? toKhai.getSoToKhai() : "")).append("</So_TK_HQ>");
            xml.append("<Ma_LH>").append(escapeXml(toKhai.getMaLoaiHinh() != null ? toKhai.getMaLoaiHinh() : "A11")).append("</Ma_LH>");
            xml.append("<Ngay_TK_HQ>").append(toKhai.getNgayToKhai() != null ? toKhai.getNgayToKhai().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))).append("</Ngay_TK_HQ>");
            xml.append("<Ma_HQ>").append(escapeXml(toKhai.getMaHaiQuan() != null ? toKhai.getMaHaiQuan() : "03CC")).append("</Ma_HQ>");
            
            // Thông tin nộp phí
            xml.append("<So_TK_NP>").append(escapeXml(toKhai.getSoTiepNhanKhaiPhi() != null ? toKhai.getSoTiepNhanKhaiPhi() : "")).append("</So_TK_NP>");
            xml.append("<Ngay_TK_NP>").append(toKhai.getNgayKhaiPhi() != null ? toKhai.getNgayKhaiPhi().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))).append("</Ngay_TK_NP>");
            
            // Thông tin kho bạc
            xml.append("<TKKB>351101071070</TKKB>");
            xml.append("<Ten_TKKB>Phòng Tàichính – Kế hoạch quận Hải An</Ten_TKKB>");
            xml.append("<Ma_KB>0063</Ma_KB>");
            xml.append("<Ten_KB>Kho bạc Nhà nước Hải An</Ten_KB>");
            
            // Tổng tiền
            xml.append("<SoTien_TO>").append(toKhai.getTongTienPhi() != null ? toKhai.getTongTienPhi().toString() : "0").append("</SoTien_TO>");
            
            // Diễn giải
            String dienGiai = String.format("ID_CT:%s;LP:%s;DVNP:%s;DVTP:%s;MA_CQT:STCHP;TM:2267;ST:%s;", 
                    toKhai.getId(),
                    toKhai.getNhomLoaiPhi() != null ? toKhai.getNhomLoaiPhi() : "PHT01",
                    toKhai.getMaDoanhNghiepKhaiPhi() != null ? toKhai.getMaDoanhNghiepKhaiPhi() : "",
                    toKhai.getMaDoanhNghiepXNK() != null ? toKhai.getMaDoanhNghiepXNK() : "",
                    toKhai.getTongTienPhi() != null ? toKhai.getTongTienPhi().toString() : "0");
            xml.append("<DienGiai>").append(escapeXml(dienGiai)).append("</DienGiai>");
            
            // Thông tin chi tiết nộp tiền
            if (toKhai.getChiTietList() != null && !toKhai.getChiTietList().isEmpty()) {
                int soTT = 1;
                for (StoKhaiCt chiTiet : toKhai.getChiTietList()) {
                    xml.append("<ThongTinNopTien>");
                    xml.append("<SoTT>").append(soTT).append("</SoTT>");
                    xml.append("<Ma_BieuCuoc>").append(escapeXml(chiTiet.getLoaiCont() != null ? chiTiet.getLoaiCont() : "")).append("</Ma_BieuCuoc>");
                    xml.append("<Ten_BieuCuoc>Container 20feet hàngkhô</Ten_BieuCuoc>");
                    xml.append("<So_VD>").append(escapeXml(chiTiet.getSoVanDon() != null ? chiTiet.getSoVanDon() : "")).append("</So_VD>");
                    xml.append("<So_Hieu_Container>").append(escapeXml(chiTiet.getSoHieu() != null ? chiTiet.getSoHieu() : "")).append("</So_Hieu_Container>");
                    // xml.append("<Don_Gia>250000</Don_Gia>");
                    // xml.append("<So_Luong>1</So_Luong>");
                    // xml.append("<Don_Vi_Tinh>Đồng/Container</Don_Vi_Tinh>");
                    // xml.append("<Thanh_Tien>250000</Thanh_Tien>");
                    xml.append("<Don_Vi_Tinh>").append(escapeXml(chiTiet.getDonViTinh() != null ? chiTiet.getDonViTinh() : "")).append("</Don_Vi_Tinh>");
                    xml.append("</ThongTinNopTien>");
                    soTT++;
                }
            } else {
                // Tạo thông tin mặc định nếu không có chi tiết

            }
            
            xml.append("</ThongTinChungTu>");
            xml.append("</Data>");
            
            // Signature sẽ được thêm bởi DatabaseCertificateService nếu có chữ ký số
            xml.append("</Customs>");
            
            String result = xml.toString();
            log.info("Hoàn thành tạo XML cho tờ khai ID: {}, độ dài XML: {} ký tự", toKhai.getId(), result.length());
            
            return result;
            
        } catch (Exception e) {
            log.error("Lỗi khi tạo XML: ", e);
            throw new BusinessException("Lỗi khi tạo XML: " + e.getMessage());
        }
    }
    
    /**
     * Escape XML special characters
     */
    private String escapeXml(String input) {
        if (input == null) {
            return "";
        }
        return input.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&apos;");
    }
}
