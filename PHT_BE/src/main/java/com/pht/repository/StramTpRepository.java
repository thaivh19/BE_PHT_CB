package com.pht.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pht.entity.StramTp;

@Repository
public interface StramTpRepository extends BaseRepository<StramTp, Long> {

    @Query("SELECT s FROM StramTp s WHERE " +
           "(:maTramTp IS NULL OR LOWER(s.maTramTp) LIKE LOWER(:maTramTp)) AND " +
           "(:tenTramTp IS NULL OR LOWER(s.tenTramTp) LIKE LOWER(:tenTramTp)) AND " +
           "(:trangThai IS NULL OR s.trangThai = :trangThai)")
    Page<StramTp> findBySearchCriteria(@Param("maTramTp") String maTramTp,
                                      @Param("tenTramTp") String tenTramTp,
                                      @Param("trangThai") String trangThai,
                                      Pageable pageable);

    @Query("SELECT s FROM StramTp s WHERE " +
           "(:maTramTp IS NULL OR LOWER(s.maTramTp) LIKE LOWER(:maTramTp)) AND " +
           "(:tenTramTp IS NULL OR LOWER(s.tenTramTp) LIKE LOWER(:tenTramTp)) AND " +
           "(:trangThai IS NULL OR s.trangThai = :trangThai)")
    List<StramTp> findBySearchCriteria(@Param("maTramTp") String maTramTp,
                                      @Param("tenTramTp") String tenTramTp,
                                      @Param("trangThai") String trangThai);

    boolean existsByMaTramTp(String maTramTp);
}