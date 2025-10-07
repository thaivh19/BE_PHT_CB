package com.pht.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pht.entity.SloaiHinh;

@Repository
public interface SloaiHinhRepository extends BaseRepository<SloaiHinh, Long> {

    @Query("SELECT s FROM SloaiHinh s WHERE " +
           "(:nhomLoaiHinh IS NULL OR LOWER(s.nhomLoaiHinh) LIKE LOWER(:nhomLoaiHinh)) AND " +
           "(:maLoaiHinh IS NULL OR LOWER(s.maLoaiHinh) LIKE LOWER(:maLoaiHinh)) AND " +
           "(:tenLoaiHinh IS NULL OR LOWER(s.tenLoaiHinh) LIKE LOWER(:tenLoaiHinh)) AND " +
           "(:trangThai IS NULL OR s.trangThai = :trangThai)")
    Page<SloaiHinh> findBySearchCriteria(@Param("nhomLoaiHinh") String nhomLoaiHinh,
                                       @Param("maLoaiHinh") String maLoaiHinh,
                                       @Param("tenLoaiHinh") String tenLoaiHinh,
                                       @Param("trangThai") String trangThai,
                                       Pageable pageable);

    @Query("SELECT s FROM SloaiHinh s WHERE " +
           "(:nhomLoaiHinh IS NULL OR LOWER(s.nhomLoaiHinh) LIKE LOWER(:nhomLoaiHinh)) AND " +
           "(:maLoaiHinh IS NULL OR LOWER(s.maLoaiHinh) LIKE LOWER(:maLoaiHinh)) AND " +
           "(:tenLoaiHinh IS NULL OR LOWER(s.tenLoaiHinh) LIKE LOWER(:tenLoaiHinh)) AND " +
           "(:trangThai IS NULL OR s.trangThai = :trangThai)")
    List<SloaiHinh> findBySearchCriteria(@Param("nhomLoaiHinh") String nhomLoaiHinh,
                                       @Param("maLoaiHinh") String maLoaiHinh,
                                       @Param("tenLoaiHinh") String tenLoaiHinh,
                                       @Param("trangThai") String trangThai);

    boolean existsByMaLoaiHinh(String maLoaiHinh);

    SloaiHinh findFirstByMaLoaiHinh(String maLoaiHinh);
}