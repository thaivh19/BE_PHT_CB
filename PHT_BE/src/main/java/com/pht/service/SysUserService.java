package com.pht.service;

import java.util.List;

import com.pht.entity.SysUser;
import com.pht.exception.BusinessException;
import com.pht.model.request.SysUserCreateRequest;
import com.pht.model.request.SysUserUpdateRequest;

public interface SysUserService extends BaseService<SysUser, Long> {
    
    List<SysUser> getAllUsers();
    
    SysUser getUserById(Long id) throws BusinessException;
    
    SysUser createUser(SysUserCreateRequest request) throws BusinessException;
    
    SysUser updateUser(SysUserUpdateRequest request) throws BusinessException;
    
    void deleteUser(Long id) throws BusinessException;
    
    SysUser findByUsername(String username);
}



