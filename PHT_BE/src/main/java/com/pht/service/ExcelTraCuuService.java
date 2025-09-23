package com.pht.service;

import com.pht.dto.ExcelTraCuuResponse;
import com.pht.exception.BusinessException;

public interface ExcelTraCuuService {
    
    /**
     * Xử lý file Excel và tra cứu thông tin tờ khai
     */
    ExcelTraCuuResponse processExcelAndTraCuu(byte[] fileData, String fileName) throws BusinessException;
    
}


