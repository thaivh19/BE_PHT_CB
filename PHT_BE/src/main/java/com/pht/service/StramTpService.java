package com.pht.service;

import java.util.List;

import com.pht.entity.StramTp;
import com.pht.exception.BusinessException;
import com.pht.model.request.StramTpCreateRequest;
import com.pht.model.request.StramTpSearchRequest;
import com.pht.model.request.StramTpUpdateRequest;
import com.pht.model.response.CatalogSearchResponse;

public interface StramTpService extends BaseService<StramTp, Long> {
    
    List<StramTp> getAllTramTp();
    
    CatalogSearchResponse<StramTp> getAllTramTpWithPagination(int page, int size, String sortBy, String sortDir);
    
    StramTp getTramTpById(Long id) throws BusinessException;
    
    StramTp createTramTp(StramTpCreateRequest request) throws BusinessException;
    
    StramTp updateTramTp(StramTpUpdateRequest request) throws BusinessException;
    
    void deleteTramTp(Long id) throws BusinessException;
    
    // Search functionality
    CatalogSearchResponse<StramTp> searchTramTp(StramTpSearchRequest request);
    
    // Export functionality
    List<StramTp> exportTramTp(StramTpSearchRequest request);
}
