package com.pht.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pht.entity.SphuongThucVanTai;

@Repository
public interface SphuongThucVanTaiRepository extends BaseRepository<SphuongThucVanTai, Long> {

    @Query("SELECT s FROM SphuongThucVanTai s WHERE " +
           "(:maPtvt IS NULL OR LOWER(s.maPtvt) LIKE LOWER(:maPtvt)) AND " +
           "(:tenPtvt IS NULL OR LOWER(s.tenPtvt) LIKE LOWER(:tenPtvt)) AND " +
           "(:trangThai IS NULL OR s.trangThai = :trangThai)")
    Page<SphuongThucVanTai> findBySearchCriteria(@Param("maPtvt") String maPtvt,
                                                @Param("tenPtvt") String tenPtvt,
                                                @Param("trangThai") String trangThai,
                                                Pageable pageable);

    @Query("SELECT s FROM SphuongThucVanTai s WHERE " +
           "(:maPtvt IS NULL OR LOWER(s.maPtvt) LIKE LOWER(:maPtvt)) AND " +
           "(:tenPtvt IS NULL OR LOWER(s.tenPtvt) LIKE LOWER(:tenPtvt)) AND " +
           "(:trangThai IS NULL OR s.trangThai = :trangThai)")
    List<SphuongThucVanTai> findBySearchCriteria(@Param("maPtvt") String maPtvt,
                                                @Param("tenPtvt") String tenPtvt,
                                                @Param("trangThai") String trangThai);

    boolean existsByMaPtvt(String maPtvt);
}