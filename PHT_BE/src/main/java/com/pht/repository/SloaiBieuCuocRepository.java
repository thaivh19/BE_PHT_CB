package com.pht.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pht.entity.SloaiBieuCuoc;

@Repository
public interface SloaiBieuCuocRepository extends BaseRepository<SloaiBieuCuoc, Long> {

    @Query("SELECT s FROM SloaiBieuCuoc s WHERE " +
           "(:ma IS NULL OR LOWER(s.ma) LIKE LOWER(:ma)) AND " +
           "(:ten IS NULL OR LOWER(s.ten) LIKE LOWER(:ten)) AND " +
           "(:trangThai IS NULL OR s.trangThai = :trangThai)")
    Page<SloaiBieuCuoc> findBySearchCriteria(@Param("ma") String ma,
                                            @Param("ten") String ten,
                                            @Param("trangThai") String trangThai,
                                            Pageable pageable);

    @Query("SELECT s FROM SloaiBieuCuoc s WHERE " +
           "(:ma IS NULL OR LOWER(s.ma) LIKE LOWER(:ma)) AND " +
           "(:ten IS NULL OR LOWER(s.ten) LIKE LOWER(:ten)) AND " +
           "(:trangThai IS NULL OR s.trangThai = :trangThai)")
    List<SloaiBieuCuoc> findBySearchCriteria(@Param("ma") String ma,
                                            @Param("ten") String ten,
                                            @Param("trangThai") String trangThai);

    boolean existsByMa(String ma);
}
