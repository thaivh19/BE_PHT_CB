package com.pht.service;

import java.util.List;

import com.pht.entity.SmauPhiBienLai;
import com.pht.exception.BusinessException;
import com.pht.model.request.SmauPhiBienLaiCreateRequest;
import com.pht.model.request.SmauPhiBienLaiSearchRequest;
import com.pht.model.request.SmauPhiBienLaiUpdateRequest;
import com.pht.model.response.CatalogSearchResponse;

public interface SmauPhiBienLaiService extends BaseService<SmauPhiBienLai, Long> {
    
    List<SmauPhiBienLai> getAllMauPhiBienLai();
    
    CatalogSearchResponse<SmauPhiBienLai> getAllMauPhiBienLaiWithPagination(int page, int size, String sortBy, String sortDir);
    
    SmauPhiBienLai getMauPhiBienLaiById(Long id) throws BusinessException;
    
    SmauPhiBienLai createMauPhiBienLai(SmauPhiBienLaiCreateRequest request) throws BusinessException;
    
    SmauPhiBienLai updateMauPhiBienLai(SmauPhiBienLaiUpdateRequest request) throws BusinessException;
    
    void deleteMauPhiBienLai(Long id) throws BusinessException;
    
    // Search functionality
    CatalogSearchResponse<SmauPhiBienLai> searchMauPhiBienLai(SmauPhiBienLaiSearchRequest request);
    
    // Export functionality
    List<SmauPhiBienLai> exportMauPhiBienLai(SmauPhiBienLaiSearchRequest request);
}
