package com.pht.service;

import java.util.List;

import com.pht.entity.SnganHang;
import com.pht.exception.BusinessException;
import com.pht.model.request.SnganHangCreateRequest;
import com.pht.model.request.SnganHangSearchRequest;
import com.pht.model.request.SnganHangUpdateRequest;
import com.pht.model.response.CatalogSearchResponse;

public interface SnganHangService extends BaseService<SnganHang, Long> {
    
    List<SnganHang> getAllNganHang();
    
    CatalogSearchResponse<SnganHang> getAllNganHangWithPagination(int page, int size, String sortBy, String sortDir);
    
    SnganHang getNganHangById(Long id) throws BusinessException;
    
    SnganHang createNganHang(SnganHangCreateRequest request) throws BusinessException;
    
    SnganHang updateNganHang(SnganHangUpdateRequest request) throws BusinessException;
    
    void deleteNganHang(Long id) throws BusinessException;
    
    // Search functionality
    CatalogSearchResponse<SnganHang> searchNganHang(SnganHangSearchRequest request);
    
    // Export functionality
    List<SnganHang> exportNganHang(SnganHangSearchRequest request);
}
