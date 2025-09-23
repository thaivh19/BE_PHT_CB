package com.pht.service;

import com.pht.entity.SysDisFeat;
import com.pht.exception.BusinessException;
import com.pht.model.request.SysDisFeatCreateRequest;
import com.pht.model.request.SysDisFeatUpdateRequest;

import java.util.List;

public interface SysDisFeatService extends BaseService<SysDisFeat, Long> {
    
    SysDisFeat getDisFeatById(Long id) throws BusinessException;
    
    SysDisFeat createDisFeat(SysDisFeatCreateRequest request) throws BusinessException;
    
    SysDisFeat updateDisFeat(SysDisFeatUpdateRequest request) throws BusinessException;
    
    void deleteDisFeat(Long id) throws BusinessException;
    
    List<SysDisFeat> getByUserId(Long userId);
}









