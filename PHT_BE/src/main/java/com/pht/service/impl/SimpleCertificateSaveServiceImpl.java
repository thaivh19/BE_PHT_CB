package com.pht.service.impl;

import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Enumeration;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pht.entity.ChukySo;
import com.pht.exception.BusinessException;
import com.pht.model.request.SaveCertificateBySerialRequest;
import com.pht.model.response.ChuKySoResponse;
import com.pht.repository.ChukySoRepository;
import com.pht.service.SimpleCertificateSaveService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation của SimpleCertificateSaveService để lưu chữ ký số từ Windows Security chỉ với SerialNumber
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SimpleCertificateSaveServiceImpl implements SimpleCertificateSaveService {
    
    private final ChukySoRepository chukySoRepository;
    
    @Override
    @Transactional
    public ChuKySoResponse saveCertificateBySerialNumber(SaveCertificateBySerialRequest request) throws BusinessException {
        log.info("Bắt đầu lưu chữ ký số từ Windows Security với serial number: {}", request.getSerialNumber());
        
        try {
            // Tìm certificate trong Windows Security
            CertificateInfo certInfo = findCertificateInWindowsStore(request.getSerialNumber());
            if (certInfo == null) {
                throw new BusinessException("Không tìm thấy chữ ký số với serial number: " + request.getSerialNumber() + " trong Windows Security");
            }
            
            // Kiểm tra certificate đã tồn tại trong database chưa
            String serialNumberHex = certInfo.certificate.getSerialNumber().toString(16).toUpperCase();
            if (chukySoRepository.existsBySerialNumber(serialNumberHex)) {
                throw new BusinessException("Chữ ký số với serial number " + serialNumberHex + " đã tồn tại trong database");
            }
            
            // Tính thumbprint để kiểm tra trùng lặp
            String thumbprint = calculateThumbprint(certInfo.certificate);
            if (thumbprint != null && chukySoRepository.existsByThumbprint(thumbprint)) {
                throw new BusinessException("Chữ ký số với thumbprint " + thumbprint + " đã tồn tại trong database");
            }
            
            // Tạo entity để lưu với thông tin tự động extract
            ChukySo chukySo = createChukySoEntityFromCertificate(certInfo, request);
            
            // Tự động đặt certificate mới nhất của cùng MST làm mặc định
            setDefaultForSameMST(chukySo);
            
            // Lưu vào database
            ChukySo savedChukySo = chukySoRepository.save(chukySo);
            
            log.info("Lưu thành công chữ ký số với ID: {} và serial number: {}", savedChukySo.getId(), serialNumberHex);
            
            // Convert sang response
            return convertToResponse(savedChukySo);
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Lỗi khi lưu chữ ký số từ Windows Security: ", e);
            throw new BusinessException("Lỗi khi lưu chữ ký số: " + e.getMessage());
        }
    }
    
    /**
     * Inner class để chứa thông tin certificate
     */
    private static class CertificateInfo {
        X509Certificate certificate;
        PrivateKey privateKey;
        
        CertificateInfo(X509Certificate certificate, PrivateKey privateKey) {
            this.certificate = certificate;
            this.privateKey = privateKey;
        }
    }
    
    /**
     * Tìm certificate trong Windows Certificate Store
     */
    private CertificateInfo findCertificateInWindowsStore(String serialNumber) {
        try {
            KeyStore keyStore = KeyStore.getInstance("Windows-MY");
            keyStore.load(null, null);
            
            Enumeration<String> aliases = keyStore.aliases();
            while (aliases.hasMoreElements()) {
                String alias = aliases.nextElement();
                
                if (keyStore.isKeyEntry(alias)) {
                    X509Certificate cert = (X509Certificate) keyStore.getCertificate(alias);
                    if (cert != null) {
                        String certSerialNumber = cert.getSerialNumber().toString(16).toUpperCase();
                        if (certSerialNumber.equals(serialNumber)) {
                            PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, null);
                            if (privateKey != null) {
                                log.info("Tìm thấy certificate với serial number: {} và alias: {}", serialNumber, alias);
                                return new CertificateInfo(cert, privateKey);
                            }
                        }
                    }
                }
            }
            
            log.warn("Không tìm thấy certificate với serial number: {}", serialNumber);
            return null;
            
        } catch (Exception e) {
            log.error("Lỗi khi tìm certificate trong Windows Store: ", e);
            return null;
        }
    }
    
    /**
     * Tính thumbprint của certificate
     */
    private String calculateThumbprint(X509Certificate certificate) {
        try {
            java.security.MessageDigest sha1 = java.security.MessageDigest.getInstance("SHA-1");
            byte[] certBytes = certificate.getEncoded();
            byte[] thumbprintBytes = sha1.digest(certBytes);
            
            StringBuilder sb = new StringBuilder();
            for (byte b : thumbprintBytes) {
                sb.append(String.format("%02X", b));
            }
            return sb.toString();
            
        } catch (Exception e) {
            log.error("Lỗi khi tính thumbprint: ", e);
            return null;
        }
    }
    
    /**
     * Tạo entity ChukySo từ certificate với thông tin tự động extract
     */
    private ChukySo createChukySoEntityFromCertificate(CertificateInfo certInfo, SaveCertificateBySerialRequest request) {
        X509Certificate cert = certInfo.certificate;
        PrivateKey privateKey = certInfo.privateKey;
        
        ChukySo chukySo = new ChukySo();
        
        // Thông tin certificate
        String serialNumberHex = cert.getSerialNumber().toString(16).toUpperCase();
        chukySo.setSerialNumber(serialNumberHex);
        chukySo.setSubject(cert.getSubjectX500Principal().getName());
        chukySo.setIssuer(cert.getIssuerX500Principal().getName());
        
        // Convert dates
        chukySo.setValidFrom(cert.getNotBefore().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        chukySo.setValidTo(cert.getNotAfter().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        
        // Tự động extract thông tin doanh nghiệp từ subject
        extractBusinessInfoFromSubject(cert, chukySo);
        
        // Encode certificate và private key
        try {
            // Encode certificate - chỉ lưu data thuần túy
            String certData = Base64.getEncoder().encodeToString(cert.getEncoded());
            chukySo.setCertificateData(certData);
            
            // Encode private key - xử lý các loại key khác nhau
            String privateKeyData = encodePrivateKey(privateKey);
            chukySo.setPrivateKey(privateKeyData);
            
            // Encode public key - chỉ lưu data thuần túy
            String publicKeyData = Base64.getEncoder().encodeToString(cert.getPublicKey().getEncoded());
            chukySo.setPublicKey(publicKeyData);
            
        } catch (Exception e) {
            log.error("Lỗi khi encode certificate/private key: ", e);
            throw new RuntimeException("Không thể encode certificate/private key", e);
        }
        
        // Thông tin thuật toán
        chukySo.setSignatureAlgorithm(cert.getSigAlgName());
        chukySo.setHashAlgorithm(cert.getSigAlgName());
        chukySo.setThumbprint(calculateThumbprint(cert));
        
        // Thông tin trạng thái - tự động xác định
        chukySo.setTrangThai(determineCertificateStatus(cert));
        chukySo.setLoaiChuKy(determineCertificateType(cert));
        chukySo.setGhiChu("Chữ ký số được import từ Windows Security");
        chukySo.setIsActive(true);
        chukySo.setIsDefault(false); // Sẽ được set lại trong setDefaultForSameMST
        
        return chukySo;
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
     * Encode private key với xử lý các loại key khác nhau
     */
    private String encodePrivateKey(PrivateKey privateKey) {
        try {
            // Thử encode trực tiếp
            return Base64.getEncoder().encodeToString(privateKey.getEncoded());
        } catch (Exception e) {
            log.warn("Không thể encode private key trực tiếp, thử cách khác: {}", e.getMessage());
            
            try {
                // Thử convert sang PKCS8 format
                PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(privateKey.getEncoded());
                return Base64.getEncoder().encodeToString(pkcs8KeySpec.getEncoded());
            } catch (Exception e2) {
                log.error("Không thể encode private key với PKCS8: {}", e2.getMessage());
                // Fallback: lưu thông tin về key thay vì key data
                return "PRIVATE_KEY_NOT_ENCODABLE_" + privateKey.getAlgorithm() + "_" + privateKey.getFormat();
            }
        }
    }
    
    /**
     * Tự động extract thông tin doanh nghiệp từ subject của certificate
     */
    private void extractBusinessInfoFromSubject(X509Certificate cert, ChukySo chukySo) {
        String subject = cert.getSubjectX500Principal().getName();
        log.info("Extracting business info from subject: {}", subject);
        
        try {
            String[] parts = subject.split(",");
            String commonName = "";
            String organizationName = "";
            String serialNumber = "";
            
            for (String part : parts) {
                part = part.trim();
                if (part.startsWith("CN=")) {
                    commonName = part.substring(3).trim();
                    // Tìm MST trong CN (thường ở cuối CN)
                    if (commonName.matches(".*\\d{10,13}.*")) {
                        serialNumber = commonName.replaceAll(".*?(\\d{10,13}).*", "$1");
                    }
                } else if (part.startsWith("O=")) {
                    organizationName = part.substring(2).trim();
                } else if (part.startsWith("SERIALNUMBER=")) {
                    serialNumber = part.substring(13).trim();
                }
            }
            
            // Sử dụng CN làm tên doanh nghiệp nếu không có O
            String tenDoanhNghiep = organizationName.isEmpty() ? commonName : organizationName;
            chukySo.setTenDoanhNghiep(tenDoanhNghiep);
            
            // Tạo mã doanh nghiệp từ serial number
            chukySo.setMaDoanhNghiep("DN_" + cert.getSerialNumber().toString(16).toUpperCase());
            
            // Set MST nếu tìm thấy
            if (!serialNumber.isEmpty()) {
                chukySo.setMaSoThue(serialNumber);
            }
            
            log.info("Extracted business info - Ten: {}, Ma: {}, MST: {}", tenDoanhNghiep, chukySo.getMaDoanhNghiep(), serialNumber);
            
        } catch (Exception e) {
            log.error("Lỗi khi extract business info từ subject: ", e);
            // Fallback values
            chukySo.setTenDoanhNghiep("Doanh nghiệp không xác định");
            chukySo.setMaDoanhNghiep("DN_" + cert.getSerialNumber().toString(16).toUpperCase());
            chukySo.setMaSoThue(null); // Không có MST
        }
    }
    
    /**
     * Tự động xác định trạng thái certificate
     */
    private String determineCertificateStatus(X509Certificate cert) {
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime validFrom = cert.getNotBefore().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            LocalDateTime validTo = cert.getNotAfter().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            
            if (now.isBefore(validFrom)) {
                return "NOT_YET_VALID";
            } else if (now.isAfter(validTo)) {
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
     * Tự động xác định loại certificate
     */
    private String determineCertificateType(X509Certificate cert) {
        try {
            String subject = cert.getSubjectX500Principal().getName();
            
            // Kiểm tra xem có phải certificate cá nhân không
            if (subject.contains("CN=") && !subject.contains("O=")) {
                return "PERSONAL";
            }
            
            // Kiểm tra xem có phải certificate tổ chức không
            if (subject.contains("O=")) {
                return "ORGANIZATION";
            }
            
            // Mặc định
            return "ENTERPRISE";
            
        } catch (Exception e) {
            log.error("Lỗi khi xác định loại certificate: ", e);
            return "UNKNOWN";
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
