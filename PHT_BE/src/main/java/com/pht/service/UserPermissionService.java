package com.pht.service;

import com.pht.dto.UserPermissionDto;
import com.pht.exception.BusinessException;

public interface UserPermissionService {
    
    UserPermissionDto getUserPermissions(Long userId) throws BusinessException;
    
    UserPermissionDto getUserPermissionsByUsername(String username) throws BusinessException;
}









