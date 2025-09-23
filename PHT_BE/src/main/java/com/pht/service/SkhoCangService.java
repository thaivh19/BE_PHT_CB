package com.pht.service;

import java.util.List;

import com.pht.entity.SkhoCang;
import com.pht.exception.BusinessException;
import com.pht.model.request.SkhoCangCreateRequest;
import com.pht.model.request.SkhoCangSearchRequest;
import com.pht.model.request.SkhoCangUpdateRequest;
import com.pht.model.response.CatalogSearchResponse;

public interface SkhoCangService extends BaseService<SkhoCang, Long> {
    
    List<SkhoCang> getAllKhoCang();
    
    CatalogSearchResponse<SkhoCang> getAllKhoCangWithPagination(int page, int size, String sortBy, String sortDir);
    
    SkhoCang getKhoCangById(Long id) throws BusinessException;
    
    SkhoCang createKhoCang(SkhoCangCreateRequest request) throws BusinessException;
    
    SkhoCang updateKhoCang(SkhoCangUpdateRequest request) throws BusinessException;
    
    void deleteKhoCang(Long id) throws BusinessException;
    
    // Search functionality
    CatalogSearchResponse<SkhoCang> searchKhoCang(SkhoCangSearchRequest request);
    
    // Export functionality
    List<SkhoCang> exportKhoCang(SkhoCangSearchRequest request);
}
