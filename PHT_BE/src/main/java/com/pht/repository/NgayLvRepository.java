package com.pht.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.pht.entity.NgayLv;

@Repository
public interface NgayLvRepository extends BaseRepository<NgayLv, Long> {
    
    List<NgayLv> findByTrangThai(String trangThai);
    
    List<NgayLv> findByCot(String cot);
}
