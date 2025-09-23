package com.pht.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pht.entity.SysGroupUser;
import com.pht.exception.BusinessException;
import com.pht.model.request.SysGroupUserCreateRequest;
import com.pht.model.request.SysGroupUserUpdateRequest;
import com.pht.repository.SysGroupUserRepository;
import com.pht.service.SysGroupUserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class SysGroupUserServiceImpl extends BaseServiceImpl<SysGroupUser, Long> implements SysGroupUserService {

    @Autowired
    private SysGroupUserRepository sysGroupUserRepository;

    @Override
    public SysGroupUser getGroupUserById(Long id) throws BusinessException {
        log.info("Lấy thông tin nhóm người dùng với ID: {}", id);
        
        Optional<SysGroupUser> result = sysGroupUserRepository.findById(id);
        if (!result.isPresent()) {
            throw new BusinessException("Không tìm thấy nhóm người dùng với ID: " + id);
        }
        return result.get();
    }

    @Override
    public SysGroupUser createGroupUser(SysGroupUserCreateRequest request) throws BusinessException {
        log.info("Tạo mới nhóm người dùng: {}", request.getGroupName());
        
        // Tạo entity mới
        SysGroupUser entity = new SysGroupUser();
        entity.setGroupName(request.getGroupName());
        
        return sysGroupUserRepository.save(entity);
    }

    @Override
    public SysGroupUser updateGroupUser(SysGroupUserUpdateRequest request) throws BusinessException {
        log.info("Cập nhật nhóm người dùng với ID: {}", request.getId());
        
        SysGroupUser existingEntity = getGroupUserById(request.getId());
        
        // Cập nhật các trường
        existingEntity.setGroupName(request.getGroupName());
        
        return sysGroupUserRepository.save(existingEntity);
    }

    @Override
    public void deleteGroupUser(Long id) throws BusinessException {
        log.info("Xóa nhóm người dùng với ID: {}", id);
        if (!sysGroupUserRepository.existsById(id)) {
            throw new BusinessException("Không tìm thấy nhóm người dùng với ID: " + id);
        }
        sysGroupUserRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return sysGroupUserRepository.existsById(id);
    }

    @Override
    public void deleteById(Long id) {
        try {
            deleteGroupUser(id);
        } catch (BusinessException e) {
            log.error("Lỗi khi xóa nhóm người dùng với ID {}: {}", id, e.getMessage());
            throw new RuntimeException(e);
        }
    }
}









