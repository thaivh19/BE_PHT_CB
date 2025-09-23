package com.pht.service.impl;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Enumeration;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pht.exception.BusinessException;
import com.pht.model.request.ImportCertificateFileRequest;
import com.pht.model.request.ImportCertificateRequest;
import com.pht.model.response.ImportCertificateResponse;
import com.pht.service.CertificateFileImportService;
import com.pht.service.CertificateImportService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Data class để chứa certificate và private key từ file P12
 */
class P12Data {
    String certificateData;
    String privateKeyData;
    
    P12Data(String certificateData, String privateKeyData) {
        this.certificateData = certificateData;
        this.privateKeyData = privateKeyData;
    }
}

@Slf4j
@Service
@RequiredArgsConstructor
public class CertificateFileImportServiceImpl implements CertificateFileImportService {
    
    private final CertificateImportService certificateImportService;
    
    @Override
    @Transactional
    public ImportCertificateResponse importCertificateFromFile(ImportCertificateFileRequest request) throws BusinessException {
        log.info("Bắt đầu import certificate từ file: {}", request.getCertificateFilePath());
        
        try {
            String filePath = request.getCertificateFilePath();
            String fileName = Paths.get(filePath).getFileName().toString().toLowerCase();
            
            String certificateData;
            String privateKeyData;
            
            // Kiểm tra loại file
            if (fileName.endsWith(".p12") || fileName.endsWith(".pfx")) {
                log.info("Phát hiện file PKCS#12: {}", fileName);
                // Xử lý file .p12/.pfx
                P12Data p12Data = readP12File(filePath, request.getPassword());
                certificateData = p12Data.certificateData;
                privateKeyData = p12Data.privateKeyData;
            } else {
                log.info("Phát hiện file certificate thông thường: {}", fileName);
                // Xử lý file certificate thông thường
                certificateData = readCertificateFile(filePath);
                privateKeyData = extractPrivateKeyFromCertificate(certificateData, request.getPassword());
            }
            
            // Parse certificate để extract thông tin
            String cleanData = certificateData
                    .replace("-----BEGIN CERTIFICATE-----", "")
                    .replace("-----END CERTIFICATE-----", "")
                    .replaceAll("\\s", "");
            
            byte[] certBytes = Base64.getDecoder().decode(cleanData);
            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            X509Certificate certificate = (X509Certificate) certFactory.generateCertificate(new ByteArrayInputStream(certBytes));
            
            // Extract thông tin doanh nghiệp từ certificate
            String subject = certificate.getSubjectX500Principal().getName();
            String tenDoanhNghiep = extractCompanyName(subject);
            String maSoThue = extractTaxCode(subject);
            
            // Sử dụng tên doanh nghiệp từ certificate
            String finalTenDoanhNghiep = tenDoanhNghiep;
            
            // Tạo request cho service import - clean data trước khi truyền
            ImportCertificateRequest importRequest = new ImportCertificateRequest();
            importRequest.setCertificateData(cleanCertificateData(certificateData));
            importRequest.setPrivateKeyData(cleanPrivateKeyData(privateKeyData));
            importRequest.setPassword(request.getPassword());
            importRequest.setTenDoanhNghiep(finalTenDoanhNghiep);
            importRequest.setMaSoThue(maSoThue);
            importRequest.setLoaiChuKy("TOKEN"); // Default
            importRequest.setGhiChu("Import từ file: " + request.getCertificateFilePath());
            importRequest.setIsDefault(request.getIsDefault());
            
            // Gọi service import
            return certificateImportService.importCertificate(importRequest);
            
        } catch (BusinessException e) {
            log.error("Lỗi business khi import certificate từ file: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Lỗi không xác định khi import certificate từ file: ", e);
            throw new BusinessException("Lỗi khi import certificate từ file: " + e.getMessage());
        }
    }
    
    @Override
    public String readCertificateFile(String filePath) throws BusinessException {
        try {
            log.info("Đọc file certificate: {}", filePath);
            
            // Kiểm tra file tồn tại
            Path path = Paths.get(filePath);
            if (!Files.exists(path)) {
                throw new BusinessException("File certificate không tồn tại: " + filePath);
            }
            
            // Kiểm tra extension
            String fileName = path.getFileName().toString().toLowerCase();
            if (!fileName.endsWith(".crt") && !fileName.endsWith(".pem") && !fileName.endsWith(".cer") && 
                !fileName.endsWith(".p12") && !fileName.endsWith(".pfx")) {
                throw new BusinessException("File certificate phải có extension .crt, .pem, .cer, .p12 hoặc .pfx");
            }
            
            // Đọc nội dung file
            String content = Files.readString(path);
            
            // Validate nội dung
            if (content == null || content.trim().isEmpty()) {
                throw new BusinessException("File certificate rỗng");
            }
            
            // Kiểm tra format PEM
            if (!content.contains("-----BEGIN CERTIFICATE-----") || !content.contains("-----END CERTIFICATE-----")) {
                throw new BusinessException("File certificate không đúng format PEM");
            }
            
            log.info("Đọc file certificate thành công, kích thước: {} bytes", content.length());
            return content;
            
        } catch (IOException e) {
            log.error("Lỗi khi đọc file certificate: ", e);
            throw new BusinessException("Không thể đọc file certificate: " + e.getMessage());
        }
    }
    
