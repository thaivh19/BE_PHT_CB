package com.pht.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pht.entity.SysFunc;
import com.pht.exception.BusinessException;
import com.pht.model.request.SysFuncCreateRequest;
import com.pht.model.request.SysFuncUpdateRequest;
import com.pht.repository.SysFuncRepository;
import com.pht.service.SysFuncService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class SysFuncServiceImpl extends BaseServiceImpl<SysFunc, Long> implements SysFuncService {

    @Autowired
    private SysFuncRepository sysFuncRepository;

    @Override
    public SysFunc getFuncById(Long id) throws BusinessException {
        log.info("Lấy thông tin chức năng với ID: {}", id);
        
        Optional<SysFunc> result = sysFuncRepository.findById(id);
        if (!result.isPresent()) {
            throw new BusinessException("Không tìm thấy chức năng với ID: " + id);
        }
        return result.get();
    }

    @Override
    public SysFunc createFunc(SysFuncCreateRequest request) throws BusinessException {
        log.info("Tạo mới chức năng: {}", request.getFuncName());
        
        // Tạo entity mới
        SysFunc entity = new SysFunc();
        entity.setFuncId(request.getFuncId());
        entity.setFuncName(request.getFuncName());
        
        return sysFuncRepository.save(entity);
    }

    @Override
    public SysFunc updateFunc(SysFuncUpdateRequest request) throws BusinessException {
        log.info("Cập nhật chức năng với ID: {}", request.getId());
        
        SysFunc existingEntity = getFuncById(request.getId());
        
        // Cập nhật các trường
        existingEntity.setFuncId(request.getFuncId());
        existingEntity.setFuncName(request.getFuncName());
        
        return sysFuncRepository.save(existingEntity);
    }

    @Override
    public void deleteFunc(Long id) throws BusinessException {
        log.info("Xóa chức năng với ID: {}", id);
        if (!sysFuncRepository.existsById(id)) {
            throw new BusinessException("Không tìm thấy chức năng với ID: " + id);
        }
        sysFuncRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return sysFuncRepository.existsById(id);
    }

    @Override
    public void deleteById(Long id) {
        try {
            deleteFunc(id);
        } catch (BusinessException e) {
            log.error("Lỗi khi xóa chức năng với ID {}: {}", id, e.getMessage());
            throw new RuntimeException(e);
        }
    }
}









