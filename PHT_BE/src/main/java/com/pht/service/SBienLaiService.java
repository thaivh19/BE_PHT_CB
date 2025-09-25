package com.pht.service;

import java.util.List;
import java.time.LocalDate;

import com.pht.entity.SBienLai;
import com.pht.exception.BusinessException;
import com.pht.model.request.SBienLaiCreateRequest;
import com.pht.model.request.SBienLaiSearchRequest;
import com.pht.model.request.SBienLaiUpdateRequest;
import com.pht.model.response.CatalogSearchResponse;
import com.pht.model.response.BlThuReportItem;
import com.pht.model.response.KhoBlReportItem;

public interface SBienLaiService extends BaseService<SBienLai, Long> {
    
    List<SBienLai> getAllBienLai();
    
    CatalogSearchResponse<SBienLai> getAllBienLaiWithPagination(int page, int size, String sortBy, String sortDir);
    
    SBienLai getBienLaiById(Long id) throws BusinessException;
    
    SBienLai createBienLai(SBienLaiCreateRequest request) throws BusinessException;
    
    SBienLai updateBienLai(SBienLaiUpdateRequest request) throws BusinessException;
    
    void deleteBienLai(Long id) throws BusinessException;
    
    // Search functionality
    CatalogSearchResponse<SBienLai> searchBienLai(SBienLaiSearchRequest request);
    
    // Export functionality
    List<SBienLai> exportBienLai(SBienLaiSearchRequest request);
    
    // Find by maBl
    SBienLai findByMaBl(String maBl);

    // Báo cáo BL thu theo khoảng ngày, nhóm MST + ngày
    List<BlThuReportItem> reportBlThu(LocalDate fromDate, LocalDate toDate);

    // Báo cáo tổng hợp biên lai theo mã kho trong khoảng ngày
    List<KhoBlReportItem> reportByKho(LocalDate fromDate, LocalDate toDate);
}
