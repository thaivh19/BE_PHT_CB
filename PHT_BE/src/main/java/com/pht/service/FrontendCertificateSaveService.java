package com.pht.service;

import com.pht.exception.BusinessException;
import com.pht.model.request.SaveCertificateFromFrontendRequest;
import com.pht.model.response.ChuKySoResponse;

/**
 * Service để lưu chữ ký số từ frontend
 */
public interface FrontendCertificateSaveService {
    
    /**
     * Lưu chữ ký số từ frontend vào database
     * 
     * @param request thông tin chữ ký số từ frontend
     * @return thông tin chữ ký số đã lưu
     * @throws BusinessException nếu có lỗi xảy ra
     */
    ChuKySoResponse saveCertificateFromFrontend(SaveCertificateFromFrontendRequest request) throws BusinessException;
}




