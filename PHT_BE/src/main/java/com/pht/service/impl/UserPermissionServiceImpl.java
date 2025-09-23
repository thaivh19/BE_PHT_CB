package com.pht.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pht.dto.FunctionDto;
import com.pht.dto.UserPermissionDto;
import com.pht.entity.SysDisFeat;
import com.pht.entity.SysFunc;
import com.pht.entity.SysGroupFunc;
import com.pht.entity.SysGroupUser;
import com.pht.entity.SysUser;
import com.pht.exception.BusinessException;
import com.pht.repository.SysDisFeatRepository;
import com.pht.repository.SysFuncRepository;
import com.pht.repository.SysGroupFuncRepository;
import com.pht.repository.SysGroupUserRepository;
import com.pht.repository.SysUserRepository;
import com.pht.service.UserPermissionService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
public class UserPermissionServiceImpl implements UserPermissionService {

    @Autowired
    private SysUserRepository sysUserRepository;
    
    @Autowired
    private SysGroupUserRepository sysGroupUserRepository;
    
    @Autowired
    private SysGroupFuncRepository sysGroupFuncRepository;
    
    @Autowired
    private SysFuncRepository sysFuncRepository;
    
    @Autowired
    private SysDisFeatRepository sysDisFeatRepository;

    @Override
    public UserPermissionDto getUserPermissions(Long userId) throws BusinessException {
        log.info("Lấy quyền của người dùng với ID: {}", userId);
        
        // Lấy thông tin người dùng
        Optional<SysUser> userOpt = sysUserRepository.findById(userId);
        if (!userOpt.isPresent()) {
            throw new BusinessException("Không tìm thấy người dùng với ID: " + userId);
        }
        
        SysUser user = userOpt.get();
        return buildUserPermissionDto(user);
    }

    @Override
    public UserPermissionDto getUserPermissionsByUsername(String username) throws BusinessException {
        log.info("Lấy quyền của người dùng với username: {}", username);
        
        // Lấy thông tin người dùng theo username
        SysUser user = sysUserRepository.findByUsername(username);
        if (user == null) {
            throw new BusinessException("Không tìm thấy người dùng với username: " + username);
        }
        
        return buildUserPermissionDto(user);
    }

    private UserPermissionDto buildUserPermissionDto(SysUser user) {
        UserPermissionDto dto = new UserPermissionDto();
        
        // Thông tin người dùng
        dto.setUserId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setFullname(user.getFullname());
        dto.setMail(user.getMail());
        dto.setPhone(user.getPhone());
        dto.setAddress(user.getAddress());
        dto.setNote(user.getNote());
        
        // Thông tin nhóm
        if (user.getGroupId() != null) {
            Optional<SysGroupUser> groupOpt = sysGroupUserRepository.findById(user.getGroupId());
            if (groupOpt.isPresent()) {
                SysGroupUser group = groupOpt.get();
                dto.setGroupId(group.getId());
                dto.setGroupName(group.getGroupName());
            }
        }
        
        // Lấy danh sách function được phép
        List<FunctionDto> allowedFunctions = getAllowedFunctions(user.getId(), user.getGroupId());
        dto.setAllowedFunctions(allowedFunctions);
        
        // Tạo danh sách function code từ allowedFunctions
        List<String> listFunction = allowedFunctions.stream()
                .map(FunctionDto::getFuncIdCode)
                .collect(Collectors.toList());
        dto.setListFunction(listFunction);
        
        return dto;
    }

    private List<FunctionDto> getAllowedFunctions(Long userId, Long groupId) {
        List<FunctionDto> allowedFunctions = new ArrayList<>();
        
        // Lấy các function từ group (nếu có group)
        if (groupId != null) {
            List<SysGroupFunc> groupFunctions = sysGroupFuncRepository.findByGroupId(groupId);
            for (SysGroupFunc groupFunc : groupFunctions) {
                Optional<SysFunc> funcOpt = sysFuncRepository.findById(groupFunc.getFuncId());
                if (funcOpt.isPresent()) {
                    SysFunc func = funcOpt.get();
                    FunctionDto funcDto = new FunctionDto();
                    funcDto.setFuncId(func.getId());
                    funcDto.setFuncIdCode(func.getFuncId());
                    funcDto.setFuncName(func.getFuncName());
                    allowedFunctions.add(funcDto);
                }
            }
        }
        
        // Lấy danh sách các function bị disable cho user này
        List<SysDisFeat> disabledFeatures = sysDisFeatRepository.findByUserId(userId);
        List<Long> disabledFuncIds = disabledFeatures.stream()
                .map(SysDisFeat::getFuncId)
                .collect(Collectors.toList());
        
        // Loại bỏ các function bị disable
        allowedFunctions = allowedFunctions.stream()
                .filter(func -> !disabledFuncIds.contains(func.getFuncId()))
                .collect(Collectors.toList());
        
        log.info("Người dùng {} có {} function được phép", userId, allowedFunctions.size());
        
        return allowedFunctions;
    }
}
