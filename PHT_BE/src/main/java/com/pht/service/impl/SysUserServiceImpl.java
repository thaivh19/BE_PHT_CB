package com.pht.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pht.entity.SysUser;
import com.pht.exception.BusinessException;
import com.pht.model.request.SysUserCreateRequest;
import com.pht.model.request.SysUserUpdateRequest;
import com.pht.repository.SysUserRepository;
import com.pht.service.SysUserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class SysUserServiceImpl extends BaseServiceImpl<SysUser, Long> implements SysUserService {

    @Autowired
    private SysUserRepository sysUserRepository;

    @Override
    public SysUser getUserById(Long id) throws BusinessException {
        log.info("Lấy thông tin người dùng với ID: {}", id);
        
        Optional<SysUser> result = sysUserRepository.findById(id);
        if (!result.isPresent()) {
            throw new BusinessException("Không tìm thấy người dùng với ID: " + id);
        }
        return result.get();
    }

    @Override
    public SysUser createUser(SysUserCreateRequest request) throws BusinessException {
        log.info("Tạo mới người dùng: {}", request.getUsername());
        
        // Kiểm tra trùng lặp username
        if (sysUserRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("Tên đăng nhập đã tồn tại: " + request.getUsername());
        }
        
        // Tạo entity mới
        SysUser entity = new SysUser();
        entity.setUsername(request.getUsername());
        entity.setPassword(request.getPassword());
        entity.setGroupId(request.getGroupId());
        entity.setFullname(request.getFullname());
        entity.setMail(request.getMail());
        entity.setPhone(request.getPhone());
        entity.setAddress(request.getAddress());
        entity.setNote(request.getNote());
        
        return sysUserRepository.save(entity);
    }

    @Override
    public SysUser updateUser(SysUserUpdateRequest request) throws BusinessException {
        log.info("Cập nhật người dùng với ID: {}", request.getId());
        
        SysUser existingEntity = getUserById(request.getId());
        
        // Kiểm tra trùng lặp username nếu có thay đổi
        if (!existingEntity.getUsername().equals(request.getUsername()) && 
            sysUserRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("Tên đăng nhập đã tồn tại: " + request.getUsername());
        }
        
        // Cập nhật các trường
        existingEntity.setUsername(request.getUsername());
        existingEntity.setPassword(request.getPassword());
        existingEntity.setGroupId(request.getGroupId());
        existingEntity.setFullname(request.getFullname());
        existingEntity.setMail(request.getMail());
        existingEntity.setPhone(request.getPhone());
        existingEntity.setAddress(request.getAddress());
        existingEntity.setNote(request.getNote());
        
        return sysUserRepository.save(existingEntity);
    }

    @Override
    public void deleteUser(Long id) throws BusinessException {
        log.info("Xóa người dùng với ID: {}", id);
        if (!sysUserRepository.existsById(id)) {
            throw new BusinessException("Không tìm thấy người dùng với ID: " + id);
        }
        sysUserRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return sysUserRepository.existsById(id);
    }

    @Override
    public void deleteById(Long id) {
        try {
            deleteUser(id);
        } catch (BusinessException e) {
            log.error("Lỗi khi xóa người dùng với ID {}: {}", id, e.getMessage());
            throw new RuntimeException(e);
        }
    }


    @Override
    public SysUser findByUsername(String username) {
        return sysUserRepository.findByUsername(username);
    }

    @Override
    public List<SysUser> getAllUsers() {
        log.info("Lấy danh sách tất cả người dùng hệ thống");
        try {
            List<SysUser> result = sysUserRepository.findAll();
            log.info("Lấy thành công {} người dùng hệ thống", result.size());
            return result;
        } catch (Exception e) {
            log.error("Lỗi khi lấy danh sách người dùng hệ thống: ", e);
            throw new RuntimeException("Lỗi khi lấy danh sách người dùng hệ thống: " + e.getMessage(), e);
        }
    }
}
