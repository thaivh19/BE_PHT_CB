package com.pht.service;

import java.util.List;

import com.pht.entity.SloaiThanhToan;
import com.pht.exception.BusinessException;
import com.pht.model.request.SloaiThanhToanCreateRequest;
import com.pht.model.request.SloaiThanhToanSearchRequest;
import com.pht.model.request.SloaiThanhToanUpdateRequest;
import com.pht.model.response.CatalogSearchResponse;

public interface SloaiThanhToanService extends BaseService<SloaiThanhToan, Long> {
    
    List<SloaiThanhToan> getAllLoaiThanhToan();
    
    CatalogSearchResponse<SloaiThanhToan> getAllLoaiThanhToanWithPagination(int page, int size, String sortBy, String sortDir);
    
    SloaiThanhToan getLoaiThanhToanById(Long id) throws BusinessException;
    
    SloaiThanhToan createLoaiThanhToan(SloaiThanhToanCreateRequest request) throws BusinessException;
    
    SloaiThanhToan updateLoaiThanhToan(SloaiThanhToanUpdateRequest request) throws BusinessException;
    
    void deleteLoaiThanhToan(Long id) throws BusinessException;
    
    // Search functionality
    CatalogSearchResponse<SloaiThanhToan> searchLoaiThanhToan(SloaiThanhToanSearchRequest request);
    
    // Export functionality
    List<SloaiThanhToan> exportLoaiThanhToan(SloaiThanhToanSearchRequest request);
}
