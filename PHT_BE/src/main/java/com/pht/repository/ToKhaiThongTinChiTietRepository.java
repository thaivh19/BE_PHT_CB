package com.pht.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pht.entity.StoKhaiCt;

public interface ToKhaiThongTinChiTietRepository extends BaseRepository<StoKhaiCt, Long> {
    
    @Query("SELECT c FROM StoKhaiCt c WHERE c.toKhaiThongTinID = :toKhaiThongTinID")
    List<StoKhaiCt> findByToKhaiThongTinID(@Param("toKhaiThongTinID") Long toKhaiThongTinID);
    
    /**
     * Tra cứu theo SO_VANDON
     */
    @Query("SELECT c FROM StoKhaiCt c WHERE UPPER(TRIM(c.soVanDon)) = UPPER(TRIM(:soVanDon))")
    List<StoKhaiCt> findBySoVanDon(@Param("soVanDon") String soVanDon);
    
    /**
     * Tra cứu theo SO_HIEU
     */
    @Query("SELECT c FROM StoKhaiCt c WHERE UPPER(TRIM(c.soHieu)) = UPPER(TRIM(:soHieu))")
    List<StoKhaiCt> findBySoHieu(@Param("soHieu") String soHieu);
    
    /**
     * Tra cứu theo cả SO_VANDON và SO_HIEU
     */
    @Query("SELECT c FROM StoKhaiCt c WHERE UPPER(TRIM(c.soVanDon)) = UPPER(TRIM(:soVanDon)) AND UPPER(TRIM(c.soHieu)) = UPPER(TRIM(:soHieu))")
    List<StoKhaiCt> findBySoVanDonAndSoHieu(@Param("soVanDon") String soVanDon, @Param("soHieu") String soHieu);
    
}
