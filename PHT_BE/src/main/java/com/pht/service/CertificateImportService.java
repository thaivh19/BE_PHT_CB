package com.pht.service;

import com.pht.exception.BusinessException;
import com.pht.model.request.ImportCertificateRequest;
import com.pht.model.response.ImportCertificateResponse;

public interface CertificateImportService {
    
    /**
     * Import chữ ký số từ file certificate
     * @param request Thông tin import certificate
     * @return Thông tin chữ ký số đã import
     * @throws BusinessException Nếu có lỗi trong quá trình import
     */
    ImportCertificateResponse importCertificate(ImportCertificateRequest request) throws BusinessException;
    
    /**
     * Validate certificate data
     * @param certificateData Nội dung certificate
     * @param privateKeyData Nội dung private key
     * @param password Mật khẩu private key
     * @return true nếu certificate hợp lệ
     * @throws BusinessException Nếu certificate không hợp lệ
     */
    boolean validateCertificate(String certificateData, String privateKeyData, String password) throws BusinessException;
}


