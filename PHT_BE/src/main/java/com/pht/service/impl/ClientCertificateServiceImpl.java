package com.pht.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pht.entity.ChukySo;
import com.pht.exception.BusinessException;
import com.pht.model.request.ClientCertificateListRequest;
import com.pht.model.response.ChuKySoResponse;
import com.pht.repository.ChukySoRepository;
import com.pht.service.ClientCertificateService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation của ClientCertificateService để xử lý chữ ký số từ frontend React
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClientCertificateServiceImpl implements ClientCertificateService {
    
    private final ChukySoRepository chukySoRepository;
    
    @Override
    @Transactional
    public List<ChuKySoResponse> saveClientCertificates(ClientCertificateListRequest request) throws BusinessException {
        log.info("Nhận danh sách {} chữ ký số từ client", request.getCertificates().size());
        
        List<ChuKySoResponse> savedCertificates = new ArrayList<>();
        
        for (ClientCertificateListRequest.ClientCertificateInfo certInfo : request.getCertificates()) {
            try {
                ChuKySoResponse savedCert = saveClientCertificate(certInfo);
                savedCertificates.add(savedCert);
                log.info("Đã lưu chữ ký số: {}", certInfo.getSerialNumber());
            } catch (Exception e) {
                log.error("Lỗi khi lưu chữ ký số {}: {}", certInfo.getSerialNumber(), e.getMessage());
                // Tiếp tục xử lý các certificate khác
            }
        }
        
        log.info("Hoàn thành lưu {} chữ ký số từ client", savedCertificates.size());
        return savedCertificates;
    }
    
    @Override
    @Transactional
    public ChuKySoResponse saveClientCertificate(ClientCertificateListRequest.ClientCertificateInfo certInfo) throws BusinessException {
        log.info("Lưu chữ ký số từ client với serial number: {}", certInfo.getSerialNumber());
        
        try {
            // Kiểm tra certificate đã tồn tại chưa
            if (chukySoRepository.existsBySerialNumber(certInfo.getSerialNumber())) {
                throw new BusinessException("Chữ ký số với serial number " + certInfo.getSerialNumber() + " đã tồn tại trong database");
            }
            
            // Tạo entity để lưu
            ChukySo chukySo = createChukySoEntityFromClientData(certInfo);
            
            // Tự động đặt certificate mới nhất của cùng MST làm mặc định
            setDefaultForSameMST(chukySo);
            
            // Lưu vào database
            ChukySo savedChukySo = chukySoRepository.save(chukySo);
            
            log.info("Lưu thành công chữ ký số với ID: {} và serial number: {}", savedChukySo.getId(), certInfo.getSerialNumber());
            
            // Convert sang response
            return convertToResponse(savedChukySo);
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Lỗi khi lưu chữ ký số từ client: ", e);
            throw new BusinessException("Lỗi khi lưu chữ ký số: " + e.getMessage());
        }
    }
    
    /**
     * Tạo entity ChukySo từ dữ liệu client
     */
    private ChukySo createChukySoEntityFromClientData(ClientCertificateListRequest.ClientCertificateInfo certInfo) {
        ChukySo chukySo = new ChukySo();
        
        // Thông tin certificate từ client
        chukySo.setSerialNumber(certInfo.getSerialNumber());
        chukySo.setSubject(certInfo.getSubject());
        chukySo.setIssuer(certInfo.getIssuer());
        
        // Parse dates từ string
        chukySo.setValidFrom(parseDate(certInfo.getValidFrom()));
        chukySo.setValidTo(parseDate(certInfo.getValidTo()));
        
        // Thông tin doanh nghiệp từ client
        chukySo.setTenDoanhNghiep(certInfo.getTenDoanhNghiep());
        chukySo.setMaSoThue(certInfo.getMaSoThue());
        chukySo.setMaDoanhNghiep("DN_" + certInfo.getSerialNumber());
        
        // Dữ liệu certificate từ client (đã được encode ở frontend)
        chukySo.setCertificateData(certInfo.getCertificateData());
        chukySo.setPrivateKey(certInfo.getPrivateKeyData());
        chukySo.setPublicKey(certInfo.getPublicKeyData());
        
        // Thông tin thuật toán (mặc định)
        chukySo.setSignatureAlgorithm("RSA-PSS");
        chukySo.setHashAlgorithm("SHA-256");
        chukySo.setThumbprint(calculateThumbprintFromSerial(certInfo.getSerialNumber()));
        
        // Thông tin trạng thái - tự động xác định
        chukySo.setTrangThai(determineCertificateStatusFromDates(certInfo.getValidFrom(), certInfo.getValidTo()));
        chukySo.setLoaiChuKy(determineCertificateTypeFromSubject(certInfo.getSubject()));
        chukySo.setGhiChu("Chữ ký số được gửi từ frontend React");
        chukySo.setIsActive(true);
        chukySo.setIsDefault(false); // Sẽ được set lại trong setDefaultForSameMST
        
        return chukySo;
    }
    
    /**
     * Parse date từ string format yyyy-MM-dd
     */
    private LocalDateTime parseDate(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return LocalDateTime.now();
        }
        
        try {
            // Parse từ format yyyy-MM-dd
            return LocalDateTime.parse(dateString + "T00:00:00");
        } catch (Exception e) {
            log.warn("Không thể parse date: {}, sử dụng ngày hiện tại", dateString);
            return LocalDateTime.now();
        }
    }
    
    /**
     * Tính thumbprint từ serial number (đơn giản)
     */
    private String calculateThumbprintFromSerial(String serialNumber) {
        try {
            java.security.MessageDigest sha1 = java.security.MessageDigest.getInstance("SHA-1");
            byte[] bytes = serialNumber.getBytes();
            byte[] thumbprintBytes = sha1.digest(bytes);
            
            StringBuilder sb = new StringBuilder();
            for (byte b : thumbprintBytes) {
                sb.append(String.format("%02X", b));
            }
            return sb.toString();
        } catch (Exception e) {
            log.error("Lỗi khi tính thumbprint: ", e);
            return serialNumber; // Fallback
        }
    }
    
    /**
     * Xác định trạng thái certificate từ dates
     */
    private String determineCertificateStatusFromDates(String validFrom, String validTo) {
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime fromDate = parseDate(validFrom);
            LocalDateTime toDate = parseDate(validTo);
            
            if (now.isBefore(fromDate)) {
                return "NOT_YET_VALID";
            } else if (now.isAfter(toDate)) {
                return "EXPIRED";
            } else {
                return "ACTIVE";
            }
        } catch (Exception e) {
            log.error("Lỗi khi xác định trạng thái certificate: ", e);
            return "UNKNOWN";
        }
    }
    
    /**
     * Xác định loại certificate từ subject
     */
    private String determineCertificateTypeFromSubject(String subject) {
        if (subject == null || subject.isEmpty()) {
            return "UNKNOWN";
        }
        
        try {
            if (subject.contains("CN=") && !subject.contains("O=")) {
                return "PERSONAL";
            } else if (subject.contains("O=")) {
                return "ORGANIZATION";
            } else {
                return "ENTERPRISE";
            }
        } catch (Exception e) {
            log.error("Lỗi khi xác định loại certificate: ", e);
            return "UNKNOWN";
        }
    }
    
    /**
     * Tự động đặt certificate mới nhất của cùng MST làm mặc định
     */
    private void setDefaultForSameMST(ChukySo newCertificate) {
        try {
            String maSoThue = newCertificate.getMaSoThue();
            if (maSoThue != null && !maSoThue.isEmpty()) {
                // Bỏ mặc định của tất cả certificate có cùng MST
                chukySoRepository.clearDefaultForSameMST(maSoThue);
                
                // Đặt certificate mới làm mặc định
                newCertificate.setIsDefault(true);
                
                log.info("Đặt certificate mới làm mặc định cho MST: {}", maSoThue);
            } else {
                // Nếu không có MST, đặt làm mặc định cho tất cả
                chukySoRepository.clearDefaultCertificates();
                newCertificate.setIsDefault(true);
                
                log.info("Đặt certificate mới làm mặc định (không có MST)");
            }
        } catch (Exception e) {
            log.error("Lỗi khi đặt certificate mặc định: ", e);
            // Fallback: không đặt mặc định
            newCertificate.setIsDefault(false);
        }
    }
    
    /**
     * Convert entity sang response
     */
    private ChuKySoResponse convertToResponse(ChukySo entity) {
        ChuKySoResponse response = new ChuKySoResponse();
        
        response.setSerialNumber(entity.getSerialNumber());
        response.setIssuer(entity.getIssuer());
        response.setSubject(entity.getSubject());
        response.setCert(entity.getCertificateData());
        response.setValidFrom(formatDate(entity.getValidFrom()));
        response.setValidTo(formatDate(entity.getValidTo()));
        
        return response;
    }
    
    /**
     * Extract tên nhà phát hành từ issuer string
     */
    private String extractIssuerName(String issuer) {
        if (issuer == null || issuer.isEmpty()) {
            return "Unknown";
        }
        
        try {
            String[] parts = issuer.split(",");
            for (String part : parts) {
                part = part.trim();
                if (part.startsWith("CN=")) {
                    return part.substring(3).trim();
                }
            }
        } catch (Exception e) {
            log.debug("Không thể parse issuer: {}", issuer, e);
        }
        
        return issuer;
    }
    
    /**
     * Format date thành dd/MM/yyyy
     */
    private String formatDate(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
}
