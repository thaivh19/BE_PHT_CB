package com.pht.service;

import java.time.LocalDate;
import java.util.List;

import com.pht.dto.DoiSoatExportRequest;
import com.pht.dto.DoiSoatSearchRequest;
import com.pht.entity.SDoiSoat;
import com.pht.entity.SDoiSoatCt;
import com.pht.exception.BusinessException;

public interface DoiSoatService {
    
    /**
     * Chạy đối soát tự động từ ngày làm việc gần nhất đến ngày hiện tại
     */
    SDoiSoat chayDoiSoatTuDongTheoNgayLamViec(LocalDate ngayLamViecGanNhat, LocalDate ngayHienTai);
    
    /**
     * Chạy đối soát thủ công với logic tăng lần đối soát
     */
    SDoiSoat chayDoiSoatThuCong(LocalDate ngayLamViecGanNhat, LocalDate ngayDoiSoat);
    
    /**
     * Lấy tất cả dữ liệu đối soát
     */
    List<SDoiSoat> getAll();
    
    /**
     * Lấy đối soát theo ID
     */
    SDoiSoat getById(Long id) throws BusinessException;
    
    /**
     * Export dữ liệu đối soát theo ngày
     */
    List<SDoiSoatCt> exportDoiSoatByDate(DoiSoatExportRequest request) throws BusinessException;
    
    /**
     * Lấy danh sách SDoiSoat theo ngày với LAN_DS lớn nhất
     */
    List<SDoiSoat> getDoiSoatByDate(LocalDate ngayDs) throws BusinessException;
    
    /**
     * Tìm kiếm đối soát theo điều kiện từ ngày đến ngày
     */
    List<SDoiSoat> searchDoiSoat(DoiSoatSearchRequest request) throws BusinessException;
}
