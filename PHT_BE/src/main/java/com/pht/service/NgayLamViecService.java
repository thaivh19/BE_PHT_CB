package com.pht.service;

import java.time.LocalDate;
import java.util.List;

import com.pht.entity.NgayLv;

public interface NgayLamViecService {
    
    /**
     * Kiểm tra ngày hiện tại có phải là ngày làm việc không
     */
    boolean isNgayLamViec(LocalDate ngay);
    
    /**
     * Kiểm tra ngày cụ thể có phải là ngày làm việc không
     */
    boolean isNgayLamViec(String ngayLv);
    
    /**
     * Kiểm tra ngày có phải là ngày làm việc không (alias cho isNgayLamViec)
     */
    boolean laNgayLamViec(LocalDate ngay);
    
    /**
     * Tìm ngày làm việc gần nhất trước ngày hiện tại
     */
    LocalDate timNgayLamViecGanNhat();
    
    /**
     * Tìm ngày làm việc gần nhất trước ngày cụ thể
     */
    LocalDate timNgayLamViecGanNhat(LocalDate ngayHienTai);
    
    /**
     * Lấy danh sách tất cả ngày làm việc
     */
    List<NgayLv> getAllNgayLamViec();
    
    /**
     * Lấy danh sách ngày làm việc theo trạng thái
     */
    List<NgayLv> getNgayLamViecByTrangThai(String trangThai);
    
    /**
     * Lấy thông tin COT từ ngày làm việc
     */
    String layCotTuNgayLamViec(LocalDate ngay);
}
