package com.pht.service;

import java.util.List;

import com.pht.entity.SloaiCont;
import com.pht.exception.BusinessException;
import com.pht.model.request.SloaiContCreateRequest;
import com.pht.model.request.SloaiContSearchRequest;
import com.pht.model.request.SloaiContUpdateRequest;
import com.pht.model.response.CatalogSearchResponse;

public interface SloaiContService extends BaseService<SloaiCont, Long> {
    
    List<SloaiCont> getAllLoaiCont();
    
    CatalogSearchResponse<SloaiCont> getAllLoaiContWithPagination(int page, int size, String sortBy, String sortDir);
    
    SloaiCont getLoaiContById(Long id) throws BusinessException;
    
    SloaiCont createLoaiCont(SloaiContCreateRequest request) throws BusinessException;
    
    SloaiCont updateLoaiCont(SloaiContUpdateRequest request) throws BusinessException;
    
    void deleteLoaiCont(Long id) throws BusinessException;
    
    // Search functionality
    CatalogSearchResponse<SloaiCont> searchLoaiCont(SloaiContSearchRequest request);
    
    // Export functionality
    List<SloaiCont> exportLoaiCont(SloaiContSearchRequest request);
}
