package com.pht.service.impl;

import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.pht.model.response.ChuKySoResponse;
import com.pht.service.WindowsCertificateService;

import lombok.extern.slf4j.Slf4j;

/**
 * Implementation của WindowsCertificateService để lấy danh sách chữ ký số từ Windows Security
 */
@Slf4j
@Service
public class WindowsCertificateServiceImpl implements WindowsCertificateService {
    
    @Override
    public List<ChuKySoResponse> getAllWindowsCertificates() {
        log.info("Bắt đầu lấy danh sách tất cả chữ ký số từ Windows Certificate Store");
        
        List<ChuKySoResponse> certificates = new ArrayList<>();
        
        try {
            // Truy cập Windows Certificate Store - Personal certificates
            KeyStore keyStore = KeyStore.getInstance("Windows-MY");
            keyStore.load(null, null);
            
            Enumeration<String> aliases = keyStore.aliases();
            while (aliases.hasMoreElements()) {
                String alias = aliases.nextElement();
                
                try {
                    // Kiểm tra xem có phải là key entry (có private key) không
                    if (keyStore.isKeyEntry(alias)) {
                        X509Certificate cert = (X509Certificate) keyStore.getCertificate(alias);
                        if (cert != null) {
                            ChuKySoResponse response = convertToResponse(cert, alias);
                            if (response != null) {
                                certificates.add(response);
                            }
                        }
                    }
                } catch (Exception e) {
                    log.warn("Không thể xử lý certificate với alias: {}, lỗi: {}", alias, e.getMessage());
                }
            }
            
            log.info("Lấy thành công {} chữ ký số từ Windows Certificate Store", certificates.size());
            
        } catch (Exception e) {
            log.error("Lỗi khi lấy danh sách chữ ký số từ Windows Certificate Store: ", e);
            throw new RuntimeException("Không thể truy cập Windows Certificate Store: " + e.getMessage(), e);
        }
        
        return certificates;
    }
    
    @Override
    public List<ChuKySoResponse> getValidWindowsCertificates() {
        log.info("Bắt đầu lấy danh sách chữ ký số hợp lệ từ Windows Certificate Store");
        
        List<ChuKySoResponse> allCertificates = getAllWindowsCertificates();
        List<ChuKySoResponse> validCertificates = new ArrayList<>();
        
        Date now = new Date();
        
        for (ChuKySoResponse cert : allCertificates) {
            try {
                // Parse validFrom và validTo từ string
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                LocalDateTime validFrom = LocalDateTime.parse(cert.getValidFrom(), formatter);
                LocalDateTime validTo = LocalDateTime.parse(cert.getValidTo(), formatter);
                
                Date validFromDate = Date.from(validFrom.atZone(ZoneId.systemDefault()).toInstant());
                Date validToDate = Date.from(validTo.atZone(ZoneId.systemDefault()).toInstant());
                
                // Kiểm tra certificate có đang hợp lệ không
                if (now.after(validFromDate) && now.before(validToDate)) {
                    validCertificates.add(cert);
                }
                
            } catch (Exception e) {
                log.warn("Không thể parse ngày hiệu lực cho certificate: {}, lỗi: {}", cert.getSerialNumber(), e.getMessage());
            }
        }
        
        log.info("Lấy thành công {} chữ ký số hợp lệ từ Windows Certificate Store", validCertificates.size());
        
        return validCertificates;
    }
    
    @Override
    public ChuKySoResponse getWindowsCertificateBySerialNumber(String serialNumber) {
        log.info("Tìm chữ ký số với serial number: {} từ Windows Certificate Store", serialNumber);
        
        List<ChuKySoResponse> allCertificates = getAllWindowsCertificates();
        
        Optional<ChuKySoResponse> found = allCertificates.stream()
                .filter(cert -> cert.getSerialNumber().equals(serialNumber))
                .findFirst();
        
        if (found.isPresent()) {
            log.info("Tìm thấy chữ ký số với serial number: {}", serialNumber);
            return found.get();
        } else {
            log.warn("Không tìm thấy chữ ký số với serial number: {}", serialNumber);
            return null;
        }
    }
    
    /**
     * Convert X509Certificate thành ChuKySoResponse
     */
    private ChuKySoResponse convertToResponse(X509Certificate cert, String alias) {
        try {
            ChuKySoResponse response = new ChuKySoResponse();
            
            // Serial number
            String serialNumber = cert.getSerialNumber().toString(16).toUpperCase();
            response.setSerialNumber(serialNumber);
            
            // Subject
            String subject = cert.getSubjectX500Principal().getName();
            response.setSubject(subject);
            
            // Issuer
            String issuer = cert.getIssuerX500Principal().getName();
            response.setIssuer(issuer);
            
            // Certificate data
            response.setCert(encodeCertificate(cert));
            
            // Valid dates
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDateTime validFrom = cert.getNotBefore().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            LocalDateTime validTo = cert.getNotAfter().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            
            response.setValidFrom(validFrom.format(formatter));
            response.setValidTo(validTo.format(formatter));
            
            return response;
            
        } catch (Exception e) {
            log.error("Lỗi khi convert certificate với alias: {}, lỗi: {}", alias, e.getMessage());
            return null;
        }
    }
    
    /**
     * Extract Common Name từ DN string
     */
    private String extractCommonName(String dn) {
        if (dn == null || dn.isEmpty()) {
            return "Unknown";
        }
        
        try {
            String[] parts = dn.split(",");
            for (String part : parts) {
                part = part.trim();
                if (part.startsWith("CN=")) {
                    return part.substring(3).trim();
                }
            }
        } catch (Exception e) {
            log.debug("Không thể parse DN: {}", dn, e);
        }
        
        return dn;
    }
    
    /**
     * Encode certificate thành Base64
     */
    private String encodeCertificate(X509Certificate cert) {
        try {
            byte[] certBytes = cert.getEncoded();
            return Base64.getEncoder().encodeToString(certBytes);
        } catch (Exception e) {
            log.error("Lỗi khi encode certificate: ", e);
            return null;
        }
    }
}
