package com.pht.service;

import java.util.List;

import com.pht.entity.NgayLv;
import com.pht.exception.BusinessException;
import com.pht.model.request.NgayLvCreateRequest;
import com.pht.model.request.NgayLvUpdateRequest;

public interface NgayLvService {
    
    List<NgayLv> getAll();
    
    NgayLv getById(Long id) throws BusinessException;
    
    NgayLv create(NgayLvCreateRequest request) throws BusinessException;
    
    NgayLv update(NgayLvUpdateRequest request) throws BusinessException;
    
    void deleteById(Long id) throws BusinessException;
    
    List<NgayLv> findByTrangThai(String trangThai);
    
    List<NgayLv> findByCot(String cot);
}
