package com.pht.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pht.entity.SnganHang;

@Repository
public interface SnganHangRepository extends BaseRepository<SnganHang, Long> {
    
    @Query("SELECT s FROM SnganHang s WHERE " +
           "(:maNh IS NULL OR LOWER(s.maNh) LIKE LOWER(:maNh)) AND " +
           "(:tenNh IS NULL OR LOWER(s.tenNh) LIKE LOWER(:tenNh)) AND " +
           "(:trangThai IS NULL OR s.trangThai = :trangThai)")
    Page<SnganHang> findBySearchCriteria(@Param("maNh") String maNh, 
                                        @Param("tenNh") String tenNh, 
                                        @Param("trangThai") String trangThai, 
                                        Pageable pageable);
    
    @Query("SELECT s FROM SnganHang s WHERE " +
           "(:maNh IS NULL OR LOWER(s.maNh) LIKE LOWER(:maNh)) AND " +
           "(:tenNh IS NULL OR LOWER(s.tenNh) LIKE LOWER(:tenNh)) AND " +
           "(:trangThai IS NULL OR s.trangThai = :trangThai)")
    List<SnganHang> findBySearchCriteria(@Param("maNh") String maNh, 
                                        @Param("tenNh") String tenNh, 
                                        @Param("trangThai") String trangThai);
    
    boolean existsByMaNh(String maNh);
}