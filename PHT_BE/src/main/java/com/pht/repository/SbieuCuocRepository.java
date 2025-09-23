package com.pht.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pht.entity.SbieuCuoc;

@Repository
public interface SbieuCuocRepository extends BaseRepository<SbieuCuoc, Long> {

    @Query("SELECT s FROM SbieuCuoc s WHERE " +
           "(:maBieuCuoc IS NULL OR LOWER(s.maBieuCuoc) LIKE LOWER(:maBieuCuoc)) AND " +
           "(:tenBieuCuoc IS NULL OR LOWER(s.tenBieuCuoc) LIKE LOWER(:tenBieuCuoc)) AND " +
           "(:nhomLoaiHinh IS NULL OR LOWER(s.nhomLoaiHinh) LIKE LOWER(:nhomLoaiHinh)) AND " +
           "(:loaiCont IS NULL OR LOWER(s.loaiCont) LIKE LOWER(:loaiCont)) AND " +
           "(:loaiBc IS NULL OR LOWER(s.loaiBc) LIKE LOWER(:loaiBc)) AND " +
           "(:trangThai IS NULL OR s.trangThai = :trangThai)")
    Page<SbieuCuoc> findBySearchCriteria(@Param("maBieuCuoc") String maBieuCuoc,
                                        @Param("tenBieuCuoc") String tenBieuCuoc,
                                        @Param("nhomLoaiHinh") String nhomLoaiHinh,
                                        @Param("loaiCont") String loaiCont,
                                        @Param("loaiBc") String loaiBc,
                                        @Param("trangThai") String trangThai,
                                        Pageable pageable);

    @Query("SELECT s FROM SbieuCuoc s WHERE " +
           "(:maBieuCuoc IS NULL OR LOWER(s.maBieuCuoc) LIKE LOWER(:maBieuCuoc)) AND " +
           "(:tenBieuCuoc IS NULL OR LOWER(s.tenBieuCuoc) LIKE LOWER(:tenBieuCuoc)) AND " +
           "(:nhomLoaiHinh IS NULL OR LOWER(s.nhomLoaiHinh) LIKE LOWER(:nhomLoaiHinh)) AND " +
           "(:loaiCont IS NULL OR LOWER(s.loaiCont) LIKE LOWER(:loaiCont)) AND " +
           "(:loaiBc IS NULL OR LOWER(s.loaiBc) LIKE LOWER(:loaiBc)) AND " +
           "(:trangThai IS NULL OR s.trangThai = :trangThai)")
    List<SbieuCuoc> findBySearchCriteria(@Param("maBieuCuoc") String maBieuCuoc,
                                        @Param("tenBieuCuoc") String tenBieuCuoc,
                                        @Param("nhomLoaiHinh") String nhomLoaiHinh,
                                        @Param("loaiCont") String loaiCont,
                                        @Param("loaiBc") String loaiBc,
                                        @Param("trangThai") String trangThai);

    boolean existsByMaBieuCuoc(String maBieuCuoc);
    
    /**
     * Tìm đơn giá theo ma_loai_cont và ma_tc_cont (direct mapping)
     */
    @Query("SELECT s.donGia FROM SbieuCuoc s WHERE " +
           "s.maLoaiCont = :maLoaiCont AND " +
           "s.maTcCont = :maTcCont AND " +
           "s.trangThai = '1' AND " +
           "s.loaiBc = 'LBC001'")
    List<java.math.BigDecimal> findDonGiaByLoaiContAndTcCont(@Param("maLoaiCont") String maLoaiCont, 
                                                           @Param("maTcCont") String maTcCont);
    
    /**
     * Debug: Lấy tất cả biểu cước LBC001 để kiểm tra dữ liệu
     */
    @Query("SELECT s FROM SbieuCuoc s WHERE s.trangThai = '1' AND s.loaiBc = 'LBC001'")
    List<com.pht.entity.SbieuCuoc> findAllActive();
    
    /**
     * Tìm biểu cước theo mã biểu cước chính xác
     */
    @Query("SELECT s FROM SbieuCuoc s WHERE s.maBieuCuoc = :maBieuCuoc AND s.trangThai = '1'")
    List<com.pht.entity.SbieuCuoc> findByMaBieuCuoc(@Param("maBieuCuoc") String maBieuCuoc);
    
    /**
     * Tìm biểu cước theo đơn vị tính và loại biểu cước
     */
    @Query("SELECT s FROM SbieuCuoc s WHERE s.dvt = :dvt AND s.loaiBc = :loaiBc")
    List<com.pht.entity.SbieuCuoc> findByDvtAndLoaiBc(@Param("dvt") String dvt, 
                                                     @Param("loaiBc") String loaiBc);
}