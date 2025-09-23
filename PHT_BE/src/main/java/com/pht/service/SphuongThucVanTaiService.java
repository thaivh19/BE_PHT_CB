package com.pht.service;

import java.util.List;

import com.pht.entity.SphuongThucVanTai;
import com.pht.exception.BusinessException;
import com.pht.model.request.SphuongThucVanTaiCreateRequest;
import com.pht.model.request.SphuongThucVanTaiSearchRequest;
import com.pht.model.request.SphuongThucVanTaiUpdateRequest;
import com.pht.model.response.CatalogSearchResponse;

public interface SphuongThucVanTaiService extends BaseService<SphuongThucVanTai, Long> {
    
    List<SphuongThucVanTai> getAllPhuongThucVanTai();
    
    CatalogSearchResponse<SphuongThucVanTai> getAllPhuongThucVanTaiWithPagination(int page, int size, String sortBy, String sortDir);
    
    SphuongThucVanTai getPhuongThucVanTaiById(Long id) throws BusinessException;
    
    SphuongThucVanTai createPhuongThucVanTai(SphuongThucVanTaiCreateRequest request) throws BusinessException;
    
    SphuongThucVanTai updatePhuongThucVanTai(SphuongThucVanTaiUpdateRequest request) throws BusinessException;
    
    void deletePhuongThucVanTai(Long id) throws BusinessException;
    
    // Search functionality
    CatalogSearchResponse<SphuongThucVanTai> searchPhuongThucVanTai(SphuongThucVanTaiSearchRequest request);
    
    // Export functionality
    List<SphuongThucVanTai> exportPhuongThucVanTai(SphuongThucVanTaiSearchRequest request);
}
