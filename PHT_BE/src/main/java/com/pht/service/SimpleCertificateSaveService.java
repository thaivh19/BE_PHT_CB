package com.pht.service;

import com.pht.exception.BusinessException;
import com.pht.model.request.SaveCertificateBySerialRequest;
import com.pht.model.response.ChuKySoResponse;

/**
 * Service đơn giản để lưu chữ ký số từ Windows Security chỉ với SerialNumber
 */
public interface SimpleCertificateSaveService {
    
    /**
     * Lưu chữ ký số từ Windows Security vào database chỉ với SerialNumber
     * Tự động extract thông tin từ certificate
     * @param request Request chỉ chứa SerialNumber
     * @return Thông tin chữ ký số đã lưu
     * @throws BusinessException Nếu có lỗi trong quá trình lưu
     */
    ChuKySoResponse saveCertificateBySerialNumber(SaveCertificateBySerialRequest request) throws BusinessException;
}




