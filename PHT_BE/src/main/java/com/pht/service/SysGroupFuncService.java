package com.pht.service;

import com.pht.entity.SysGroupFunc;
import com.pht.exception.BusinessException;
import com.pht.model.request.SysGroupFuncCreateRequest;
import com.pht.model.request.SysGroupFuncUpdateRequest;

import java.util.List;

public interface SysGroupFuncService extends BaseService<SysGroupFunc, Long> {
    
    SysGroupFunc getGroupFuncById(Long id) throws BusinessException;
    
    SysGroupFunc createGroupFunc(SysGroupFuncCreateRequest request) throws BusinessException;
    
    SysGroupFunc updateGroupFunc(SysGroupFuncUpdateRequest request) throws BusinessException;
    
    void deleteGroupFunc(Long id) throws BusinessException;
    
    List<SysGroupFunc> getByGroupId(Long groupId);
}









