package com.pht.service;

import java.util.List;

import com.pht.entity.SdiaDiemLuuKho;
import com.pht.exception.BusinessException;
import com.pht.model.request.SdiaDiemLuuKhoCreateRequest;
import com.pht.model.request.SdiaDiemLuuKhoSearchRequest;
import com.pht.model.request.SdiaDiemLuuKhoUpdateRequest;
import com.pht.model.response.CatalogSearchResponse;

public interface SdiaDiemLuuKhoService extends BaseService<SdiaDiemLuuKho, Long> {
    
    List<SdiaDiemLuuKho> getAllDiaDiemLuuKho();
    
    CatalogSearchResponse<SdiaDiemLuuKho> getAllDiaDiemLuuKhoWithPagination(int page, int size, String sortBy, String sortDir);
    
    SdiaDiemLuuKho getDiaDiemLuuKhoById(Long id) throws BusinessException;
    
    SdiaDiemLuuKho createDiaDiemLuuKho(SdiaDiemLuuKhoCreateRequest request) throws BusinessException;
    
    SdiaDiemLuuKho updateDiaDiemLuuKho(SdiaDiemLuuKhoUpdateRequest request) throws BusinessException;
    
    void deleteDiaDiemLuuKho(Long id) throws BusinessException;
    
    // Search functionality
    CatalogSearchResponse<SdiaDiemLuuKho> searchDiaDiemLuuKho(SdiaDiemLuuKhoSearchRequest request);
    
    // Export functionality
    List<SdiaDiemLuuKho> exportDiaDiemLuuKho(SdiaDiemLuuKhoSearchRequest request);
}
