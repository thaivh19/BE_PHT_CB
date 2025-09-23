package com.pht.repository;

import org.springframework.stereotype.Repository;

import com.pht.entity.SthamSo;

@Repository
public interface SthamSoRepository extends BaseRepository<SthamSo, Long> {
    
    SthamSo findByMaTs(String maTs);
}
