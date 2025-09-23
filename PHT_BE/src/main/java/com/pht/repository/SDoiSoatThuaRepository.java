package com.pht.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pht.entity.SDoiSoatThua;

@Repository
public interface SDoiSoatThuaRepository extends BaseRepository<SDoiSoatThua, Long> {
    
    /**
     * Tìm dữ liệu thừa theo ngày đối soát và ngân hàng
     */
    @Query("SELECT t FROM SDoiSoatThua t WHERE t.ngayDs = :ngayDs AND t.nganHang = :nganHang ORDER BY t.id")
    List<SDoiSoatThua> findByNgayDsAndNganHang(@Param("ngayDs") LocalDate ngayDs, @Param("nganHang") String nganHang);
    
    /**
     * Tìm dữ liệu thừa theo DOI_SOAT_ID
     */
    @Query("SELECT t FROM SDoiSoatThua t WHERE t.doiSoatId = :doiSoatId ORDER BY t.id")
    List<SDoiSoatThua> findByDoiSoatId(@Param("doiSoatId") Long doiSoatId);
    
    /**
     * Đếm số tờ khai thừa theo ngày đối soát và ngân hàng
     */
    @Query("SELECT COUNT(t) FROM SDoiSoatThua t WHERE t.ngayDs = :ngayDs AND t.nganHang = :nganHang")
    Long countByNgayDsAndNganHang(@Param("ngayDs") LocalDate ngayDs, @Param("nganHang") String nganHang);
    
    /**
     * Đếm số tờ khai thừa theo DOI_SOAT_ID và ngân hàng
     */
    @Query("SELECT COUNT(t) FROM SDoiSoatThua t WHERE t.doiSoatId = :doiSoatId AND t.nganHang = :nganHang")
    Long countByDoiSoatIdAndNganHang(@Param("doiSoatId") Long doiSoatId, @Param("nganHang") String nganHang);
}
