package com.pht.service;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;

/**
 * Service để xử lý chữ ký số từ file PFX
 */
public interface PfxCertificateService {
    
    /**
     * Ký XML với certificate từ file PFX
     * @param xmlContent Nội dung XML cần ký
     * @param pfxFilePath Đường dẫn file PFX
     * @param password Mật khẩu file PFX
     * @return XML đã được ký
     */
    String signXmlWithPfxCertificate(String xmlContent, String pfxFilePath, String password);
    
    /**
     * Lấy certificate từ file PFX
     * @param pfxFilePath Đường dẫn file PFX
     * @param password Mật khẩu file PFX
     * @return Certificate
     */
    X509Certificate getCertificateFromPfx(String pfxFilePath, String password);
    
    /**
     * Lấy private key từ file PFX
     * @param pfxFilePath Đường dẫn file PFX
     * @param password Mật khẩu file PFX
     * @return Private key
     */
    PrivateKey getPrivateKeyFromPfx(String pfxFilePath, String password);
}


