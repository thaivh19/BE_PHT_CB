package com.pht.service;

import com.pht.exception.BusinessException;
import com.pht.model.request.SaveWindowsCertificateRequest;
import com.pht.model.response.ChuKySoResponse;

/**
 * Service để lưu chữ ký số từ Windows Security vào database
 */
public interface WindowsCertificateSaveService {
    
    /**
     * Lưu chữ ký số từ Windows Security vào database
     * @param request Thông tin request để lưu chữ ký số
     * @return Thông tin chữ ký số đã lưu
     * @throws BusinessException Nếu có lỗi trong quá trình lưu
     */
    ChuKySoResponse saveWindowsCertificateToDatabase(SaveWindowsCertificateRequest request) throws BusinessException;
}




