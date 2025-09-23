package com.pht.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pht.entity.SDoiSoatCtLech;

@Repository
public interface SDoiSoatCtLechRepository extends BaseRepository<SDoiSoatCtLech, Long> {
    
    @Query("SELECT l FROM SDoiSoatCtLech l WHERE l.doiSoatId = :doiSoatId ORDER BY l.id")
    List<SDoiSoatCtLech> findByDoiSoatId(@Param("doiSoatId") Long doiSoatId);
    
    @Query("SELECT l FROM SDoiSoatCtLech l WHERE l.doiSoatCtId = :doiSoatCtId ORDER BY l.id")
    List<SDoiSoatCtLech> findByDoiSoatCtId(@Param("doiSoatCtId") Long doiSoatCtId);
    
    @Query("SELECT l FROM SDoiSoatCtLech l WHERE l.soToKhai = :soToKhai ORDER BY l.id")
    List<SDoiSoatCtLech> findBySoToKhai(@Param("soToKhai") String soToKhai);
}









