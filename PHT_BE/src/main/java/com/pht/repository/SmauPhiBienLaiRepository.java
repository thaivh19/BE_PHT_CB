package com.pht.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pht.entity.SmauPhiBienLai;

@Repository
public interface SmauPhiBienLaiRepository extends BaseRepository<SmauPhiBienLai, Long> {

    @Query("SELECT s FROM SmauPhiBienLai s WHERE " +
           "(:mauBienLai IS NULL OR LOWER(s.mauBienLai) LIKE LOWER(:mauBienLai)) AND " +
           "(:kyHieu IS NULL OR LOWER(s.kyHieu) LIKE LOWER(:kyHieu)) AND " +
           "(:diemThuPhi IS NULL OR LOWER(s.diemThuPhi) LIKE LOWER(:diemThuPhi)) AND " +
           "(:trangThai IS NULL OR s.trangThai = :trangThai)")
    Page<SmauPhiBienLai> findBySearchCriteria(@Param("mauBienLai") String mauBienLai,
                                             @Param("kyHieu") String kyHieu,
                                             @Param("diemThuPhi") String diemThuPhi,
                                             @Param("trangThai") String trangThai,
                                             Pageable pageable);

    @Query("SELECT s FROM SmauPhiBienLai s WHERE " +
           "(:mauBienLai IS NULL OR LOWER(s.mauBienLai) LIKE LOWER(:mauBienLai)) AND " +
           "(:kyHieu IS NULL OR LOWER(s.kyHieu) LIKE LOWER(:kyHieu)) AND " +
           "(:diemThuPhi IS NULL OR LOWER(s.diemThuPhi) LIKE LOWER(:diemThuPhi)) AND " +
           "(:trangThai IS NULL OR s.trangThai = :trangThai)")
    List<SmauPhiBienLai> findBySearchCriteria(@Param("mauBienLai") String mauBienLai,
                                             @Param("kyHieu") String kyHieu,
                                             @Param("diemThuPhi") String diemThuPhi,
                                             @Param("trangThai") String trangThai);

    boolean existsByKyHieu(String kyHieu);
}
