package com.pht.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.pht.entity.SDonHangCt;

@Repository
public interface SDonHangCtRepository extends BaseRepository<SDonHangCt, Long> {

    List<SDonHangCt> findByDonHangId(Long donHangId);
    
    void deleteByDonHangId(Long donHangId);
}



