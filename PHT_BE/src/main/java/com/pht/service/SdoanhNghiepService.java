package com.pht.service;

import java.util.List;

import com.pht.entity.SdoanhNghiep;
import com.pht.exception.BusinessException;
import com.pht.model.request.SdoanhNghiepCreateRequest;
import com.pht.model.request.SdoanhNghiepSearchRequest;
import com.pht.model.request.SdoanhNghiepUpdateRequest;
import com.pht.model.response.CatalogSearchResponse;

public interface SdoanhNghiepService extends BaseService<SdoanhNghiep, Long> {
    
    List<SdoanhNghiep> getAllDoanhNghiep();
    
    CatalogSearchResponse<SdoanhNghiep> getAllDoanhNghiepWithPagination(int page, int size, String sortBy, String sortDir);
    
    SdoanhNghiep getDoanhNghiepById(Long id) throws BusinessException;
    
    SdoanhNghiep createDoanhNghiep(SdoanhNghiepCreateRequest request) throws BusinessException;
    
    SdoanhNghiep updateDoanhNghiep(SdoanhNghiepUpdateRequest request) throws BusinessException;
    
    void deleteDoanhNghiep(Long id) throws BusinessException;
    
    // Search functionality
    CatalogSearchResponse<SdoanhNghiep> searchDoanhNghiep(SdoanhNghiepSearchRequest request);
    
    // Export functionality
    List<SdoanhNghiep> exportDoanhNghiep(SdoanhNghiepSearchRequest request);
}
