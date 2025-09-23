package com.pht.service;

import com.pht.entity.SysFunc;
import com.pht.exception.BusinessException;
import com.pht.model.request.SysFuncCreateRequest;
import com.pht.model.request.SysFuncUpdateRequest;

public interface SysFuncService extends BaseService<SysFunc, Long> {
    
    SysFunc getFuncById(Long id) throws BusinessException;
    
    SysFunc createFunc(SysFuncCreateRequest request) throws BusinessException;
    
    SysFunc updateFunc(SysFuncUpdateRequest request) throws BusinessException;
    
    void deleteFunc(Long id) throws BusinessException;
}









