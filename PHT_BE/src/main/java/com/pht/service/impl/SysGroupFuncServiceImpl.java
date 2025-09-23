package com.pht.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pht.entity.SysGroupFunc;
import com.pht.exception.BusinessException;
import com.pht.model.request.SysGroupFuncCreateRequest;
import com.pht.model.request.SysGroupFuncUpdateRequest;
import com.pht.repository.SysGroupFuncRepository;
import com.pht.service.SysGroupFuncService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class SysGroupFuncServiceImpl extends BaseServiceImpl<SysGroupFunc, Long> implements SysGroupFuncService {

    @Autowired
    private SysGroupFuncRepository sysGroupFuncRepository;

    @Override
    public SysGroupFunc getGroupFuncById(Long id) throws BusinessException {
        log.info("Lấy thông tin phân quyền nhóm với ID: {}", id);
        
        Optional<SysGroupFunc> result = sysGroupFuncRepository.findById(id);
        if (!result.isPresent()) {
            throw new BusinessException("Không tìm thấy phân quyền nhóm với ID: " + id);
        }
        return result.get();
    }

    @Override
    public SysGroupFunc createGroupFunc(SysGroupFuncCreateRequest request) throws BusinessException {
        log.info("Tạo mới phân quyền nhóm: groupId={}, funcId={}", request.getGroupId(), request.getFuncId());
        
        // Tạo entity mới
        SysGroupFunc entity = new SysGroupFunc();
        entity.setFuncId(request.getFuncId());
        entity.setGroupId(request.getGroupId());
        
        return sysGroupFuncRepository.save(entity);
    }

    @Override
    public SysGroupFunc updateGroupFunc(SysGroupFuncUpdateRequest request) throws BusinessException {
        log.info("Cập nhật phân quyền nhóm với ID: {}", request.getId());
        
        SysGroupFunc existingEntity = getGroupFuncById(request.getId());
        
        // Cập nhật các trường
        existingEntity.setFuncId(request.getFuncId());
        existingEntity.setGroupId(request.getGroupId());
        
        return sysGroupFuncRepository.save(existingEntity);
    }

    @Override
    public void deleteGroupFunc(Long id) throws BusinessException {
        log.info("Xóa phân quyền nhóm với ID: {}", id);
        if (!sysGroupFuncRepository.existsById(id)) {
            throw new BusinessException("Không tìm thấy phân quyền nhóm với ID: " + id);
        }
        sysGroupFuncRepository.deleteById(id);
    }

    @Override
    public List<SysGroupFunc> getByGroupId(Long groupId) {
        log.info("Lấy danh sách phân quyền theo groupId: {}", groupId);
        return sysGroupFuncRepository.findByGroupId(groupId);
    }

    @Override
    public boolean existsById(Long id) {
        return sysGroupFuncRepository.existsById(id);
    }

    @Override
    public void deleteById(Long id) {
        try {
            deleteGroupFunc(id);
        } catch (BusinessException e) {
            log.error("Lỗi khi xóa phân quyền nhóm với ID {}: {}", id, e.getMessage());
            throw new RuntimeException(e);
        }
    }
}