    @Override
    public String readPrivateKeyFile(String filePath) throws BusinessException {
        try {
            log.info("Đọc file private key: {}", filePath);
            
            // Kiểm tra file tồn tại
            Path path = Paths.get(filePath);
            if (!Files.exists(path)) {
                throw new BusinessException("File private key không tồn tại: " + filePath);
            }
            
            // Kiểm tra extension
            String fileName = path.getFileName().toString().toLowerCase();
            if (!fileName.endsWith(".key") && !fileName.endsWith(".pem")) {
                throw new BusinessException("File private key phải có extension .key hoặc .pem");
            }
            
            // Đọc nội dung file
            String content = Files.readString(path);
            
            // Validate nội dung
            if (content == null || content.trim().isEmpty()) {
                throw new BusinessException("File private key rỗng");
            }
            
            // Kiểm tra format PEM
            if (!content.contains("-----BEGIN") || !content.contains("-----END")) {
                throw new BusinessException("File private key không đúng format PEM");
            }
            
            log.info("Đọc file private key thành công, kích thước: {} bytes", content.length());
            return content;
            
        } catch (IOException e) {
            log.error("Lỗi khi đọc file private key: ", e);
            throw new BusinessException("Không thể đọc file private key: " + e.getMessage());
        }
    }
    
