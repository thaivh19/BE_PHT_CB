package com.pht.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.pht.entity.ChukySo;
import com.pht.entity.SDonHang;
import com.pht.exception.BusinessException;
import com.pht.repository.ChukySoRepository;
import com.pht.service.DatabaseCertificateService;
import com.pht.service.DonHangKySoService;
import com.pht.service.PfxCertificateService;
import com.pht.service.SDonHangService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DonHangKySoServiceImpl implements DonHangKySoService {

    private final SDonHangService sDonHangService;
    private final ChukySoRepository chukySoRepository;
    private final DatabaseCertificateService databaseCertificateService;
    private final PfxCertificateService pfxCertificateService;

    @Override
    @Transactional
    public String kySoDonHang(Long idDonHang, String serialNumber) throws BusinessException {
        log.info("Bắt đầu ký số đơn hàng ID: {} với serial number: {}", idDonHang, serialNumber);

        try {
            // Kiểm tra đơn hàng tồn tại
            SDonHang donHang = sDonHangService.findById(idDonHang);
            if (donHang == null) {
                throw new BusinessException("Không tìm thấy đơn hàng với ID: " + idDonHang);
            }

            // Kiểm tra chữ ký số tồn tại
            if (!StringUtils.hasText(serialNumber)) {
                throw new BusinessException("Serial number không được để trống");
            }

            ChukySo chuKySo = chukySoRepository.findBySerialNumber(serialNumber)
                    .orElseThrow(() -> new BusinessException("Không tìm thấy chữ ký số với serial number: " + serialNumber));

            // Kiểm tra chữ ký số có hợp lệ không
            if (!chuKySo.getIsActive()) {
                throw new BusinessException("Chữ ký số không còn hoạt động");
            }

            // Tạo XML cho đơn hàng
            String xmlContent = generateXmlForDonHang(donHang);
            
            // Ký XML với chữ ký số từ file PFX
            log.info("Ký XML với chữ ký số từ file PFX");
            xmlContent = pfxCertificateService.signXmlWithPfxCertificate(xmlContent, null, null);
            log.info("Hoàn thành ký XML với chữ ký số từ file PFX");
            
            // Log nội dung XML để kiểm tra
            log.info("=== XML CONTENT GENERATED ===");
            log.info("DonHang ID: {}, SerialNumber: {}", idDonHang, serialNumber);
            log.info("XML Content:\n{}", xmlContent);
            log.info("=== END XML CONTENT ===");
            
            // Cập nhật XML ký vào đơn hàng
            donHang.setXmlKy(xmlContent);
            sDonHangService.save(donHang);

            log.info("Ký số thành công cho đơn hàng ID: {} với serial number: {}", idDonHang, serialNumber);
            
            return xmlContent;
            
        } catch (BusinessException e) {
            log.error("Lỗi khi ký số đơn hàng: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Lỗi không xác định khi ký số đơn hàng: ", e);
            throw new BusinessException("Lỗi hệ thống khi ký số đơn hàng: " + e.getMessage());
        }
    }

    /**
     * Tạo XML cho đơn hàng (trước khi ký)
     */
    private String generateXmlForDonHang(SDonHang donHang) throws BusinessException {
        try {
            log.info("Bắt đầu tạo XML cho đơn hàng ID: {}, Số đơn hàng: {}", donHang.getId(), donHang.getSoDonHang());
            log.info("Thông tin đơn hàng - MST: {}, Tên DN: {}, Tổng tiền: {}", 
                    donHang.getMst(), donHang.getTenDn(), donHang.getTongTien());
            
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
            xml.append("<Message_Type>321</Message_Type>");
            xml.append("<Message_Name>Thông điệp gửi sang HQ</Message_Name>");
            xml.append("<Transaction_Date>").append(java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))).append("</Transaction_Date>");
            xml.append("<Transaction_ID>").append(java.util.UUID.randomUUID().toString()).append("</Transaction_ID>");
            xml.append("<Request_ID>").append(java.util.UUID.randomUUID().toString().toUpperCase()).append("</Request_ID>");
            xml.append("</Header>");
            
            // Data section
            xml.append("<Data>");
            xml.append("<ThongTinDonHang>");
            
            // Thông tin đơn hàng
            xml.append("<ID>").append(escapeXml(String.valueOf(donHang.getId()))).append("</ID>");
            xml.append("<SoDonHang>").append(escapeXml(donHang.getSoDonHang() != null ? donHang.getSoDonHang() : "")).append("</SoDonHang>");
            xml.append("<MST>").append(escapeXml(donHang.getMst() != null ? donHang.getMst() : "")).append("</MST>");
            xml.append("<TenDN>").append(escapeXml(donHang.getTenDn() != null ? donHang.getTenDn() : "")).append("</TenDN>");
            xml.append("<NgayDonHang>").append(donHang.getNgayDonHang() != null ? donHang.getNgayDonHang().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")) : java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))).append("</NgayDonHang>");
            xml.append("<TongTien>").append(donHang.getTongTien() != null ? donHang.getTongTien().toString() : "0").append("</TongTien>");
            xml.append("<LoaiThanhToan>").append(escapeXml(donHang.getLoaiThanhToan() != null ? donHang.getLoaiThanhToan() : "")).append("</LoaiThanhToan>");
            xml.append("<NganHang>").append(escapeXml(donHang.getNganHang() != null ? donHang.getNganHang() : "")).append("</NganHang>");
            xml.append("<TrangThai>").append(escapeXml(donHang.getTrangThai() != null ? donHang.getTrangThai() : "")).append("</TrangThai>");
            xml.append("<MoTa>").append(escapeXml(donHang.getMoTa() != null ? donHang.getMoTa() : "")).append("</MoTa>");
            
            xml.append("</ThongTinDonHang>");
            xml.append("</Data>");
            
            // Signature sẽ được thêm bởi DatabaseCertificateService
            xml.append("</Customs>");
            
            String result = xml.toString();
            log.info("Hoàn thành tạo XML cho đơn hàng ID: {}, độ dài XML: {} ký tự", donHang.getId(), result.length());
            
            return result;
            
        } catch (Exception e) {
            log.error("Lỗi khi tạo XML cho đơn hàng: ", e);
            throw new BusinessException("Lỗi khi tạo XML cho đơn hàng: " + e.getMessage());
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
