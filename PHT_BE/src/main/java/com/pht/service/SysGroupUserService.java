package com.pht.service;

import com.pht.entity.SysGroupUser;
import com.pht.exception.BusinessException;
import com.pht.model.request.SysGroupUserCreateRequest;
import com.pht.model.request.SysGroupUserUpdateRequest;

public interface SysGroupUserService extends BaseService<SysGroupUser, Long> {
    
    SysGroupUser getGroupUserById(Long id) throws BusinessException;
    
    SysGroupUser createGroupUser(SysGroupUserCreateRequest request) throws BusinessException;
    
    SysGroupUser updateGroupUser(SysGroupUserUpdateRequest request) throws BusinessException;
    
    void deleteGroupUser(Long id) throws BusinessException;
}









