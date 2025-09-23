package com.pht.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pht.entity.SDoiSoat;

@Repository
public interface SDoiSoatRepository extends BaseRepository<SDoiSoat, Long> {
    
    @Query("SELECT ds FROM SDoiSoat ds WHERE ds.ngayDs = :ngayDs ORDER BY ds.id DESC")
    List<SDoiSoat> findByNgayDs(@Param("ngayDs") LocalDate ngayDs);
    
    @Query("SELECT ds FROM SDoiSoat ds WHERE ds.ngayDs = :ngayDs ORDER BY ds.lanDs DESC")
    List<SDoiSoat> findByNgayDsOrderByLanDsDesc(@Param("ngayDs") LocalDate ngayDs);
    
    @Query("SELECT ds FROM SDoiSoat ds WHERE ds.trangThai = :trangThai ORDER BY ds.id DESC")
    List<SDoiSoat> findByTrangThai(@Param("trangThai") String trangThai);
    
    @Query("SELECT ds FROM SDoiSoat ds WHERE ds.soBk = :soBk")
    SDoiSoat findBySoBk(@Param("soBk") String soBk);
    
    /**
     * Tìm bản ghi đối soát có LAN_DS lớn nhất theo ngày
     */
    @Query("SELECT ds FROM SDoiSoat ds WHERE ds.ngayDs = :ngayDs AND ds.lanDs = (SELECT MAX(ds2.lanDs) FROM SDoiSoat ds2 WHERE ds2.ngayDs = :ngayDs)")
    List<SDoiSoat> findByNgayDsAndMaxLanDs(@Param("ngayDs") LocalDate ngayDs);
}


