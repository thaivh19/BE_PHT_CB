package com.pht.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pht.entity.SBienLai;

@Repository
public interface SBienLaiRepository extends BaseRepository<SBienLai, Long> {

    @Query("SELECT s FROM SBienLai s WHERE " +
           "(:mst IS NULL OR LOWER(s.mst) LIKE LOWER(:mst)) AND " +
           "(:tenDvi IS NULL OR LOWER(s.tenDvi) LIKE LOWER(:tenDvi)) AND " +
           "(:maBl IS NULL OR LOWER(s.maBl) LIKE LOWER(:maBl)) AND " +
           "(:soBl IS NULL OR LOWER(s.soBl) LIKE LOWER(:soBl)) AND " +
           "(:soTk IS NULL OR LOWER(s.soTk) LIKE LOWER(:soTk)) AND " +
           "(:maKho IS NULL OR LOWER(s.maKho) LIKE LOWER(:maKho))")
    Page<SBienLai> findBySearchCriteria(@Param("mst") String mst,
                                        @Param("tenDvi") String tenDvi,
                                        @Param("maBl") String maBl,
                                        @Param("soBl") String soBl,
                                        @Param("soTk") String soTk,
                                        @Param("maKho") String maKho,
                                        Pageable pageable);

    @Query("SELECT s FROM SBienLai s WHERE " +
           "(:mst IS NULL OR LOWER(s.mst) LIKE LOWER(:mst)) AND " +
           "(:tenDvi IS NULL OR LOWER(s.tenDvi) LIKE LOWER(:tenDvi)) AND " +
           "(:maBl IS NULL OR LOWER(s.maBl) LIKE LOWER(:maBl)) AND " +
           "(:soBl IS NULL OR LOWER(s.soBl) LIKE LOWER(:soBl)) AND " +
           "(:soTk IS NULL OR LOWER(s.soTk) LIKE LOWER(:soTk)) AND " +
           "(:maKho IS NULL OR LOWER(s.maKho) LIKE LOWER(:maKho))")
    List<SBienLai> findBySearchCriteria(@Param("mst") String mst,
                                        @Param("tenDvi") String tenDvi,
                                        @Param("maBl") String maBl,
                                        @Param("soBl") String soBl,
                                        @Param("soTk") String soTk,
                                        @Param("maKho") String maKho);

    boolean existsByMaBl(String maBl);
    
    boolean existsBySoBl(String soBl);
    
    SBienLai findByMaBl(String maBl);
}
