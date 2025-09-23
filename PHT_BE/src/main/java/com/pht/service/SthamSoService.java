package com.pht.service;

import java.util.List;

import com.pht.entity.SthamSo;
import com.pht.exception.BusinessException;
import com.pht.model.request.SthamSoCreateRequest;
import com.pht.model.request.SthamSoUpdateRequest;

public interface SthamSoService {
    
    List<SthamSo> getAll();
    
    SthamSo getById(Long id) throws BusinessException;
    
    SthamSo create(SthamSoCreateRequest request) throws BusinessException;
    
    SthamSo update(SthamSoUpdateRequest request) throws BusinessException;
    
    void deleteById(Long id) throws BusinessException;
    
    SthamSo findByMaTs(String maTs);
}
