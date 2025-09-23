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
import com.pht.model.request.SaveWindowsCertificateRequest;
import com.pht.model.response.ChuKySoResponse;
import com.pht.repository.ChukySoRepository;
import com.pht.service.WindowsCertificateSaveService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation của WindowsCertificateSaveService để lưu chữ ký số từ Windows Security vào database
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WindowsCertificateSaveServiceImpl implements WindowsCertificateSaveService {
    
    private final ChukySoRepository chukySoRepository;
    
    @Override
    @Transactional
    public ChuKySoResponse saveWindowsCertificateToDatabase(SaveWindowsCertificateRequest request) throws BusinessException {
        log.info("Bắt đầu lưu chữ ký số từ Windows Security với serial number: {}", request.getSerialNumber());
        
        try {
            // Tìm certificate trong Windows Security
            X509Certificate certificate = findCertificateInWindowsStore(request.getSerialNumber());
            if (certificate == null) {
                throw new BusinessException("Không tìm thấy chữ ký số với serial number: " + request.getSerialNumber() + " trong Windows Security");
            }
            
            // Lấy private key
            PrivateKey privateKey = findPrivateKeyInWindowsStore(request.getSerialNumber());
            if (privateKey == null) {
                throw new BusinessException("Không tìm thấy private key cho certificate với serial number: " + request.getSerialNumber());
            }
            
            // Kiểm tra certificate đã tồn tại trong database chưa
            String serialNumberHex = certificate.getSerialNumber().toString(16).toUpperCase();
            if (chukySoRepository.existsBySerialNumber(serialNumberHex)) {
                throw new BusinessException("Chữ ký số với serial number " + serialNumberHex + " đã tồn tại trong database");
            }
            
            // Tính thumbprint để kiểm tra trùng lặp
            String thumbprint = calculateThumbprint(certificate);
            if (thumbprint != null && chukySoRepository.existsByThumbprint(thumbprint)) {
                throw new BusinessException("Chữ ký số với thumbprint " + thumbprint + " đã tồn tại trong database");
            }
            
            // Nếu đặt làm mặc định, bỏ mặc định của các chữ ký khác
            if (Boolean.TRUE.equals(request.getIsDefault())) {
                chukySoRepository.clearDefaultCertificates();
            }
            
            // Tạo entity để lưu
            ChukySo chukySo = createChukySoEntity(certificate, privateKey, request);
            
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
     * Tìm certificate trong Windows Certificate Store
     */
    private X509Certificate findCertificateInWindowsStore(String serialNumber) {
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
                            log.info("Tìm thấy certificate với serial number: {} và alias: {}", serialNumber, alias);
                            return cert;
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
     * Tìm private key trong Windows Certificate Store
     */
    private PrivateKey findPrivateKeyInWindowsStore(String serialNumber) {
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
                                log.info("Tìm thấy private key cho certificate với serial number: {}", serialNumber);
                                return privateKey;
                            }
                        }
                    }
                }
            }
            
            log.warn("Không tìm thấy private key cho certificate với serial number: {}", serialNumber);
            return null;
            
        } catch (Exception e) {
            log.error("Lỗi khi tìm private key trong Windows Store: ", e);
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
     * Tạo entity ChukySo từ certificate và request
     */
    private ChukySo createChukySoEntity(X509Certificate certificate, PrivateKey privateKey, SaveWindowsCertificateRequest request) {
        ChukySo chukySo = new ChukySo();
        
        // Thông tin certificate
        String serialNumberHex = certificate.getSerialNumber().toString(16).toUpperCase();
        chukySo.setSerialNumber(serialNumberHex);
        chukySo.setSubject(certificate.getSubjectX500Principal().getName());
        chukySo.setIssuer(certificate.getIssuerX500Principal().getName());
        
        // Convert dates
        chukySo.setValidFrom(certificate.getNotBefore().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        chukySo.setValidTo(certificate.getNotAfter().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        
        // Thông tin doanh nghiệp
        chukySo.setMaDoanhNghiep(request.getMaDoanhNghiep());
        chukySo.setTenDoanhNghiep(request.getTenDoanhNghiep());
        chukySo.setMaSoThue(request.getMaSoThue());
        
        // Encode certificate và private key
        try {
            // Encode certificate - chỉ lưu data thuần túy
            String certData = Base64.getEncoder().encodeToString(certificate.getEncoded());
            chukySo.setCertificateData(certData);
            
            // Encode private key - xử lý các loại key khác nhau
            String privateKeyData = encodePrivateKey(privateKey);
            chukySo.setPrivateKey(privateKeyData);
            
            // Encode public key - chỉ lưu data thuần túy
            String publicKeyData = Base64.getEncoder().encodeToString(certificate.getPublicKey().getEncoded());
            chukySo.setPublicKey(publicKeyData);
            
        } catch (Exception e) {
            log.error("Lỗi khi encode certificate/private key: ", e);
            throw new RuntimeException("Không thể encode certificate/private key", e);
        }
        
        // Thông tin thuật toán
        chukySo.setSignatureAlgorithm(certificate.getSigAlgName());
        chukySo.setHashAlgorithm(certificate.getSigAlgName());
        chukySo.setThumbprint(calculateThumbprint(certificate));
        
        // Thông tin trạng thái
        chukySo.setTrangThai(request.getTrangThai());
        chukySo.setLoaiChuKy(request.getLoaiChuKy());
        chukySo.setGhiChu(request.getGhiChu());
        chukySo.setIsActive(true);
        chukySo.setIsDefault(request.getIsDefault());
        
        return chukySo;
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
}
