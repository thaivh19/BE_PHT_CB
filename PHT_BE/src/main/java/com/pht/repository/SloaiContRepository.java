package com.pht.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pht.entity.SloaiCont;

@Repository
public interface SloaiContRepository extends BaseRepository<SloaiCont, Long> {

    @Query("SELECT s FROM SloaiCont s WHERE " +
           "(:ma IS NULL OR LOWER(s.ma) LIKE LOWER(:ma)) AND " +
           "(:ten IS NULL OR LOWER(s.ten) LIKE LOWER(:ten)) AND " +
           "(:trangThai IS NULL OR s.trangThai = :trangThai)")
    Page<SloaiCont> findBySearchCriteria(@Param("ma") String ma,
                                       @Param("ten") String ten,
                                       @Param("trangThai") String trangThai,
                                       Pageable pageable);

    @Query("SELECT s FROM SloaiCont s WHERE " +
           "(:ma IS NULL OR LOWER(s.ma) LIKE LOWER(:ma)) AND " +
           "(:ten IS NULL OR LOWER(s.ten) LIKE LOWER(:ten)) AND " +
           "(:trangThai IS NULL OR s.trangThai = :trangThai)")
    List<SloaiCont> findBySearchCriteria(@Param("ma") String ma,
                                       @Param("ten") String ten,
                                       @Param("trangThai") String trangThai);

    boolean existsByMa(String ma);
}