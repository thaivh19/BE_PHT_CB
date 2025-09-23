package com.pht.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pht.entity.SysDisFeat;

@Repository
public interface SysDisFeatRepository extends BaseRepository<SysDisFeat, Long> {

    @Query("SELECT df FROM SysDisFeat df WHERE df.userId = :userId")
    List<SysDisFeat> findByUserId(@Param("userId") Long userId);
}









