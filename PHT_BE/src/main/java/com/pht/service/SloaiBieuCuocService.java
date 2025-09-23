package com.pht.service;

import java.util.List;

import com.pht.entity.SloaiBieuCuoc;
import com.pht.exception.BusinessException;
import com.pht.model.request.SloaiBieuCuocCreateRequest;
import com.pht.model.request.SloaiBieuCuocSearchRequest;
import com.pht.model.request.SloaiBieuCuocUpdateRequest;
import com.pht.model.response.CatalogSearchResponse;

public interface SloaiBieuCuocService extends BaseService<SloaiBieuCuoc, Long> {
    
    List<SloaiBieuCuoc> getAllLoaiBieuCuoc();
    
    CatalogSearchResponse<SloaiBieuCuoc> getAllLoaiBieuCuocWithPagination(int page, int size, String sortBy, String sortDir);
    
    SloaiBieuCuoc getLoaiBieuCuocById(Long id) throws BusinessException;
    
    SloaiBieuCuoc createLoaiBieuCuoc(SloaiBieuCuocCreateRequest request) throws BusinessException;
    
    SloaiBieuCuoc updateLoaiBieuCuoc(SloaiBieuCuocUpdateRequest request) throws BusinessException;
    
    void deleteLoaiBieuCuoc(Long id) throws BusinessException;
    
    // Search functionality
    CatalogSearchResponse<SloaiBieuCuoc> searchLoaiBieuCuoc(SloaiBieuCuocSearchRequest request);
    
    // Export functionality
    List<SloaiBieuCuoc> exportLoaiBieuCuoc(SloaiBieuCuocSearchRequest request);
}
