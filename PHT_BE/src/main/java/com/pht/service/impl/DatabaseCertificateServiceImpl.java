package com.pht.service.impl;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.List;
import javax.crypto.EncryptedPrivateKeyInfo;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pht.entity.ChukySo;
import com.pht.repository.ChukySoRepository;
import com.pht.service.DatabaseCertificateService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DatabaseCertificateServiceImpl implements DatabaseCertificateService {
    
    private final ChukySoRepository chukySoRepository;
    
    @Override
    public List<ChukySo> getActiveCertificates() {
        log.info("Lấy danh sách chữ ký số đang hoạt động từ database");
        return chukySoRepository.findActiveCertificates();
    }
    
    @Override
    public ChukySo findBySerialNumber(String serialNumber) {
        log.info("Tìm chữ ký số với serial number: {}", serialNumber);
        return chukySoRepository.findBySerialNumber(serialNumber).orElse(null);
    }
    
    @Override
    public ChukySo findByThumbprint(String thumbprint) {
        log.info("Tìm chữ ký số với thumbprint: {}", thumbprint);
        return chukySoRepository.findByThumbprint(thumbprint).orElse(null);
    }
    
    @Override
    @Transactional
    public String signXmlWithDatabaseCertificate(String xmlContent, String serialNumber) {
        try {
            log.info("Bắt đầu ký XML với chữ ký số từ database, serial number: {}", serialNumber);
            
            // Tìm chữ ký số trong database
            ChukySo chukySo = findBySerialNumber(serialNumber);
            if (chukySo == null) {
                throw new RuntimeException("Không tìm thấy chữ ký số với serial number: " + serialNumber);
            }
            
            if (!Boolean.TRUE.equals(chukySo.getIsActive())) {
                throw new RuntimeException("Chữ ký số đã bị vô hiệu hóa");
            }
            
            log.info("Tìm thấy chữ ký số trong database");
            
            // Parse certificate và private key từ database
            X509Certificate certificate = parseCertificateFromDatabase(chukySo.getCertificateData());
            PrivateKey privateKey = parsePrivateKeyFromDatabaseWithFallback(chukySo.getPrivateKey(), chukySo.getPassword(), chukySo.getSerialNumber());
            
            // Sử dụng thông tin thuật toán từ database
            String hashAlgorithm = getHashAlgorithmFromDatabase(chukySo);
            String signatureAlgorithm = getSignatureAlgorithmFromDatabase(chukySo);
            
            // Ký XML với certificate từ database
            return signXmlWithRealCertificate(xmlContent, privateKey, certificate, chukySo, hashAlgorithm, signatureAlgorithm);
            
        } catch (Exception e) {
            log.error("Lỗi khi ký XML với chữ ký số từ database: ", e);
            throw new RuntimeException("Lỗi khi ký XML: " + e.getMessage(), e);
        }
    }
    
    /**
     * Parse certificate từ database
     */
    private X509Certificate parseCertificateFromDatabase(String certificateData) throws Exception {
        try {
            // Clean certificate data trước khi decode
            String cleanedCertificateData = cleanBase64Data(certificateData);
            
            // Data trong database đã được clean (không có header/footer)
            byte[] certBytes = Base64.getDecoder().decode(cleanedCertificateData);
            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            return (X509Certificate) certFactory.generateCertificate(new ByteArrayInputStream(certBytes));
            
        } catch (Exception e) {
            log.error("Lỗi khi parse certificate từ database: {}", e.getMessage());
            log.error("Certificate data: {}", certificateData);
            throw new RuntimeException("Không thể parse certificate từ database: " + e.getMessage());
        }
    }
    
    /**
     * Parse private key từ database
     */
    private PrivateKey parsePrivateKeyFromDatabase(String privateKeyData, String password) throws Exception {
        try {
            log.info("Bắt đầu parse private key từ database, data length: {}", privateKeyData != null ? privateKeyData.length() : 0);
            
            // Clean private key data trước khi decode
            String cleanedPrivateKeyData = cleanBase64Data(privateKeyData);
            log.info("Đã clean private key data, cleaned length: {}", cleanedPrivateKeyData.length());
            
            // Data trong database đã được clean (không có header/footer)
            byte[] keyBytes = decodeBase64WithFallback(cleanedPrivateKeyData);
            log.info("Đã decode private key thành công, key bytes length: {}", keyBytes.length);
            
            // Thử parse private key với nhiều cách khác nhau
            PrivateKey privateKey = parsePrivateKeyWithMultipleMethods(keyBytes, password);
            log.info("Đã tạo private key thành công");
            
            return privateKey;
            
        } catch (Exception e) {
            log.error("Lỗi khi parse private key từ database: {}", e.getMessage());
            log.error("Private key data: {}", privateKeyData);
            throw new RuntimeException("Không thể parse private key từ database: " + e.getMessage());
        }
    }
    
    /**
     * Clean base64 data để loại bỏ các ký tự không hợp lệ
     */
    private String cleanBase64Data(String base64Data) {
        if (base64Data == null || base64Data.trim().isEmpty()) {
            return base64Data;
        }
        
        try {
            log.debug("Cleaning base64 data, original length: {}", base64Data.length());
            
            // Loại bỏ whitespace và newlines
            String cleaned = base64Data.replaceAll("\\s+", "");
            log.debug("After removing whitespace, length: {}", cleaned.length());
            
            // Thay thế các ký tự URL-safe base64 sang standard base64 trước khi loại bỏ ký tự không hợp lệ
            cleaned = cleaned.replace("-", "+").replace("_", "/");
            log.debug("After URL-safe conversion, length: {}", cleaned.length());
            
            // Loại bỏ các ký tự không hợp lệ trong base64
            // Chỉ giữ lại các ký tự hợp lệ: A-Z, a-z, 0-9, +, /, =
            String beforeClean = cleaned;
            cleaned = cleaned.replaceAll("[^A-Za-z0-9+/=]", "");
            log.debug("After removing invalid chars, length: {} (removed {} chars)", cleaned.length(), beforeClean.length() - cleaned.length());
            
            // Thêm padding nếu cần thiết
            int remainder = cleaned.length() % 4;
            if (remainder > 0) {
                cleaned += "=".repeat(4 - remainder);
                log.debug("Added padding, final length: {}", cleaned.length());
            }
            
            log.debug("Cleaned base64 data: original length={}, cleaned length={}", base64Data.length(), cleaned.length());
            log.debug("Original data preview: {}...", base64Data.length() > 50 ? base64Data.substring(0, 50) : base64Data);
            log.debug("Cleaned data preview: {}...", cleaned.length() > 50 ? cleaned.substring(0, 50) : cleaned);
            
            return cleaned;
            
        } catch (Exception e) {
            log.error("Không thể clean base64 data: {}", e.getMessage());
            log.error("Original data: {}", base64Data);
            
            // Fallback: trả về data gốc đã loại bỏ whitespace
            String fallback = base64Data.replaceAll("\\s+", "");
            log.warn("Sử dụng fallback data, length: {}", fallback.length());
            return fallback;
        }
    }
    
    /**
     * Decode base64 với fallback mechanism
     */
    private byte[] decodeBase64WithFallback(String base64Data) throws Exception {
        try {
            // Thử với standard decoder trước
            return Base64.getDecoder().decode(base64Data);
        } catch (Exception e) {
            log.warn("Standard base64 decoder thất bại: {}, thử URL-safe decoder", e.getMessage());
            
            try {
                // Thử với URL-safe decoder
                return Base64.getUrlDecoder().decode(base64Data);
            } catch (Exception urlE) {
                log.error("URL-safe decoder cũng thất bại: {}", urlE.getMessage());
                
                // Thử với MIME decoder
                try {
                    return Base64.getMimeDecoder().decode(base64Data);
                } catch (Exception mimeE) {
                    log.error("MIME decoder cũng thất bại: {}", mimeE.getMessage());
                    throw new RuntimeException("Tất cả các decoder đều thất bại: " + e.getMessage(), e);
                }
            }
        }
    }
    
    /**
     * Parse private key từ database với fallback sang Windows Certificate Store
     */
    private PrivateKey parsePrivateKeyFromDatabaseWithFallback(String privateKeyData, String password, String serialNumber) throws Exception {
        try {
            // Thử parse từ database trước
            return parsePrivateKeyFromDatabase(privateKeyData, password);
        } catch (Exception e) {
            log.warn("Không thể parse private key từ database: {}, thử Windows Certificate Store", e.getMessage());
            
            // Fallback: thử lấy từ Windows Certificate Store
            try {
                PrivateKey privateKey = findPrivateKeyInWindowsStore(serialNumber);
                if (privateKey != null) {
                    log.info("Thành công lấy private key từ Windows Certificate Store");
                    return privateKey;
                }
            } catch (Exception windowsE) {
                log.error("Windows Certificate Store cũng thất bại: {}", windowsE.getMessage());
            }
            
            // Nếu cả hai đều thất bại, throw exception gốc
            throw e;
        }
    }
    
    /**
     * Tìm private key trong Windows Certificate Store
     */
    private PrivateKey findPrivateKeyInWindowsStore(String serialNumber) throws Exception {
        try {
            java.security.KeyStore keyStore = java.security.KeyStore.getInstance("Windows-MY");
            keyStore.load(null, null);
            
            java.util.Enumeration<String> aliases = keyStore.aliases();
            while (aliases.hasMoreElements()) {
                String alias = aliases.nextElement();
                
                if (keyStore.isKeyEntry(alias)) {
                    java.security.cert.X509Certificate cert = (java.security.cert.X509Certificate) keyStore.getCertificate(alias);
                    if (cert != null) {
                        String certSerialNumber = cert.getSerialNumber().toString(16).toUpperCase();
                        if (certSerialNumber.equals(serialNumber)) {
                            log.info("Tìm thấy certificate trong Windows Store với serial: {} và alias: {}", serialNumber, alias);
                            
                            // Lấy private key
                            PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, null);
                            if (privateKey != null) {
                                log.info("Lấy private key thành công từ Windows Store");
                                return privateKey;
                            }
                        }
                    }
                }
            }
            
            log.warn("Không tìm thấy certificate với serial number: {} trong Windows Store", serialNumber);
            return null;
            
        } catch (Exception e) {
            log.error("Lỗi khi tìm certificate trong Windows Store: ", e);
            throw e;
        }
    }
    
    /**
     * Parse private key với nhiều phương pháp khác nhau
     */
    private PrivateKey parsePrivateKeyWithMultipleMethods(byte[] keyBytes, String password) throws Exception {
        // Log thông tin về key bytes để debug
        log.debug("Private key bytes length: {}", keyBytes.length);
        log.debug("Private key bytes preview: {}", bytesToHex(keyBytes, 32));
        
        // Detect format của private key
        String keyFormat = detectPrivateKeyFormat(keyBytes);
        log.info("Detected private key format: {}", keyFormat);
        // Method 1: Thử PKCS#8 format (không mã hóa)
        try {
            log.info("Thử parse private key với PKCS#8 format (không mã hóa)");
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
            log.info("Thành công với PKCS#8 format (không mã hóa)");
            return privateKey;
        } catch (Exception e) {
            log.warn("PKCS#8 format (không mã hóa) thất bại: {}", e.getMessage());
            log.warn("Exception type: {}", e.getClass().getSimpleName());
        }
        
        // Method 2: Thử PKCS#8 format (có mã hóa) nếu có password
        if (password != null && !password.trim().isEmpty()) {
            try {
                log.info("Thử parse private key với PKCS#8 format (có mã hóa), password length: {}", password.length());
                PrivateKey privateKey = parseEncryptedPrivateKey(keyBytes, password);
                log.info("Thành công với PKCS#8 format (có mã hóa)");
                return privateKey;
            } catch (Exception e) {
                log.warn("PKCS#8 format (có mã hóa) thất bại: {}", e.getMessage());
                log.warn("Exception type: {}", e.getClass().getSimpleName());
            }
        } else {
            log.info("Không có password, bỏ qua encrypted private key parsing");
        }
        
        // Method 3: Thử PKCS#1 format (RSA private key)
        try {
            log.debug("Thử parse private key với PKCS#1 format");
            java.security.spec.RSAPrivateCrtKeySpec keySpec = parsePKCS1PrivateKey(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
            log.info("Thành công với PKCS#1 format");
            return privateKey;
        } catch (Exception e) {
            log.debug("PKCS#1 format thất bại: {}", e.getMessage());
        }
        
        // Method 4: Thử với các thuật toán khác
        String[] algorithms = {"RSA", "DSA", "EC"};
        for (String algorithm : algorithms) {
            try {
                log.info("Thử parse private key với thuật toán: {}", algorithm);
                PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
                KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
                PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
                log.info("Thành công với thuật toán: {}", algorithm);
                return privateKey;
            } catch (Exception e) {
                log.warn("Thuật toán {} thất bại: {}", algorithm, e.getMessage());
                log.warn("Exception type: {}", e.getClass().getSimpleName());
            }
        }
        
        // Method 5: Thử với raw bytes và các format khác
        try {
            log.info("Thử parse private key với raw bytes");
            PrivateKey privateKey = parseRawPrivateKey(keyBytes);
            log.info("Thành công với raw bytes parsing");
            return privateKey;
        } catch (Exception e) {
            log.warn("Raw bytes parsing thất bại: {}", e.getMessage());
            log.warn("Exception type: {}", e.getClass().getSimpleName());
        }
        
        // Log thông tin chi tiết về key bytes để debug
        log.error("Tất cả phương pháp parse private key đều thất bại");
        log.error("Key bytes length: {}", keyBytes.length);
        log.error("Key bytes hex (first 64 bytes): {}", bytesToHex(keyBytes, 64));
        log.error("Key format detected: {}", keyFormat);
        
        throw new RuntimeException("Không thể parse private key với bất kỳ phương pháp nào. Key length: " + keyBytes.length + ", Format: " + keyFormat);
    }
    
    /**
     * Parse encrypted private key
     */
    private PrivateKey parseEncryptedPrivateKey(byte[] keyBytes, String password) throws Exception {
        try {
            // Tạo EncryptedPrivateKeyInfo từ keyBytes
            EncryptedPrivateKeyInfo encryptedPrivateKeyInfo = new EncryptedPrivateKeyInfo(keyBytes);
            
            // Lấy thuật toán mã hóa
            String algorithm = encryptedPrivateKeyInfo.getAlgName();
            log.debug("Thuật toán mã hóa private key: {}", algorithm);
            
            // Tạo SecretKeyFactory để giải mã
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(algorithm);
            
            // Tạo PBEKeySpec từ password
            PBEKeySpec pbeKeySpec = new PBEKeySpec(password.toCharArray());
            
            // Tạo secret key từ password
            javax.crypto.SecretKey secretKey = secretKeyFactory.generateSecret(pbeKeySpec);
            
            // Giải mã private key
            PKCS8EncodedKeySpec keySpec = encryptedPrivateKeyInfo.getKeySpec(secretKey);
            
            // Tạo private key từ keySpec
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(keySpec);
            
        } catch (Exception e) {
            log.error("Lỗi khi parse encrypted private key: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Detect format của private key
     */
    private String detectPrivateKeyFormat(byte[] keyBytes) {
        if (keyBytes == null || keyBytes.length < 4) {
            return "UNKNOWN";
        }
        
        // Kiểm tra PEM format (text format)
        try {
            String pemString = new String(keyBytes, "UTF-8");
            if (pemString.contains("-----BEGIN") && pemString.contains("-----END")) {
                if (pemString.contains("PRIVATE KEY")) {
                    return "PEM_PKCS#8";
                } else if (pemString.contains("RSA PRIVATE KEY")) {
                    return "PEM_PKCS#1";
                } else if (pemString.contains("ENCRYPTED")) {
                    return "PEM_ENCRYPTED";
                }
                return "PEM_UNKNOWN";
            }
        } catch (Exception e) {
            // Không phải text format
        }
        
        // Kiểm tra PKCS#8 format (bắt đầu với 0x30)
        if (keyBytes[0] == 0x30) {
            return "DER_PKCS#8";
        }
        
        // Kiểm tra PKCS#1 format (bắt đầu với 0x30 0x82)
        if (keyBytes.length >= 2 && keyBytes[0] == 0x30 && keyBytes[1] == (byte)0x82) {
            return "DER_PKCS#1";
        }
        
        // Kiểm tra encrypted format
        try {
            new EncryptedPrivateKeyInfo(keyBytes);
            return "DER_ENCRYPTED_PKCS#8";
        } catch (Exception e) {
            // Không phải encrypted format
        }
        
        // Kiểm tra các format khác
        if (keyBytes.length >= 4) {
            String hex = bytesToHex(keyBytes, 8);
            log.debug("Key bytes hex preview: {}", hex);
        }
        
        return "UNKNOWN";
    }
    
    /**
     * Convert bytes to hex string for debugging
     */
    private String bytesToHex(byte[] bytes, int maxLength) {
        if (bytes == null) return "null";
        
        int length = Math.min(bytes.length, maxLength);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(String.format("%02x", bytes[i]));
        }
        if (bytes.length > maxLength) {
            sb.append("...");
        }
        return sb.toString();
    }
    
    /**
     * Parse raw private key với các format khác nhau
     */
    private PrivateKey parseRawPrivateKey(byte[] keyBytes) throws Exception {
        // Thử với DER format
        try {
            log.debug("Thử parse với DER format");
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            log.debug("DER format thất bại: {}", e.getMessage());
        }
        
        // Thử với PEM format (nếu có header/footer)
        try {
            log.debug("Thử parse với PEM format");
            String pemString = new String(keyBytes, "UTF-8");
            if (pemString.contains("-----BEGIN") && pemString.contains("-----END")) {
                // Đây là PEM format, cần extract base64 content
                String base64Content = pemString
                    .replaceAll("-----BEGIN.*-----", "")
                    .replaceAll("-----END.*-----", "")
                    .replaceAll("\\s", "");
                
                byte[] pemBytes = Base64.getDecoder().decode(base64Content);
                PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(pemBytes);
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                return keyFactory.generatePrivate(keySpec);
            }
        } catch (Exception e) {
            log.debug("PEM format thất bại: {}", e.getMessage());
        }
        
        throw new UnsupportedOperationException("Raw private key parsing không thành công");
    }
    
    /**
     * Parse PKCS#1 RSA private key
     */
    private java.security.spec.RSAPrivateCrtKeySpec parsePKCS1PrivateKey(byte[] keyBytes) throws Exception {
        // Đây là implementation đơn giản, có thể cần cải thiện tùy thuộc vào format cụ thể
        // Thường PKCS#1 private key có format khác với PKCS#8
        throw new UnsupportedOperationException("PKCS#1 parsing chưa được implement");
    }
    
    /**
     * Lấy thuật toán hash từ database
     */
    private String getHashAlgorithmFromDatabase(ChukySo chukySo) {
        String hashAlg = chukySo.getHashAlgorithm();
        if (hashAlg != null && !hashAlg.trim().isEmpty()) {
            // Convert từ format database sang XML URI
            return convertHashAlgorithmToXmlUri(hashAlg);
        }
        
        // Fallback: xác định từ certificate
        try {
            X509Certificate certificate = parseCertificateFromDatabase(chukySo.getCertificateData());
            return determineHashAlgorithm(certificate);
        } catch (Exception e) {
            log.warn("Không thể parse certificate để xác định hash algorithm, sử dụng mặc định");
            return "http://www.w3.org/2001/04/xmlenc#sha256";
        }
    }
    
    /**
     * Lấy thuật toán ký từ database
     */
    private String getSignatureAlgorithmFromDatabase(ChukySo chukySo) {
        String sigAlg = chukySo.getSignatureAlgorithm();
        if (sigAlg != null && !sigAlg.trim().isEmpty()) {
            // Convert từ format database sang XML URI
            return convertSignatureAlgorithmToXmlUri(sigAlg);
        }
        
        // Fallback: xác định từ certificate
        try {
            X509Certificate certificate = parseCertificateFromDatabase(chukySo.getCertificateData());
            return determineSignatureAlgorithm(certificate);
        } catch (Exception e) {
            log.warn("Không thể parse certificate để xác định signature algorithm, sử dụng mặc định");
            return "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256";
        }
    }
    
    /**
     * Convert hash algorithm từ database format sang XML URI
     */
    private String convertHashAlgorithmToXmlUri(String hashAlgorithm) {
        String hash = hashAlgorithm.toUpperCase();
        
        if (hash.contains("SHA256") || hash.contains("SHA-256")) {
            return "http://www.w3.org/2001/04/xmlenc#sha256";
        } else if (hash.contains("SHA1") || hash.contains("SHA-1")) {
            return "http://www.w3.org/2000/09/xmldsig#sha1";
        } else if (hash.contains("SHA384") || hash.contains("SHA-384")) {
            return "http://www.w3.org/2001/04/xmlenc#sha384";
        } else if (hash.contains("SHA512") || hash.contains("SHA-512")) {
            return "http://www.w3.org/2001/04/xmlenc#sha512";
        }
        
        return "http://www.w3.org/2001/04/xmlenc#sha256";
    }
    
    /**
     * Convert signature algorithm từ database format sang XML URI
     */
    private String convertSignatureAlgorithmToXmlUri(String signatureAlgorithm) {
        String sig = signatureAlgorithm.toUpperCase();
        
        if (sig.contains("SHA256") || sig.contains("SHA-256")) {
            return "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256";
        } else if (sig.contains("SHA1") || sig.contains("SHA-1")) {
            return "http://www.w3.org/2000/09/xmldsig#rsa-sha1";
        } else if (sig.contains("SHA384") || sig.contains("SHA-384")) {
            return "http://www.w3.org/2001/04/xmldsig-more#rsa-sha384";
        } else if (sig.contains("SHA512") || sig.contains("SHA-512")) {
            return "http://www.w3.org/2001/04/xmldsig-more#rsa-sha512";
        }
        
        return "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256";
    }
    
    /**
     * Xác định thuật toán hash dựa trên certificate (fallback)
     */
    private String determineHashAlgorithm(X509Certificate certificate) {
        try {
            String sigAlg = certificate.getSigAlgName();
            
            if (sigAlg != null) {
                if (sigAlg.contains("SHA256") || sigAlg.contains("SHA-256")) {
                    return "http://www.w3.org/2001/04/xmlenc#sha256";
                } else if (sigAlg.contains("SHA1") || sigAlg.contains("SHA-1")) {
                    return "http://www.w3.org/2000/09/xmldsig#sha1";
                } else if (sigAlg.contains("SHA384") || sigAlg.contains("SHA-384")) {
                    return "http://www.w3.org/2001/04/xmlenc#sha384";
                } else if (sigAlg.contains("SHA512") || sigAlg.contains("SHA-512")) {
                    return "http://www.w3.org/2001/04/xmlenc#sha512";
                }
            }
            
            return "http://www.w3.org/2001/04/xmlenc#sha256";
            
        } catch (Exception e) {
            log.error("Lỗi khi xác định thuật toán hash: ", e);
            return "http://www.w3.org/2001/04/xmlenc#sha256";
        }
    }
    
    /**
     * Xác định thuật toán ký dựa trên certificate
     */
    private String determineSignatureAlgorithm(X509Certificate certificate) {
        try {
            String sigAlg = certificate.getSigAlgName();
            
            if (sigAlg != null) {
                if (sigAlg.contains("SHA256") || sigAlg.contains("SHA-256")) {
                    return "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256";
                } else if (sigAlg.contains("SHA1") || sigAlg.contains("SHA-1")) {
                    return "http://www.w3.org/2000/09/xmldsig#rsa-sha1";
                } else if (sigAlg.contains("SHA384") || sigAlg.contains("SHA-384")) {
                    return "http://www.w3.org/2001/04/xmldsig-more#rsa-sha384";
                } else if (sigAlg.contains("SHA512") || sigAlg.contains("SHA-512")) {
                    return "http://www.w3.org/2001/04/xmldsig-more#rsa-sha512";
                }
            }
            
            return "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256";
            
        } catch (Exception e) {
            log.error("Lỗi khi xác định thuật toán ký: ", e);
            return "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256";
        }
    }
    
    /**
     * Ký XML với certificate thực từ database
     */
    private String signXmlWithRealCertificate(String xmlContent, PrivateKey privateKey, X509Certificate certificate, 
                                            ChukySo chukySo, String hashAlgorithm, String signatureAlgorithm) {
        try {
            log.info("Ký XML với certificate từ database");
            
            // Parse XML
            javax.xml.parsers.DocumentBuilderFactory factory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            javax.xml.parsers.DocumentBuilder builder = factory.newDocumentBuilder();
            org.w3c.dom.Document doc = builder.parse(new ByteArrayInputStream(xmlContent.getBytes("UTF-8")));
            
            // Tạo signature element với thuật toán phù hợp
            org.w3c.dom.Element signatureElement = createSignatureElement(doc, certificate, privateKey, chukySo, hashAlgorithm, signatureAlgorithm);
            
            // Thêm signature vào XML
            doc.getDocumentElement().appendChild(signatureElement);
            
            // Convert Document to String
            javax.xml.transform.TransformerFactory transformerFactory = javax.xml.transform.TransformerFactory.newInstance();
            javax.xml.transform.Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(javax.xml.transform.OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "yes");
            
            StringWriter writer = new StringWriter();
            transformer.transform(new javax.xml.transform.dom.DOMSource(doc), new javax.xml.transform.stream.StreamResult(writer));
            
            String signedXml = writer.toString();
            log.info("Ký XML thành công với certificate từ database");
            
            return signedXml;
            
        } catch (Exception e) {
            log.error("Lỗi khi ký XML với certificate từ database: ", e);
            throw new RuntimeException("Lỗi khi ký XML với certificate từ database: " + e.getMessage(), e);
        }
    }
    
    /**
     * Tạo signature element với certificate từ database
     */
    private org.w3c.dom.Element createSignatureElement(org.w3c.dom.Document doc, X509Certificate certificate, 
                                                      PrivateKey privateKey, ChukySo chukySo, 
                                                      String hashAlgorithm, String signatureAlgorithm) {
        org.w3c.dom.Element signature = doc.createElementNS("http://www.w3.org/2000/09/xmldsig#", "Signature");
        
        // SignedInfo
        org.w3c.dom.Element signedInfo = doc.createElement("SignedInfo");
        
        org.w3c.dom.Element canonicalizationMethod = doc.createElement("CanonicalizationMethod");
        canonicalizationMethod.setAttribute("Algorithm", "http://www.w3.org/TR/2001/REC-xml-c14n-20010315");
        signedInfo.appendChild(canonicalizationMethod);
        
        org.w3c.dom.Element signatureMethod = doc.createElement("SignatureMethod");
        signatureMethod.setAttribute("Algorithm", signatureAlgorithm);
        signedInfo.appendChild(signatureMethod);
        
        org.w3c.dom.Element reference = doc.createElement("Reference");
        reference.setAttribute("URI", "");
        
        org.w3c.dom.Element transforms = doc.createElement("Transforms");
        org.w3c.dom.Element transform = doc.createElement("Transform");
        transform.setAttribute("Algorithm", "http://www.w3.org/2000/09/xmldsig#enveloped-signature");
        transforms.appendChild(transform);
        reference.appendChild(transforms);
        
        org.w3c.dom.Element digestMethod = doc.createElement("DigestMethod");
        digestMethod.setAttribute("Algorithm", hashAlgorithm);
        reference.appendChild(digestMethod);
        
        org.w3c.dom.Element digestValue = doc.createElement("DigestValue");
        // Tính toán digest thực tế với thuật toán phù hợp
        String actualDigest = calculateDigest(doc.getDocumentElement(), hashAlgorithm);
        digestValue.setTextContent(actualDigest);
        reference.appendChild(digestValue);
        
        signedInfo.appendChild(reference);
        signature.appendChild(signedInfo);
        
        // SignatureValue (tính toán thực tế)
        org.w3c.dom.Element signatureValue = doc.createElement("SignatureValue");
        String actualSignature = calculateSignature(signedInfo, privateKey, signatureAlgorithm);
        signatureValue.setTextContent(actualSignature);
        signature.appendChild(signatureValue);
        
        // KeyInfo
        org.w3c.dom.Element keyInfo = doc.createElement("KeyInfo");
        org.w3c.dom.Element x509Data = doc.createElement("X509Data");
        
        // Thêm X509Certificate (public key)
        org.w3c.dom.Element x509Certificate = doc.createElement("X509Certificate");
        try {
            x509Certificate.setTextContent(java.util.Base64.getEncoder().encodeToString(certificate.getEncoded()));
        } catch (java.security.cert.CertificateEncodingException e) {
            log.error("Lỗi khi encode certificate: ", e);
            throw new RuntimeException("Không thể encode certificate: " + e.getMessage(), e);
        }
        x509Data.appendChild(x509Certificate);
        
        // Thêm X509IssuerSerial
        org.w3c.dom.Element x509IssuerSerial = doc.createElement("X509IssuerSerial");
        
        org.w3c.dom.Element x509IssuerName = doc.createElement("X509IssuerName");
        x509IssuerName.setTextContent(certificate.getIssuerX500Principal().getName());
        x509IssuerSerial.appendChild(x509IssuerName);
        
        org.w3c.dom.Element x509SerialNumber = doc.createElement("X509SerialNumber");
        x509SerialNumber.setTextContent(certificate.getSerialNumber().toString());
        x509IssuerSerial.appendChild(x509SerialNumber);
        
        x509Data.appendChild(x509IssuerSerial);
        keyInfo.appendChild(x509Data);
        signature.appendChild(keyInfo);
        
        return signature;
    }
    
    /**
     * Tính toán signature thực tế cho SignedInfo
     */
    private String calculateSignature(org.w3c.dom.Element signedInfo, PrivateKey privateKey, String signatureAlgorithm) {
        try {
            // Chuyển SignedInfo thành string
            javax.xml.transform.TransformerFactory transformerFactory = javax.xml.transform.TransformerFactory.newInstance();
            javax.xml.transform.Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(javax.xml.transform.OutputKeys.ENCODING, "UTF-8");
            
            StringWriter writer = new StringWriter();
            transformer.transform(new javax.xml.transform.dom.DOMSource(signedInfo), new javax.xml.transform.stream.StreamResult(writer));
            String signedInfoString = writer.toString();
            
            // Xác định thuật toán signature từ URI
            String algorithm;
            if (signatureAlgorithm.contains("rsa-sha256")) {
                algorithm = "SHA256withRSA";
            } else if (signatureAlgorithm.contains("rsa-sha1")) {
                algorithm = "SHA1withRSA";
            } else if (signatureAlgorithm.contains("rsa-sha384")) {
                algorithm = "SHA384withRSA";
            } else if (signatureAlgorithm.contains("rsa-sha512")) {
                algorithm = "SHA512withRSA";
            } else {
                algorithm = "SHA256withRSA"; // Mặc định
            }
            
            // Tạo signature bằng private key
            java.security.Signature signature = java.security.Signature.getInstance(algorithm);
            signature.initSign(privateKey);
            signature.update(signedInfoString.getBytes("UTF-8"));
            byte[] signatureBytes = signature.sign();
            
            // Chuyển thành Base64
            return Base64.getEncoder().encodeToString(signatureBytes);
            
        } catch (Exception e) {
            log.error("Lỗi khi tính signature với thuật toán {}: ", signatureAlgorithm, e);
            throw new RuntimeException("Không thể tính signature: " + e.getMessage(), e);
        }
    }
    
    /**
     * Tính toán digest cho XML element với thuật toán phù hợp
     */
    private String calculateDigest(org.w3c.dom.Element element, String hashAlgorithm) {
        try {
            // Chuyển element thành string
            javax.xml.transform.TransformerFactory transformerFactory = javax.xml.transform.TransformerFactory.newInstance();
            javax.xml.transform.Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(javax.xml.transform.OutputKeys.ENCODING, "UTF-8");
            
            StringWriter writer = new StringWriter();
            transformer.transform(new javax.xml.transform.dom.DOMSource(element), new javax.xml.transform.stream.StreamResult(writer));
            String xmlString = writer.toString();
            
            // Xác định thuật toán hash từ URI
            String algorithm;
            if (hashAlgorithm.contains("sha256")) {
                algorithm = "SHA-256";
            } else if (hashAlgorithm.contains("sha1")) {
                algorithm = "SHA-1";
            } else if (hashAlgorithm.contains("sha384")) {
                algorithm = "SHA-384";
            } else if (hashAlgorithm.contains("sha512")) {
                algorithm = "SHA-512";
            } else {
                algorithm = "SHA-256"; // Mặc định
            }
            
            // Tính hash
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance(algorithm);
            byte[] hash = digest.digest(xmlString.getBytes("UTF-8"));
            
            // Chuyển thành Base64
            return Base64.getEncoder().encodeToString(hash);
            
        } catch (Exception e) {
            log.error("Lỗi khi tính digest với thuật toán {}: ", hashAlgorithm, e);
            throw new RuntimeException("Không thể tính digest: " + e.getMessage(), e);
        }
    }
}
