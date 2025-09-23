package com.pht.service;

import java.util.List;

import com.pht.entity.SDonHang;
import com.pht.exception.BusinessException;
import com.pht.model.request.SDonHangCreateRequest;
import com.pht.model.request.SDonHangSearchRequest;
import com.pht.model.request.SDonHangUpdateRequest;
import com.pht.model.response.CatalogSearchResponse;

public interface SDonHangService extends BaseService<SDonHang, Long> {
    
    List<SDonHang> getAllDonHang();
    
    CatalogSearchResponse<SDonHang> getAllDonHangWithPagination(int page, int size, String sortBy, String sortDir);
    
    SDonHang getDonHangById(Long id) throws BusinessException;
    
    SDonHang createDonHang(SDonHangCreateRequest request) throws BusinessException;
    
    SDonHang updateDonHang(SDonHangUpdateRequest request) throws BusinessException;
    
    void deleteDonHang(Long id) throws BusinessException;
    
    // Search functionality
    CatalogSearchResponse<SDonHang> searchDonHang(SDonHangSearchRequest request);
    
    // Export functionality
    List<SDonHang> exportDonHang(SDonHangSearchRequest request);
    
    // Find by soDonHang
    SDonHang findBySoDonHang(String soDonHang);

    // Generate unique soDonHang
    String generateSoDonHang();
}
