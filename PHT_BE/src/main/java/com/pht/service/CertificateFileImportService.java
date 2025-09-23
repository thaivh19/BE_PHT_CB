package com.pht.service;

import com.pht.exception.BusinessException;
import com.pht.model.request.ImportCertificateFileRequest;
import com.pht.model.response.ImportCertificateResponse;

/**
 * Service để import certificate từ file
 */
public interface CertificateFileImportService {
    
    /**
     * Import certificate từ file
     * @param request Thông tin file và doanh nghiệp
     * @return Thông tin certificate đã import
     * @throws BusinessException Nếu có lỗi
     */
    ImportCertificateResponse importCertificateFromFile(ImportCertificateFileRequest request) throws BusinessException;
    
    /**
     * Đọc nội dung file certificate
     * @param filePath Đường dẫn file
     * @return Nội dung file dạng string
     * @throws BusinessException Nếu không đọc được file
     */
    String readCertificateFile(String filePath) throws BusinessException;
    
    /**
     * Đọc nội dung file private key
     * @param filePath Đường dẫn file
     * @return Nội dung file dạng string
     * @throws BusinessException Nếu không đọc được file
     */
    String readPrivateKeyFile(String filePath) throws BusinessException;
}


