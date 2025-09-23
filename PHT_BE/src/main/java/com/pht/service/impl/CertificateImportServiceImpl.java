package com.pht.service.impl;

import java.io.ByteArrayInputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.ZoneId;
import java.util.Base64;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pht.entity.ChukySo;
import com.pht.exception.BusinessException;
import com.pht.model.request.ImportCertificateRequest;
import com.pht.model.response.ImportCertificateResponse;
import com.pht.repository.ChukySoRepository;
import com.pht.service.CertificateImportService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CertificateImportServiceImpl implements CertificateImportService {
    
    private final ChukySoRepository chukySoRepository;
    
    @Override
    @Transactional
    public ImportCertificateResponse importCertificate(ImportCertificateRequest request) throws BusinessException {
        log.info("Bắt đầu import certificate cho doanh nghiệp: {}", request.getTenDoanhNghiep());
        
        try {
            // Validate certificate
            validateCertificate(request.getCertificateData(), request.getPrivateKeyData(), request.getPassword());
            
            // Parse certificate
            X509Certificate certificate = parseCertificate(request.getCertificateData());
            
            // Parse private key
            PrivateKey privateKey = parsePrivateKey(request.getPrivateKeyData(), request.getPassword());
            
            // Extract public key
            PublicKey publicKey = certificate.getPublicKey();
            
            // Kiểm tra serial number đã tồn tại chưa
            String serialNumber = certificate.getSerialNumber().toString(16).toUpperCase();
            if (chukySoRepository.existsBySerialNumber(serialNumber)) {
                throw new BusinessException("Chữ ký số với serial number " + serialNumber + " đã tồn tại trong hệ thống");
            }
            
            // Tính thumbprint để kiểm tra trùng lặp
            String thumbprint = calculateThumbprint(certificate);
            if (thumbprint != null && chukySoRepository.existsByThumbprint(thumbprint)) {
                throw new BusinessException("Chữ ký số với thumbprint " + thumbprint + " đã tồn tại trong hệ thống");
            }
            
            // Nếu đặt làm mặc định, bỏ mặc định của các chữ ký khác
            if (Boolean.TRUE.equals(request.getIsDefault())) {
                chukySoRepository.findDefaultCertificate().ifPresent(existingDefault -> {
                    existingDefault.setIsDefault(false);
                    chukySoRepository.save(existingDefault);
                });
            }
            
            // Tạo entity ChukySo
            ChukySo chukySo = new ChukySo();
            chukySo.setSerialNumber(serialNumber);
            chukySo.setSubject(certificate.getSubjectX500Principal().getName());
            chukySo.setIssuer(certificate.getIssuerX500Principal().getName());
            chukySo.setValidFrom(certificate.getNotBefore().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
            chukySo.setValidTo(certificate.getNotAfter().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
            
            // Thông tin doanh nghiệp
            chukySo.setMaDoanhNghiep(request.getMaDoanhNghiep());
            chukySo.setTenDoanhNghiep(request.getTenDoanhNghiep());
            chukySo.setMaSoThue(request.getMaSoThue());
            
            // Thông tin certificate - lưu clean data (không có header/footer)
            chukySo.setCertificateData(cleanCertificateData(request.getCertificateData()));
            chukySo.setPrivateKey(cleanPrivateKeyData(request.getPrivateKeyData())); // Lưu clean data
            chukySo.setPublicKey(Base64.getEncoder().encodeToString(publicKey.getEncoded()));
            
            // Lưu thông tin thuật toán ký
            chukySo.setSignatureAlgorithm(certificate.getSigAlgName());
            chukySo.setHashAlgorithm(extractHashAlgorithm(certificate.getSigAlgName()));
            
            // Lưu thumbprint (đã tính ở trên)
            chukySo.setThumbprint(thumbprint);
            
            // Thông tin trạng thái
            chukySo.setTrangThai("ACTIVE");
            chukySo.setLoaiChuKy(request.getLoaiChuKy());
            chukySo.setGhiChu(request.getGhiChu());
            chukySo.setPassword(request.getPassword());
            chukySo.setIsActive(true);
            chukySo.setIsDefault(Boolean.TRUE.equals(request.getIsDefault()));
            
            // Lưu vào database
            ChukySo savedChukySo = chukySoRepository.save(chukySo);
            
            log.info("Import certificate thành công");
            
            // Tạo response
            ImportCertificateResponse response = new ImportCertificateResponse();
            response.setId(savedChukySo.getId());
            response.setSerialNumber(savedChukySo.getSerialNumber());
            response.setIssuer(savedChukySo.getIssuer());
            response.setValidFrom(formatDate(savedChukySo.getValidFrom()));
            response.setValidTo(formatDate(savedChukySo.getValidTo()));
            response.setTenDoanhNghiep(savedChukySo.getTenDoanhNghiep());
            response.setMaSoThue(savedChukySo.getMaSoThue());
            response.setLoaiChuKy(savedChukySo.getLoaiChuKy());
            response.setTrangThai(savedChukySo.getTrangThai());
            response.setMessage("Import chữ ký số thành công");
            
            return response;
                    
        } catch (BusinessException e) {
            log.error("Lỗi business khi import certificate: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Lỗi không xác định khi import certificate: ", e);
            throw new BusinessException("Lỗi khi import certificate: " + e.getMessage());
        }
    }
    
    @Override
    public boolean validateCertificate(String certificateData, String privateKeyData, String password) throws BusinessException {
        try {
            log.info("Bắt đầu validate certificate");
            
            // Parse certificate
            X509Certificate certificate = parseCertificate(certificateData);
            
            // Parse private key để validate
            parsePrivateKey(privateKeyData, password);
            
            // Kiểm tra certificate có hợp lệ không
            certificate.checkValidity();
            
            // Kiểm tra algorithm
            PublicKey publicKeyFromCert = certificate.getPublicKey();
            if (!publicKeyFromCert.getAlgorithm().equals("RSA")) {
                throw new BusinessException("Certificate không phải loại RSA");
            }
            
            log.info("Private key và certificate validation thành công");
            
            log.info("Certificate validation thành công");
            return true;
            
        } catch (BusinessException e) {
            log.error("Certificate validation thất bại: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Lỗi khi validate certificate: ", e);
            throw new BusinessException("Certificate không hợp lệ: " + e.getMessage());
        }
    }
    
    /**
     * Parse certificate từ string
     */
    private X509Certificate parseCertificate(String certificateData) throws Exception {
        try {
            // Remove header/footer nếu có
            String cleanData = certificateData
                    .replace("-----BEGIN CERTIFICATE-----", "")
                    .replace("-----END CERTIFICATE-----", "")
                    .replaceAll("\\s", "");
            
            byte[] certBytes = Base64.getDecoder().decode(cleanData);
            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            return (X509Certificate) certFactory.generateCertificate(new ByteArrayInputStream(certBytes));
            
        } catch (Exception e) {
            throw new BusinessException("Không thể parse certificate: " + e.getMessage());
        }
    }
    
    /**
     * Parse private key từ string
     */
    private PrivateKey parsePrivateKey(String privateKeyData, String password) throws Exception {
        try {
            // Remove header/footer nếu có
            String cleanData = privateKeyData
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replace("-----BEGIN RSA PRIVATE KEY-----", "")
                    .replace("-----END RSA PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");
            
            byte[] keyBytes = Base64.getDecoder().decode(cleanData);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(keySpec);
            
        } catch (Exception e) {
            throw new BusinessException("Không thể parse private key: " + e.getMessage());
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
    
    /**
     * Extract hash algorithm từ signature algorithm
     */
    private String extractHashAlgorithm(String signatureAlgorithm) {
        if (signatureAlgorithm == null) return "SHA256";
        
        // Xử lý các format khác nhau
        String sigAlg = signatureAlgorithm.toUpperCase();
        
        if (sigAlg.contains("SHA256") || sigAlg.contains("SHA-256")) {
            return "SHA256";
        } else if (sigAlg.contains("SHA1") || sigAlg.contains("SHA-1")) {
            return "SHA1";
        } else if (sigAlg.contains("SHA384") || sigAlg.contains("SHA-384")) {
            return "SHA384";
        } else if (sigAlg.contains("SHA512") || sigAlg.contains("SHA-512")) {
            return "SHA512";
        }
        
        // Mặc định SHA256
        return "SHA256";
    }
    
    /**
     * Tính thumbprint (SHA-1 hash) của certificate
     */
    private String calculateThumbprint(X509Certificate certificate) {
        try {
            // Tính SHA-1 hash của certificate
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-1");
            byte[] hash = digest.digest(certificate.getEncoded());
            
            // Chuyển thành hex string và format như Windows (viết hoa, có dấu :)
            StringBuilder thumbprint = new StringBuilder();
            for (byte b : hash) {
                thumbprint.append(String.format("%02X", b));
            }
            
            // Format với dấu : mỗi 2 ký tự (như Windows)
            String result = thumbprint.toString();
            StringBuilder formatted = new StringBuilder();
            for (int i = 0; i < result.length(); i += 2) {
                if (i > 0) formatted.append(":");
                formatted.append(result.substring(i, i + 2));
            }
            
            return formatted.toString();
            
        } catch (Exception e) {
            log.error("Lỗi khi tính thumbprint: ", e);
            return null;
        }
    }
    
    /**
     * Format date thành dd/MM/yyyy
     */
    private String formatDate(java.time.LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return dateTime.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
}
