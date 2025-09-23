package com.pht.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pht.entity.SlogNhKb;

@Repository
public interface SlogNhKbRepository extends BaseRepository<SlogNhKb, Long> {
    
    /**
     * Tìm log theo ngày đối soát và loại
     */
    @Query("SELECT s FROM SlogNhKb s WHERE s.ngayDs = :ngayDs AND s.loai = :loai")
    List<SlogNhKb> findByNgayDsAndLoai(@Param("ngayDs") LocalDateTime ngayDs, @Param("loai") String loai);
    
    /**
     * Tìm log theo ngân hàng và loại
     */
    @Query("SELECT s FROM SlogNhKb s WHERE s.nganHang = :nganHang AND s.loai = :loai")
    List<SlogNhKb> findByNganHangAndLoai(@Param("nganHang") String nganHang, @Param("loai") String loai);
    
    /**
     * Tìm log theo khoảng thời gian
     */
    @Query("SELECT s FROM SlogNhKb s WHERE s.ngayDs >= :tuNgay AND s.ngayDs <= :denNgay ORDER BY s.ngayDs DESC")
    List<SlogNhKb> findByNgayDsBetween(@Param("tuNgay") LocalDateTime tuNgay, @Param("denNgay") LocalDateTime denNgay);
}

