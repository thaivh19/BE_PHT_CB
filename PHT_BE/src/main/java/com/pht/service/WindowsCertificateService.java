package com.pht.service;

import java.util.List;
import com.pht.model.response.ChuKySoResponse;

/**
 * Service để lấy danh sách chữ ký số từ Windows Security
 */
public interface WindowsCertificateService {
    
    /**
     * Lấy danh sách tất cả chữ ký số từ Windows Certificate Store
     * @return Danh sách chữ ký số
     */
    List<ChuKySoResponse> getAllWindowsCertificates();
    
    /**
     * Lấy danh sách chữ ký số đang hợp lệ từ Windows Certificate Store
     * @return Danh sách chữ ký số hợp lệ
     */
    List<ChuKySoResponse> getValidWindowsCertificates();
    
    /**
     * Lấy chữ ký số theo serial number từ Windows Certificate Store
     * @param serialNumber Serial number của certificate
     * @return Thông tin chữ ký số
     */
    ChuKySoResponse getWindowsCertificateBySerialNumber(String serialNumber);
}




