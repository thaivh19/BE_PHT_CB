package com.pht.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pht.entity.SthamSo;
import com.pht.exception.BusinessException;
import com.pht.model.request.SthamSoCreateRequest;
import com.pht.model.request.SthamSoUpdateRequest;
import com.pht.repository.SthamSoRepository;
import com.pht.service.SthamSoService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class SthamSoServiceImpl implements SthamSoService {

    @Autowired
    private SthamSoRepository sthamSoRepository;

    @Override
    public List<SthamSo> getAll() {
        log.info("Lấy danh sách tất cả tham số hệ thống");
        return sthamSoRepository.findAll();
    }

    @Override
    public SthamSo getById(Long id) throws BusinessException {
        log.info("Lấy thông tin tham số với ID: {}", id);
        return sthamSoRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Không tìm thấy tham số với ID: " + id));
    }

    @Override
    public SthamSo create(SthamSoCreateRequest request) throws BusinessException {
        log.info("Tạo mới tham số: {}", request.getMaTs());

        // Kiểm tra mã tham số đã tồn tại chưa
        if (sthamSoRepository.findByMaTs(request.getMaTs()) != null) {
            throw new BusinessException("Mã tham số đã tồn tại: " + request.getMaTs());
        }

        SthamSo newSthamSo = new SthamSo();
        newSthamSo.setMaTs(request.getMaTs());
        newSthamSo.setTenTs(request.getTenTs());
        newSthamSo.setGiaTri(request.getGiaTri());

        return sthamSoRepository.save(newSthamSo);
    }

    @Override
    public SthamSo update(SthamSoUpdateRequest request) throws BusinessException {
        log.info("Cập nhật tham số với ID: {}", request.getId());

        SthamSo existingSthamSo = getById(request.getId());

        // Kiểm tra mã tham số đã tồn tại chưa (trừ chính nó)
        SthamSo existingByMaTs = sthamSoRepository.findByMaTs(request.getMaTs());
        if (existingByMaTs != null && !existingByMaTs.getId().equals(request.getId())) {
            throw new BusinessException("Mã tham số đã tồn tại: " + request.getMaTs());
        }

        existingSthamSo.setMaTs(request.getMaTs());
        existingSthamSo.setTenTs(request.getTenTs());
        existingSthamSo.setGiaTri(request.getGiaTri());

        return sthamSoRepository.save(existingSthamSo);
    }

    @Override
    public void deleteById(Long id) throws BusinessException {
        log.info("Xóa tham số với ID: {}", id);
        if (!sthamSoRepository.existsById(id)) {
            throw new BusinessException("Không tìm thấy tham số với ID: " + id);
        }
        sthamSoRepository.deleteById(id);
    }

    @Override
    public SthamSo findByMaTs(String maTs) {
        log.info("Tìm tham số theo mã: {}", maTs);
        return sthamSoRepository.findByMaTs(maTs);
    }
}









