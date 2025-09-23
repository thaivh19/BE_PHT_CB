package com.pht.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pht.entity.SDonHang;

@Repository
public interface SDonHangRepository extends BaseRepository<SDonHang, Long> {

    @Query("SELECT s FROM SDonHang s WHERE " +
           "(:mst IS NULL OR LOWER(s.mst) LIKE LOWER(:mst)) AND " +
           "(:tenDn IS NULL OR LOWER(s.tenDn) LIKE LOWER(:tenDn)) AND " +
           "(:soDonHang IS NULL OR LOWER(s.soDonHang) LIKE LOWER(:soDonHang)) AND " +
           "(:loaiThanhToan IS NULL OR LOWER(s.loaiThanhToan) LIKE LOWER(:loaiThanhToan)) AND " +
           "(:trangThai IS NULL OR LOWER(s.trangThai) LIKE LOWER(:trangThai)) AND " +
           "(:nganHang IS NULL OR LOWER(s.nganHang) LIKE LOWER(:nganHang))")
    Page<SDonHang> findBySearchCriteria(@Param("mst") String mst,
                                        @Param("tenDn") String tenDn,
                                        @Param("soDonHang") String soDonHang,
                                        @Param("loaiThanhToan") String loaiThanhToan,
                                        @Param("trangThai") String trangThai,
                                        @Param("nganHang") String nganHang,
                                        Pageable pageable);

    @Query("SELECT s FROM SDonHang s WHERE " +
           "(:mst IS NULL OR LOWER(s.mst) LIKE LOWER(:mst)) AND " +
           "(:tenDn IS NULL OR LOWER(s.tenDn) LIKE LOWER(:tenDn)) AND " +
           "(:soDonHang IS NULL OR LOWER(s.soDonHang) LIKE LOWER(:soDonHang)) AND " +
           "(:loaiThanhToan IS NULL OR LOWER(s.loaiThanhToan) LIKE LOWER(:loaiThanhToan)) AND " +
           "(:trangThai IS NULL OR LOWER(s.trangThai) LIKE LOWER(:trangThai)) AND " +
           "(:nganHang IS NULL OR LOWER(s.nganHang) LIKE LOWER(:nganHang))")
    List<SDonHang> findBySearchCriteria(@Param("mst") String mst,
                                        @Param("tenDn") String tenDn,
                                        @Param("soDonHang") String soDonHang,
                                        @Param("loaiThanhToan") String loaiThanhToan,
                                        @Param("trangThai") String trangThai,
                                        @Param("nganHang") String nganHang);

    boolean existsBySoDonHang(String soDonHang);
    
    SDonHang findBySoDonHang(String soDonHang);

    // Tìm số đơn hàng lớn nhất để generate số tiếp theo
    @Query("SELECT MAX(CAST(SUBSTRING(s.soDonHang, 4) AS long)) FROM SDonHang s WHERE s.soDonHang LIKE 'DH%'")
    Long findMaxSoDonHangNumber();

    @Query("SELECT DISTINCT s FROM SDonHang s, SDonHangCt c, StoKhai t WHERE c.donHangId = s.id AND t.id = c.idTokhai AND s.trangThai = :tt")
    java.util.List<SDonHang> findOrdersHavingTokhaiTrangThaiNganHang(@Param("tt") String tt);
}
