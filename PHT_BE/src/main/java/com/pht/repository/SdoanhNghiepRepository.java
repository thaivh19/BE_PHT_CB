package com.pht.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pht.entity.SdoanhNghiep;

@Repository
public interface SdoanhNghiepRepository extends BaseRepository<SdoanhNghiep, Long> {

    @Query("SELECT s FROM SdoanhNghiep s WHERE " +
           "(:maDn IS NULL OR LOWER(s.maDn) LIKE LOWER(:maDn)) AND " +
           "(:tenDn IS NULL OR LOWER(s.tenDn) LIKE LOWER(:tenDn)) AND " +
           "(:trangThai IS NULL OR s.trangThai = :trangThai)")
    Page<SdoanhNghiep> findBySearchCriteria(@Param("maDn") String maDn,
                                           @Param("tenDn") String tenDn,
                                           @Param("trangThai") String trangThai,
                                           Pageable pageable);

    @Query("SELECT s FROM SdoanhNghiep s WHERE " +
           "(:maDn IS NULL OR LOWER(s.maDn) LIKE LOWER(:maDn)) AND " +
           "(:tenDn IS NULL OR LOWER(s.tenDn) LIKE LOWER(:tenDn)) AND " +
           "(:trangThai IS NULL OR s.trangThai = :trangThai)")
    List<SdoanhNghiep> findBySearchCriteria(@Param("maDn") String maDn,
                                           @Param("tenDn") String tenDn,
                                           @Param("trangThai") String trangThai);

    boolean existsByMaDn(String maDn);
}