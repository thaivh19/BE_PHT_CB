package com.pht.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pht.entity.ChukySo;
import com.pht.exception.BusinessException;
import com.pht.model.request.SaveCertificateFromFrontendRequest;
import com.pht.model.response.ChuKySoResponse;
import com.pht.repository.ChukySoRepository;
import com.pht.service.FrontendCertificateSaveService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation của FrontendCertificateSaveService
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FrontendCertificateSaveServiceImpl implements FrontendCertificateSaveService {
    
    private final ChukySoRepository chukySoRepository;
    
    @Override
    @Transactional
    public ChuKySoResponse saveCertificateFromFrontend(SaveCertificateFromFrontendRequest request) throws BusinessException {
        log.info("Lưu chữ ký số từ frontend với serial number: {}", request.getSerialNumber());
        
        try {
            // Kiểm tra certificate đã tồn tại chưa
            if (chukySoRepository.existsBySerialNumber(request.getSerialNumber())) {
                throw new BusinessException("Chữ ký số với serial number " + request.getSerialNumber() + " đã tồn tại trong database");
            }
            
            // Tạo entity để lưu
            ChukySo chukySo = createChukySoEntityFromFrontendRequest(request);
            
            // Tự động đặt certificate mới nhất của cùng MST làm mặc định
            setDefaultForSameMST(chukySo);
            
            // Lưu vào database
            ChukySo savedChukySo = chukySoRepository.save(chukySo);
            
            log.info("Lưu thành công chữ ký số với ID: {} và serial number: {}", savedChukySo.getId(), request.getSerialNumber());
            
            // Convert sang response
            return convertToResponse(savedChukySo);
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Lỗi khi lưu chữ ký số từ frontend: ", e);
            throw new BusinessException("Lỗi khi lưu chữ ký số: " + e.getMessage());
        }
    }
    
    /**
     * Tạo entity ChukySo từ request từ frontend
     */
    private ChukySo createChukySoEntityFromFrontendRequest(SaveCertificateFromFrontendRequest request) {
        ChukySo chukySo = new ChukySo();
        
        // Thông tin cơ bản từ frontend
        chukySo.setSerialNumber(request.getSerialNumber());
        chukySo.setSubject(request.getSubject());
        chukySo.setIssuer(request.getIssuer());
        
        // Parse dates từ string
        chukySo.setValidFrom(parseDate(request.getValidFrom()));
        chukySo.setValidTo(parseDate(request.getValidTo()));
        
        // Thông tin doanh nghiệp
        chukySo.setTenDoanhNghiep(request.getTenDoanhNghiep());
        chukySo.setMaSoThue(request.getMaSoThue());
        chukySo.setMaDoanhNghiep(request.getMaDoanhNghiep() != null ? request.getMaDoanhNghiep() : "DN_" + request.getSerialNumber());
        
        // Dữ liệu certificate từ frontend (đã được encode Base64)
        chukySo.setCertificateData(request.getCertificateData());
        chukySo.setPrivateKey(request.getPrivateKeyData());
        chukySo.setPublicKey(request.getPublicKeyData());
        
        // Thông tin thuật toán
        chukySo.setSignatureAlgorithm(request.getSignatureAlgorithm() != null ? request.getSignatureAlgorithm() : "RSA-PSS");
        chukySo.setHashAlgorithm(request.getHashAlgorithm() != null ? request.getHashAlgorithm() : "SHA-256");
        
        // Thumbprint
        chukySo.setThumbprint(request.getThumbprint() != null ? request.getThumbprint() : calculateThumbprintFromSerial(request.getSerialNumber()));
        
        // Mật khẩu và ghi chú
        chukySo.setPassword(request.getPassword());
        chukySo.setGhiChu(request.getGhiChu() != null ? request.getGhiChu() : "Chữ ký số được gửi từ frontend");
        
        // Thông tin trạng thái - tự động xác định
        chukySo.setTrangThai(determineCertificateStatusFromDates(request.getValidFrom(), request.getValidTo()));
        chukySo.setLoaiChuKy(determineCertificateTypeFromSubject(request.getSubject()));
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
