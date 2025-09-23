package com.pht.service;

import java.util.List;

import com.pht.entity.SbieuCuoc;
import com.pht.exception.BusinessException;
import com.pht.model.request.SbieuCuocCreateRequest;
import com.pht.model.request.SbieuCuocSearchRequest;
import com.pht.model.request.SbieuCuocUpdateRequest;
import com.pht.model.response.CatalogSearchResponse;

public interface SbieuCuocService extends BaseService<SbieuCuoc, Long> {
    
    List<SbieuCuoc> getAllBieuCuoc();
    
    CatalogSearchResponse<SbieuCuoc> getAllBieuCuocWithPagination(int page, int size, String sortBy, String sortDir);
    
    SbieuCuoc getBieuCuocById(Long id) throws BusinessException;
    
    SbieuCuoc createBieuCuoc(SbieuCuocCreateRequest request) throws BusinessException;
    
    SbieuCuoc updateBieuCuoc(SbieuCuocUpdateRequest request) throws BusinessException;
    
    void deleteBieuCuoc(Long id) throws BusinessException;
    
    // Search functionality
    CatalogSearchResponse<SbieuCuoc> searchBieuCuoc(SbieuCuocSearchRequest request);
    
    // Export functionality
    List<SbieuCuoc> exportBieuCuoc(SbieuCuocSearchRequest request);
}