    /**
     * Thử extract private key từ certificate (nếu có)
     */
    private String extractPrivateKeyFromCertificate(String certificateData, String password) throws BusinessException {
        try {
            log.info("Thử extract private key từ certificate");
            
            // Parse certificate để kiểm tra
            String cleanData = certificateData
                    .replace("-----BEGIN CERTIFICATE-----", "")
                    .replace("-----END CERTIFICATE-----", "")
                    .replaceAll("\\s", "");
            
            byte[] certBytes = Base64.getDecoder().decode(cleanData);
            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            X509Certificate certificate = (X509Certificate) certFactory.generateCertificate(new ByteArrayInputStream(certBytes));
            
            log.info("Certificate parsed thành công, subject: {}", certificate.getSubjectX500Principal().getName());
            
            // Thử tìm private key trong Windows Certificate Store
            try {
                KeyStore keyStore = KeyStore.getInstance("Windows-MY");
                keyStore.load(null, null);
                
                // Duyệt qua tất cả aliases để tìm certificate
                java.util.Enumeration<String> aliases = keyStore.aliases();
                while (aliases.hasMoreElements()) {
                    String alias = aliases.nextElement();
                    if (keyStore.isKeyEntry(alias)) {
                        // Kiểm tra certificate có match không
                        java.security.cert.Certificate[] certChain = keyStore.getCertificateChain(alias);
                        if (certChain != null && certChain.length > 0) {
                            X509Certificate storeCert = (X509Certificate) certChain[0];
                            if (storeCert.getSerialNumber().equals(certificate.getSerialNumber()) &&
                                storeCert.getSubjectX500Principal().equals(certificate.getSubjectX500Principal())) {
                                
                                log.info("Tìm thấy certificate trong Windows Certificate Store với alias: {}", alias);
                                
                                // Thử lấy private key
                                PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, password != null ? password.toCharArray() : null);
                                if (privateKey != null) {
                                    log.info("Extract private key thành công từ Windows Certificate Store");
                                    return "-----BEGIN PRIVATE KEY-----\n" + 
                                           Base64.getEncoder().encodeToString(privateKey.getEncoded()) + 
                                           "\n-----END PRIVATE KEY-----";
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                log.warn("Không thể extract private key từ Windows Certificate Store: {}", e.getMessage());
            }
            
            // Nếu không tìm thấy private key
            throw new BusinessException("Không tìm thấy private key. Vui lòng cung cấp file private key riêng hoặc import certificate vào Windows Certificate Store với private key");
            
        } catch (Exception e) {
            log.error("Lỗi khi extract private key từ certificate: ", e);
            throw new BusinessException("Không thể extract private key từ certificate: " + e.getMessage());
        }
    }
    
    /**
     * Extract tên doanh nghiệp từ certificate subject
     */
    private String extractCompanyName(String subject) {
        try {
            log.info("Extract tên doanh nghiệp từ subject: {}", subject);
            
            // Parse subject string để tìm CN (Common Name) hoặc O (Organization)
            // Format: CN=Company Name, O=Organization, C=VN
            String[] parts = subject.split(",");
            for (String part : parts) {
                String trimmed = part.trim();
                if (trimmed.startsWith("CN=")) {
                    String companyName = trimmed.substring(3).trim();
                    log.info("Tìm thấy tên doanh nghiệp từ CN: {}", companyName);
                    return companyName;
                } else if (trimmed.startsWith("O=")) {
                    String companyName = trimmed.substring(2).trim();
                    log.info("Tìm thấy tên doanh nghiệp từ O: {}", companyName);
                    return companyName;
                }
            }
            // Nếu không tìm thấy, trả về subject
            log.warn("Không tìm thấy CN hoặc O trong subject, sử dụng toàn bộ subject");
            return subject;
        } catch (Exception e) {
            log.warn("Không thể extract tên doanh nghiệp từ subject: {}", subject);
            return "Unknown Company";
        }
    }
    
    /**
     * Extract mã số thuế từ certificate subject
     */
    private String extractTaxCode(String subject) {
        try {
            log.info("Extract mã số thuế từ subject: {}", subject);
            
            // Parse subject string để tìm SERIALNUMBER, OU, hoặc các field khác chứa MST
            // Format: CN=Company Name, SERIALNUMBER=0123456789, C=VN
            String[] parts = subject.split(",");
            for (String part : parts) {
                String trimmed = part.trim();
                
                // Tìm SERIALNUMBER
                if (trimmed.startsWith("SERIALNUMBER=")) {
                    String mst = trimmed.substring(13).trim();
                    log.info("Tìm thấy mã số thuế từ SERIALNUMBER: {}", mst);
                    return mst;
                }
                
                // Tìm OU (Organizational Unit)
                if (trimmed.startsWith("OU=")) {
                    String ou = trimmed.substring(3).trim();
                    // Kiểm tra xem OU có phải là mã số thuế không (10-13 số)
                    if (ou.matches("\\d{10,13}")) {
                        log.info("Tìm thấy mã số thuế từ OU: {}", ou);
                        return ou;
                    }
                }
                
                // Tìm trong các field khác có thể chứa MST
                if (trimmed.contains("=")) {
                    String value = trimmed.substring(trimmed.indexOf("=") + 1).trim();
                    // Kiểm tra xem value có phải là mã số thuế không (10-13 số)
                    if (value.matches("\\d{10,13}")) {
                        log.info("Tìm thấy mã số thuế từ field {}: {}", trimmed, value);
                        return value;
                    }
                }
            }
            
            log.warn("Không tìm thấy mã số thuế trong subject");
            return null; // Không tìm thấy mã số thuế
        } catch (Exception e) {
            log.warn("Không thể extract mã số thuế từ subject: {}", subject);
            return null;
        }
    }
    
    /**
     * Đọc file PKCS#12 (.p12/.pfx) và extract certificate + private key
     */
    private P12Data readP12File(String filePath, String password) throws BusinessException {
        try {
            log.info("Đọc file PKCS#12: {}", filePath);
            
            // Kiểm tra file tồn tại
            Path path = Paths.get(filePath);
            if (!Files.exists(path)) {
                throw new BusinessException("File PKCS#12 không tồn tại: " + filePath);
            }
            
            // Kiểm tra extension
            String fileName = path.getFileName().toString().toLowerCase();
            if (!fileName.endsWith(".p12") && !fileName.endsWith(".pfx")) {
                throw new BusinessException("File phải có extension .p12 hoặc .pfx");
            }
            
            // Load KeyStore từ file P12
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            char[] passwordChars = password != null ? password.toCharArray() : null;
            
            try (FileInputStream fis = new FileInputStream(filePath)) {
                keyStore.load(fis, passwordChars);
            }
            
            // Tìm certificate và private key
            Enumeration<String> aliases = keyStore.aliases();
            while (aliases.hasMoreElements()) {
                String alias = aliases.nextElement();
                if (keyStore.isKeyEntry(alias)) {
                    // Lấy certificate
                    X509Certificate certificate = (X509Certificate) keyStore.getCertificate(alias);
                    String certificateData = "-----BEGIN CERTIFICATE-----\n" + 
                                           Base64.getEncoder().encodeToString(certificate.getEncoded()) + 
                                           "\n-----END CERTIFICATE-----";
                    
                    // Lấy private key
                    PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, passwordChars);
                    String privateKeyData = "-----BEGIN PRIVATE KEY-----\n" + 
                                          Base64.getEncoder().encodeToString(privateKey.getEncoded()) + 
                                          "\n-----END PRIVATE KEY-----";
                    
                    log.info("Extract thành công certificate và private key từ file P12 với alias: {}", alias);
                    return new P12Data(certificateData, privateKeyData);
                }
            }
            
            throw new BusinessException("Không tìm thấy certificate và private key trong file PKCS#12");
            
        } catch (Exception e) {
            log.error("Lỗi khi đọc file PKCS#12: ", e);
            throw new BusinessException("Không thể đọc file PKCS#12: " + e.getMessage());
        }
    }
    
    /**
     * Clean certificate data - xóa header/footer
     */
    private String cleanCertificateData(String certificateData) {
        if (certificateData == null) return null;
        
        return certificateData
                .replace("-----BEGIN CERTIFICATE-----", "")
                .replace("-----END CERTIFICATE-----", "")
                .replaceAll("\\s", "");
    }
    
    /**
     * Clean private key data - xóa header/footer
     */
    private String cleanPrivateKeyData(String privateKeyData) {
        if (privateKeyData == null) return null;
        
        return privateKeyData
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replace("-----BEGIN RSA PRIVATE KEY-----", "")
                .replace("-----END RSA PRIVATE KEY-----", "")
                .replaceAll("\\s", "");
    }
}
