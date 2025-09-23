package com.pht.service;

import java.util.List;

import com.pht.dto.ExcelTraCuuResponse;
import com.pht.dto.ToKhaiTraCuuResponse;
import com.pht.entity.SDoiSoat;
import com.pht.entity.SDoiSoatCt;
import com.pht.exception.BusinessException;

public interface ExcelExportService {
    
    /**
     * Export danh sách tra cứu tờ khai ra file Excel
     */
    byte[] exportToKhaiTraCuuToExcel(List<ToKhaiTraCuuResponse> data, String fileName) throws BusinessException;
    
    /**
     * Export kết quả Excel tra cứu ra file Excel
     */
    byte[] exportExcelTraCuuToExcel(ExcelTraCuuResponse data, String fileName) throws BusinessException;
    
    /**
     * Export dữ liệu đối soát ra file Excel với nhiều sheet
     */
    byte[] exportDoiSoatToExcel(List<SDoiSoat> doiSoatList, List<SDoiSoatCt> doiSoatCtList, String fileName) throws BusinessException;
    
    /**
     * Export dữ liệu đối soát ra file Excel với layout master-detail trong 1 sheet
     */
    byte[] exportDoiSoatMasterDetailToExcel(List<SDoiSoat> doiSoatList, String fileName) throws BusinessException;
    
}
