package com.pht.service;

import java.util.List;

import com.pht.entity.ChukySo;

public interface DatabaseCertificateService {
    
    /**
     * Lấy danh sách chữ ký số đang hoạt động từ database
     * @return Danh sách chữ ký số
     */
    List<ChukySo> getActiveCertificates();
    
    /**
     * Tìm chữ ký số theo serial number
     * @param serialNumber Serial number của chữ ký số
     * @return Chữ ký số hoặc null nếu không tìm thấy
     */
    ChukySo findBySerialNumber(String serialNumber);
    
    /**
     * Tìm chữ ký số theo thumbprint
     * @param thumbprint Thumbprint của chữ ký số
     * @return Chữ ký số hoặc null nếu không tìm thấy
     */
    ChukySo findByThumbprint(String thumbprint);
    
    /**
     * Ký XML với chữ ký số từ database
     * @param xmlContent Nội dung XML cần ký
     * @param serialNumber Serial number của chữ ký số
     * @return XML đã được ký
     */
    String signXmlWithDatabaseCertificate(String xmlContent, String serialNumber);
}

