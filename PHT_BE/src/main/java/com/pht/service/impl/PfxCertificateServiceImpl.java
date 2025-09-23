package com.pht.service.impl;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Enumeration;

import org.springframework.stereotype.Service;

import com.pht.service.PfxCertificateService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PfxCertificateServiceImpl implements PfxCertificateService {
    
    @Override
    public String signXmlWithPfxCertificate(String xmlContent, String pfxFilePath, String password) {
        try {
            // Sử dụng giá trị mặc định nếu không được cung cấp
            String actualPfxPath = (pfxFilePath != null && !pfxFilePath.trim().isEmpty()) 
                ? pfxFilePath 
                : "C:/IDA/HQ/TPBANK.pfx";
            String actualPassword = (password != null && !password.trim().isEmpty()) 
                ? password 
                : "123456";
            
            log.info("Bắt đầu ký XML với certificate từ file PFX: {}", actualPfxPath);
            
            // Lấy certificate và private key từ file PFX
            X509Certificate certificate = getCertificateFromPfx(actualPfxPath, actualPassword);
            PrivateKey privateKey = getPrivateKeyFromPfx(actualPfxPath, actualPassword);
            
            if (certificate == null || privateKey == null) {
                throw new RuntimeException("Không thể lấy certificate hoặc private key từ file PFX");
            }
            
            log.info("Đã lấy certificate và private key thành công từ file PFX");
            
            // Ký XML với certificate từ file PFX
            return signXmlWithRealCertificate(xmlContent, privateKey, certificate);
            
        } catch (Exception e) {
            log.error("Lỗi khi ký XML với certificate từ file PFX: ", e);
            throw new RuntimeException("Lỗi khi ký XML với file PFX: " + e.getMessage(), e);
        }
    }
    
    @Override
    public X509Certificate getCertificateFromPfx(String pfxFilePath, String password) {
        try {
            // Sử dụng giá trị mặc định nếu không được cung cấp
            String actualPfxPath = (pfxFilePath != null && !pfxFilePath.trim().isEmpty()) 
                ? pfxFilePath 
                : "C:/IDA/HQ/TPBANK.pfx";
            String actualPassword = (password != null && !password.trim().isEmpty()) 
                ? password 
                : "123456";
            
            log.info("Lấy certificate từ file PFX: {}", actualPfxPath);
            
            // Kiểm tra file tồn tại
            Path path = Paths.get(actualPfxPath);
            if (!Files.exists(path)) {
                throw new RuntimeException("File PFX không tồn tại: " + pfxFilePath);
            }
            
            // Kiểm tra extension
            String fileName = path.getFileName().toString().toLowerCase();
            if (!fileName.endsWith(".p12") && !fileName.endsWith(".pfx")) {
                throw new RuntimeException("File phải có extension .p12 hoặc .pfx");
            }
            
            // Load KeyStore từ file PFX
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            char[] passwordChars = actualPassword != null ? actualPassword.toCharArray() : null;
            
            try (FileInputStream fis = new FileInputStream(actualPfxPath)) {
                keyStore.load(fis, passwordChars);
            }
            
            // Tìm certificate
            Enumeration<String> aliases = keyStore.aliases();
            while (aliases.hasMoreElements()) {
                String alias = aliases.nextElement();
                if (keyStore.isKeyEntry(alias)) {
                    X509Certificate certificate = (X509Certificate) keyStore.getCertificate(alias);
                    if (certificate != null) {
                        log.info("Tìm thấy certificate với alias: {}", alias);
                        return certificate;
                    }
                }
            }
            
            throw new RuntimeException("Không tìm thấy certificate trong file PFX");
            
        } catch (Exception e) {
            log.error("Lỗi khi lấy certificate từ file PFX: ", e);
            throw new RuntimeException("Không thể lấy certificate từ file PFX: " + e.getMessage(), e);
        }
    }
    
    @Override
    public PrivateKey getPrivateKeyFromPfx(String pfxFilePath, String password) {
        try {
            // Sử dụng giá trị mặc định nếu không được cung cấp
            String actualPfxPath = (pfxFilePath != null && !pfxFilePath.trim().isEmpty()) 
                ? pfxFilePath 
                : "C:/IDA/HQ/TPBANK.pfx";
            String actualPassword = (password != null && !password.trim().isEmpty()) 
                ? password 
                : "123456";
            
            log.info("Lấy private key từ file PFX: {}", actualPfxPath);
            
            // Kiểm tra file tồn tại
            Path path = Paths.get(actualPfxPath);
            if (!Files.exists(path)) {
                throw new RuntimeException("File PFX không tồn tại: " + actualPfxPath);
            }
            
            // Kiểm tra extension
            String fileName = path.getFileName().toString().toLowerCase();
            if (!fileName.endsWith(".p12") && !fileName.endsWith(".pfx")) {
                throw new RuntimeException("File phải có extension .p12 hoặc .pfx");
            }
            
            // Load KeyStore từ file PFX
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            char[] passwordChars = actualPassword != null ? actualPassword.toCharArray() : null;
            
            try (FileInputStream fis = new FileInputStream(actualPfxPath)) {
                keyStore.load(fis, passwordChars);
            }
            
            // Tìm private key
            Enumeration<String> aliases = keyStore.aliases();
            while (aliases.hasMoreElements()) {
                String alias = aliases.nextElement();
                if (keyStore.isKeyEntry(alias)) {
                    PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, passwordChars);
                    if (privateKey != null) {
                        log.info("Tìm thấy private key với alias: {}", alias);
                        return privateKey;
                    }
                }
            }
            
            throw new RuntimeException("Không tìm thấy private key trong file PFX");
            
        } catch (Exception e) {
            log.error("Lỗi khi lấy private key từ file PFX: ", e);
            throw new RuntimeException("Không thể lấy private key từ file PFX: " + e.getMessage(), e);
        }
    }
    
    /**
     * Ký XML với certificate thực từ file PFX
     */
    private String signXmlWithRealCertificate(String xmlContent, PrivateKey privateKey, X509Certificate certificate) {
        try {
            log.info("Ký XML với certificate từ file PFX");
            
            // Parse XML
            javax.xml.parsers.DocumentBuilderFactory factory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            javax.xml.parsers.DocumentBuilder builder = factory.newDocumentBuilder();
            org.w3c.dom.Document doc = builder.parse(new ByteArrayInputStream(xmlContent.getBytes("UTF-8")));
            
            // Tạo signature element
            org.w3c.dom.Element signatureElement = createSignatureElement(doc, certificate, privateKey);
            
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
            log.info("Ký XML thành công với certificate từ file PFX");
            
            return signedXml;
            
        } catch (Exception e) {
            log.error("Lỗi khi ký XML với certificate từ file PFX: ", e);
            throw new RuntimeException("Lỗi khi ký XML với certificate từ file PFX: " + e.getMessage(), e);
        }
    }
    
    /**
     * Tạo signature element với certificate từ file PFX
     */
    private org.w3c.dom.Element createSignatureElement(org.w3c.dom.Document doc, X509Certificate certificate, PrivateKey privateKey) {
        org.w3c.dom.Element signature = doc.createElementNS("http://www.w3.org/2000/09/xmldsig#", "Signature");
        
        // SignedInfo
        org.w3c.dom.Element signedInfo = doc.createElement("SignedInfo");
        
        org.w3c.dom.Element canonicalizationMethod = doc.createElement("CanonicalizationMethod");
        canonicalizationMethod.setAttribute("Algorithm", "http://www.w3.org/TR/2001/REC-xml-c14n-20010315");
        signedInfo.appendChild(canonicalizationMethod);
        
        org.w3c.dom.Element signatureMethod = doc.createElement("SignatureMethod");
        signatureMethod.setAttribute("Algorithm", "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256");
        signedInfo.appendChild(signatureMethod);
        
        org.w3c.dom.Element reference = doc.createElement("Reference");
        reference.setAttribute("URI", "");
        
        org.w3c.dom.Element transforms = doc.createElement("Transforms");
        org.w3c.dom.Element transform = doc.createElement("Transform");
        transform.setAttribute("Algorithm", "http://www.w3.org/2000/09/xmldsig#enveloped-signature");
        transforms.appendChild(transform);
        reference.appendChild(transforms);
        
        org.w3c.dom.Element digestMethod = doc.createElement("DigestMethod");
        digestMethod.setAttribute("Algorithm", "http://www.w3.org/2001/04/xmlenc#sha256");
        reference.appendChild(digestMethod);
        
        org.w3c.dom.Element digestValue = doc.createElement("DigestValue");
        // Tính toán digest thực tế
        String actualDigest = calculateDigest(doc.getDocumentElement());
        digestValue.setTextContent(actualDigest);
        reference.appendChild(digestValue);
        
        signedInfo.appendChild(reference);
        signature.appendChild(signedInfo);
        
        // SignatureValue (tính toán thực tế)
        org.w3c.dom.Element signatureValue = doc.createElement("SignatureValue");
        String actualSignature = calculateSignature(signedInfo, privateKey);
        signatureValue.setTextContent(actualSignature);
        signature.appendChild(signatureValue);
        
        // KeyInfo
        org.w3c.dom.Element keyInfo = doc.createElement("KeyInfo");
        org.w3c.dom.Element x509Data = doc.createElement("X509Data");
        
        // Thêm X509Certificate (public key)
        org.w3c.dom.Element x509Certificate = doc.createElement("X509Certificate");
        try {
            x509Certificate.setTextContent(Base64.getEncoder().encodeToString(certificate.getEncoded()));
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
    private String calculateSignature(org.w3c.dom.Element signedInfo, PrivateKey privateKey) {
        try {
            // Chuyển SignedInfo thành string
            javax.xml.transform.TransformerFactory transformerFactory = javax.xml.transform.TransformerFactory.newInstance();
            javax.xml.transform.Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(javax.xml.transform.OutputKeys.ENCODING, "UTF-8");
            
            StringWriter writer = new StringWriter();
            transformer.transform(new javax.xml.transform.dom.DOMSource(signedInfo), new javax.xml.transform.stream.StreamResult(writer));
            String signedInfoString = writer.toString();
            
            // Tạo signature bằng private key
            java.security.Signature signature = java.security.Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);
            signature.update(signedInfoString.getBytes("UTF-8"));
            byte[] signatureBytes = signature.sign();
            
            // Chuyển thành Base64
            return Base64.getEncoder().encodeToString(signatureBytes);
            
        } catch (Exception e) {
            log.error("Lỗi khi tính signature: ", e);
            throw new RuntimeException("Không thể tính signature: " + e.getMessage(), e);
        }
    }
    
    /**
     * Tính toán digest cho XML element
     */
    private String calculateDigest(org.w3c.dom.Element element) {
        try {
            // Chuyển element thành string
            javax.xml.transform.TransformerFactory transformerFactory = javax.xml.transform.TransformerFactory.newInstance();
            javax.xml.transform.Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(javax.xml.transform.OutputKeys.ENCODING, "UTF-8");
            
            StringWriter writer = new StringWriter();
            transformer.transform(new javax.xml.transform.dom.DOMSource(element), new javax.xml.transform.stream.StreamResult(writer));
            String xmlString = writer.toString();
            
            // Tính hash SHA-256
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(xmlString.getBytes("UTF-8"));
            
            // Chuyển thành Base64
            return Base64.getEncoder().encodeToString(hash);
            
        } catch (Exception e) {
            log.error("Lỗi khi tính digest: ", e);
            throw new RuntimeException("Không thể tính digest: " + e.getMessage(), e);
        }
    }
}
