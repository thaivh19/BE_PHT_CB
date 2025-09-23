package com.pht.service;

import java.util.List;

import com.pht.entity.ShaiQuan;
import com.pht.exception.BusinessException;
import com.pht.model.request.ShaiQuanCreateRequest;
import com.pht.model.request.ShaiQuanSearchRequest;
import com.pht.model.request.ShaiQuanUpdateRequest;
import com.pht.model.response.CatalogSearchResponse;

public interface ShaiQuanService extends BaseService<ShaiQuan, Long> {
    
    List<ShaiQuan> getAllHaiQuan();
    
    CatalogSearchResponse<ShaiQuan> getAllHaiQuanWithPagination(int page, int size, String sortBy, String sortDir);
    
    ShaiQuan getHaiQuanById(Long id) throws BusinessException;
    
    ShaiQuan createHaiQuan(ShaiQuanCreateRequest request) throws BusinessException;
    
    ShaiQuan updateHaiQuan(ShaiQuanUpdateRequest request) throws BusinessException;
    
    void deleteHaiQuan(Long id) throws BusinessException;
    
    // Search functionality
    CatalogSearchResponse<ShaiQuan> searchHaiQuan(ShaiQuanSearchRequest request);
    
    // Export functionality
    List<ShaiQuan> exportHaiQuan(ShaiQuanSearchRequest request);
}
