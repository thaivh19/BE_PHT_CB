package com.pht.service;

import java.util.List;

import com.pht.entity.SloaiHinh;
import com.pht.exception.BusinessException;
import com.pht.model.request.SloaiHinhCreateRequest;
import com.pht.model.request.SloaiHinhSearchRequest;
import com.pht.model.request.SloaiHinhUpdateRequest;
import com.pht.model.response.CatalogSearchResponse;

public interface SloaiHinhService extends BaseService<SloaiHinh, Long> {
    
    List<SloaiHinh> getAllLoaiHinh();
    
    CatalogSearchResponse<SloaiHinh> getAllLoaiHinhWithPagination(int page, int size, String sortBy, String sortDir);
    
    SloaiHinh getLoaiHinhById(Long id) throws BusinessException;
    
    SloaiHinh createLoaiHinh(SloaiHinhCreateRequest request) throws BusinessException;
    
    SloaiHinh updateLoaiHinh(SloaiHinhUpdateRequest request) throws BusinessException;
    
    void deleteLoaiHinh(Long id) throws BusinessException;
    
    // Search functionality
    CatalogSearchResponse<SloaiHinh> searchLoaiHinh(SloaiHinhSearchRequest request);
    
    // Export functionality
    List<SloaiHinh> exportLoaiHinh(SloaiHinhSearchRequest request);
}
