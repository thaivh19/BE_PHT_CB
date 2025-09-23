package com.pht.service;

import com.pht.entity.StoKhai;
import com.pht.exception.BusinessException;

public interface XmlGenerationService {
    
    /**
     * Tạo XML từ thông tin tờ khai và lưu vào trường XML tương ứng
     * @param toKhaiId ID của tờ khai
     * @param lanKy Lần ký (1: lưu vào KYLAN1_XML, khác: lưu vào KYLAN2_XML)
     * @param serialNumber Serial Number của chữ ký số để ký XML
     */
    String generateAndSaveXml(Long toKhaiId, Integer lanKy, String serialNumber) throws BusinessException;
    
    /**
     * Tạo XML từ thông tin tờ khai và lưu vào trường XML tương ứng (backward compatibility)
     * @param toKhaiId ID của tờ khai
     * @param lanKy Lần ký (1: lưu vào KYLAN1_XML, khác: lưu vào KYLAN2_XML)
     */
    String generateAndSaveXml(Long toKhaiId, Integer lanKy) throws BusinessException;
    
    /**
     * Tạo XML từ đối tượng StoKhai
     */
    String generateXml(StoKhai toKhai) throws BusinessException;
}
