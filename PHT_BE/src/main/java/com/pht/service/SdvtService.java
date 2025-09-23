package com.pht.service;

import java.util.List;

import com.pht.entity.Sdvt;
import com.pht.exception.BusinessException;
import com.pht.model.request.SdvtCreateRequest;
import com.pht.model.request.SdvtSearchRequest;
import com.pht.model.request.SdvtUpdateRequest;
import com.pht.model.response.CatalogSearchResponse;

public interface SdvtService extends BaseService<Sdvt, Long> {
    
    List<Sdvt> getAllDvt();
    
    CatalogSearchResponse<Sdvt> getAllDvtWithPagination(int page, int size, String sortBy, String sortDir);
    
    Sdvt getDvtById(Long id) throws BusinessException;
    
    Sdvt createDvt(SdvtCreateRequest request) throws BusinessException;
    
    Sdvt updateDvt(SdvtUpdateRequest request) throws BusinessException;
    
    void deleteDvt(Long id) throws BusinessException;
    
    // Search functionality
    CatalogSearchResponse<Sdvt> searchDvt(SdvtSearchRequest request);
    
    // Export functionality
    List<Sdvt> exportDvt(SdvtSearchRequest request);
}
