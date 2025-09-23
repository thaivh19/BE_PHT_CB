package com.pht.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pht.entity.SdiaDiemLuuKho;

@Repository
public interface SdiaDiemLuuKhoRepository extends BaseRepository<SdiaDiemLuuKho, Long> {

    @Query("SELECT s FROM SdiaDiemLuuKho s WHERE " +
           "(:maDiaDiemLuuKho IS NULL OR LOWER(s.maDiaDiemLuuKho) LIKE LOWER(:maDiaDiemLuuKho)) AND " +
           "(:tenDiaDiem IS NULL OR LOWER(s.tenDiaDiem) LIKE LOWER(:tenDiaDiem)) AND " +
           "(:trangThai IS NULL OR s.trangThai = :trangThai)")
    Page<SdiaDiemLuuKho> findBySearchCriteria(@Param("maDiaDiemLuuKho") String maDiaDiemLuuKho,
                                             @Param("tenDiaDiem") String tenDiaDiem,
                                             @Param("trangThai") String trangThai,
                                             Pageable pageable);

    @Query("SELECT s FROM SdiaDiemLuuKho s WHERE " +
           "(:maDiaDiemLuuKho IS NULL OR LOWER(s.maDiaDiemLuuKho) LIKE LOWER(:maDiaDiemLuuKho)) AND " +
           "(:tenDiaDiem IS NULL OR LOWER(s.tenDiaDiem) LIKE LOWER(:tenDiaDiem)) AND " +
           "(:trangThai IS NULL OR s.trangThai = :trangThai)")
    List<SdiaDiemLuuKho> findBySearchCriteria(@Param("maDiaDiemLuuKho") String maDiaDiemLuuKho,
                                             @Param("tenDiaDiem") String tenDiaDiem,
                                             @Param("trangThai") String trangThai);

    boolean existsByMaDiaDiemLuuKho(String maDiaDiemLuuKho);
}