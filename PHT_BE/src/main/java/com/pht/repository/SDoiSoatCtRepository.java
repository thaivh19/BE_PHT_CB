package com.pht.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pht.entity.SDoiSoatCt;

@Repository
public interface SDoiSoatCtRepository extends BaseRepository<SDoiSoatCt, Long> {
    
    @Query("SELECT ct FROM SDoiSoatCt ct WHERE ct.doiSoatId = :doiSoatId ORDER BY ct.id")
    List<SDoiSoatCt> findByDoiSoatId(@Param("doiSoatId") Long doiSoatId);
    
    @Query("SELECT ct FROM SDoiSoatCt ct WHERE ct.stoKhaiId = :stoKhaiId")
    SDoiSoatCt findByStoKhaiId(@Param("stoKhaiId") Long stoKhaiId);
    
    @Query("SELECT ct FROM SDoiSoatCt ct WHERE ct.soToKhai = :soToKhai")
    SDoiSoatCt findBySoToKhai(@Param("soToKhai") String soToKhai);
    
    /**
     * Tìm chi tiết đối soát theo ngày đối soát và ngân hàng
     */
    @Query("SELECT ct FROM SDoiSoatCt ct " +
           "JOIN ct.doiSoat ds " +
           "WHERE ds.ngayDs = :ngayDs AND ct.nganHang = :nganHang " +
           "ORDER BY ct.id")
    List<SDoiSoatCt> findByNgayDsAndNganHang(@Param("ngayDs") LocalDate ngayDs, @Param("nganHang") String nganHang);
    
    /**
     * Tìm chi tiết đối soát theo transId
     */
    @Query("SELECT ct FROM SDoiSoatCt ct WHERE ct.transId = :transId")
    SDoiSoatCt findByTransId(@Param("transId") String transId);
    
    /**
     * Tìm tất cả chi tiết đối soát theo ngày đối soát (không filter theo ngân hàng)
     */
    @Query("SELECT ct FROM SDoiSoatCt ct " +
           "JOIN ct.doiSoat ds " +
           "WHERE ds.ngayDs = :ngayDs " +
           "ORDER BY ct.id")
    List<SDoiSoatCt> findByNgayDs(@Param("ngayDs") LocalDate ngayDs);
    
    /**
     * Tìm chi tiết đối soát theo ngày đối soát và ngân hàng với LAN_DS lớn nhất
     */
    @Query("SELECT ct FROM SDoiSoatCt ct " +
           "JOIN ct.doiSoat ds " +
           "WHERE ds.ngayDs = :ngayDs AND ct.nganHang = :nganHang " +
           "AND ds.lanDs = (SELECT MAX(ds2.lanDs) FROM SDoiSoat ds2 WHERE ds2.ngayDs = :ngayDs) " +
           "ORDER BY ct.id")
    List<SDoiSoatCt> findByNgayDsAndNganHangMaxLanDs(@Param("ngayDs") LocalDate ngayDs, @Param("nganHang") String nganHang);
    
    /**
     * Tìm tất cả chi tiết đối soát theo ngày đối soát với LAN_DS lớn nhất (không filter theo ngân hàng)
     */
    @Query("SELECT ct FROM SDoiSoatCt ct " +
           "JOIN ct.doiSoat ds " +
           "WHERE ds.ngayDs = :ngayDs " +
           "AND ds.lanDs = (SELECT MAX(ds2.lanDs) FROM SDoiSoat ds2 WHERE ds2.ngayDs = :ngayDs) " +
           "ORDER BY ct.id")
    List<SDoiSoatCt> findByNgayDsMaxLanDs(@Param("ngayDs") LocalDate ngayDs);
    
    /**
     * Tìm các bản ghi SDOI_SOAT_CT theo ngày đối soát với LAN_DS lớn nhất và KB_DS = "00" (chưa đối soát)
     */
    @Query("SELECT ct FROM SDoiSoatCt ct " +
           "JOIN ct.doiSoat ds " +
           "WHERE ds.ngayDs = :ngayDs " +
           "AND ds.lanDs = (SELECT MAX(ds2.lanDs) FROM SDoiSoat ds2 WHERE ds2.ngayDs = :ngayDs) " +
           "AND (ct.kbDs IS NULL OR ct.kbDs = '00') " +
           "ORDER BY ct.id")
    List<SDoiSoatCt> findByNgayDsMaxLanDsAndKbDs00(@Param("ngayDs") LocalDate ngayDs);
    
    /**
     * Tìm các bản ghi SDOI_SOAT_CT theo ngày đối soát với LAN_DS lớn nhất, ngân hàng và NH_DS = "00" (chưa đối soát)
     */
    @Query("SELECT ct FROM SDoiSoatCt ct " +
           "JOIN ct.doiSoat ds " +
           "WHERE ds.ngayDs = :ngayDs " +
           "AND ct.nganHang = :nganHang " +
           "AND ds.lanDs = (SELECT MAX(ds2.lanDs) FROM SDoiSoat ds2 WHERE ds2.ngayDs = :ngayDs) " +
           "AND (ct.nhDs IS NULL OR ct.nhDs = '00') " +
           "ORDER BY ct.id")
    List<SDoiSoatCt> findByNgayDsAndNganHangMaxLanDsAndNhDs00(@Param("ngayDs") LocalDate ngayDs, @Param("nganHang") String nganHang);
}





