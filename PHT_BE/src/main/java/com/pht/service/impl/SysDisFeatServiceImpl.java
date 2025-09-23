package com.pht.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pht.entity.SysDisFeat;
import com.pht.exception.BusinessException;
import com.pht.model.request.SysDisFeatCreateRequest;
import com.pht.model.request.SysDisFeatUpdateRequest;
import com.pht.repository.SysDisFeatRepository;
import com.pht.service.SysDisFeatService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class SysDisFeatServiceImpl extends BaseServiceImpl<SysDisFeat, Long> implements SysDisFeatService {

    @Autowired
    private SysDisFeatRepository sysDisFeatRepository;

    @Override
    public SysDisFeat getDisFeatById(Long id) throws BusinessException {
        log.info("Lấy thông tin phân quyền người dùng với ID: {}", id);
        
        Optional<SysDisFeat> result = sysDisFeatRepository.findById(id);
        if (!result.isPresent()) {
            throw new BusinessException("Không tìm thấy phân quyền người dùng với ID: " + id);
        }
        return result.get();
    }

    @Override
    public SysDisFeat createDisFeat(SysDisFeatCreateRequest request) throws BusinessException {
        log.info("Tạo mới phân quyền người dùng: userId={}, funcId={}", request.getUserId(), request.getFuncId());
        
        // Tạo entity mới
        SysDisFeat entity = new SysDisFeat();
        entity.setUserId(request.getUserId());
        entity.setFuncId(request.getFuncId());
        
        return sysDisFeatRepository.save(entity);
    }

    @Override
    public SysDisFeat updateDisFeat(SysDisFeatUpdateRequest request) throws BusinessException {
        log.info("Cập nhật phân quyền người dùng với ID: {}", request.getId());
        
        SysDisFeat existingEntity = getDisFeatById(request.getId());
        
        // Cập nhật các trường
        existingEntity.setUserId(request.getUserId());
        existingEntity.setFuncId(request.getFuncId());
        
        return sysDisFeatRepository.save(existingEntity);
    }

    @Override
    public void deleteDisFeat(Long id) throws BusinessException {
        log.info("Xóa phân quyền người dùng với ID: {}", id);
        if (!sysDisFeatRepository.existsById(id)) {
            throw new BusinessException("Không tìm thấy phân quyền người dùng với ID: " + id);
        }
        sysDisFeatRepository.deleteById(id);
    }

    @Override
    public List<SysDisFeat> getByUserId(Long userId) {
        log.info("Lấy danh sách phân quyền theo userId: {}", userId);
        return sysDisFeatRepository.findByUserId(userId);
    }

    @Override
    public boolean existsById(Long id) {
        return sysDisFeatRepository.existsById(id);
    }

    @Override
    public void deleteById(Long id) {
        try {
            deleteDisFeat(id);
        } catch (BusinessException e) {
            log.error("Lỗi khi xóa phân quyền người dùng với ID {}: {}", id, e.getMessage());
            throw new RuntimeException(e);
        }
    }
}









