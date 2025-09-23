package com.pht.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pht.entity.SloaiThanhToan;

@Repository
public interface SloaiThanhToanRepository extends BaseRepository<SloaiThanhToan, Long> {

    @Query("SELECT s FROM SloaiThanhToan s WHERE " +
           "(:maLoaiThanhToan IS NULL OR LOWER(s.maLoaiThanhToan) LIKE LOWER(:maLoaiThanhToan)) AND " +
           "(:tenLoaiThanhToan IS NULL OR LOWER(s.tenLoaiThanhToan) LIKE LOWER(:tenLoaiThanhToan)) AND " +
           "(:trangThai IS NULL OR s.trangThai = :trangThai)")
    Page<SloaiThanhToan> findBySearchCriteria(@Param("maLoaiThanhToan") String maLoaiThanhToan,
                                            @Param("tenLoaiThanhToan") String tenLoaiThanhToan,
                                            @Param("trangThai") String trangThai,
                                            Pageable pageable);

    @Query("SELECT s FROM SloaiThanhToan s WHERE " +
           "(:maLoaiThanhToan IS NULL OR LOWER(s.maLoaiThanhToan) LIKE LOWER(:maLoaiThanhToan)) AND " +
           "(:tenLoaiThanhToan IS NULL OR LOWER(s.tenLoaiThanhToan) LIKE LOWER(:tenLoaiThanhToan)) AND " +
           "(:trangThai IS NULL OR s.trangThai = :trangThai)")
    List<SloaiThanhToan> findBySearchCriteria(@Param("maLoaiThanhToan") String maLoaiThanhToan,
                                            @Param("tenLoaiThanhToan") String tenLoaiThanhToan,
                                            @Param("trangThai") String trangThai);

    boolean existsByMaLoaiThanhToan(String maLoaiThanhToan);
}