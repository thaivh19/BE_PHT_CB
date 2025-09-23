package com.pht.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pht.entity.ShaiQuan;

@Repository
public interface ShaiQuanRepository extends BaseRepository<ShaiQuan, Long> {
    
    @Query("SELECT s FROM ShaiQuan s WHERE " +
           "(:maHq IS NULL OR LOWER(s.maHq) LIKE LOWER(:maHq)) AND " +
           "(:tenHq IS NULL OR LOWER(s.tenHq) LIKE LOWER(:tenHq)) AND " +
           "(:trangThai IS NULL OR s.trangThai = :trangThai)")
    Page<ShaiQuan> findBySearchCriteria(@Param("maHq") String maHq, 
                                       @Param("tenHq") String tenHq, 
                                       @Param("trangThai") String trangThai, 
                                       Pageable pageable);
    
    @Query("SELECT s FROM ShaiQuan s WHERE " +
           "(:maHq IS NULL OR LOWER(s.maHq) LIKE LOWER(:maHq)) AND " +
           "(:tenHq IS NULL OR LOWER(s.tenHq) LIKE LOWER(:tenHq)) AND " +
           "(:trangThai IS NULL OR s.trangThai = :trangThai)")
    List<ShaiQuan> findBySearchCriteria(@Param("maHq") String maHq, 
                                       @Param("tenHq") String tenHq, 
                                       @Param("trangThai") String trangThai);
    
    boolean existsByMaHq(String maHq);
}
