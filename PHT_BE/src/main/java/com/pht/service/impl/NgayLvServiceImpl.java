package com.pht.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pht.entity.NgayLv;
import com.pht.exception.BusinessException;
import com.pht.model.request.NgayLvCreateRequest;
import com.pht.model.request.NgayLvUpdateRequest;
import com.pht.repository.NgayLvRepository;
import com.pht.service.NgayLvService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class NgayLvServiceImpl implements NgayLvService {

    @Autowired
    private NgayLvRepository ngayLvRepository;

    @Override
    public List<NgayLv> getAll() {
        log.info("Lấy danh sách tất cả ngày làm việc");
        return ngayLvRepository.findAll();
    }

    @Override
    public NgayLv getById(Long id) throws BusinessException {
        log.info("Lấy thông tin ngày làm việc với ID: {}", id);
        return ngayLvRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Không tìm thấy ngày làm việc với ID: " + id));
    }

    @Override
    public NgayLv create(NgayLvCreateRequest request) throws BusinessException {
        log.info("Tạo mới ngày làm việc: {}", request.getNgayLv());

        NgayLv newNgayLv = new NgayLv();
        newNgayLv.setNgayLv(request.getNgayLv());
        newNgayLv.setTrangThai(request.getTrangThai());
        newNgayLv.setCot(request.getCot());

        return ngayLvRepository.save(newNgayLv);
    }

    @Override
    public NgayLv update(NgayLvUpdateRequest request) throws BusinessException {
        log.info("Cập nhật ngày làm việc với ID: {}", request.getId());

        NgayLv existingNgayLv = getById(request.getId());

        existingNgayLv.setNgayLv(request.getNgayLv());
        existingNgayLv.setTrangThai(request.getTrangThai());
        existingNgayLv.setCot(request.getCot());

        return ngayLvRepository.save(existingNgayLv);
    }

    @Override
    public void deleteById(Long id) throws BusinessException {
        log.info("Xóa ngày làm việc với ID: {}", id);
        if (!ngayLvRepository.existsById(id)) {
            throw new BusinessException("Không tìm thấy ngày làm việc với ID: " + id);
        }
        ngayLvRepository.deleteById(id);
    }

    @Override
    public List<NgayLv> findByTrangThai(String trangThai) {
        log.info("Tìm ngày làm việc theo trạng thái: {}", trangThai);
        return ngayLvRepository.findByTrangThai(trangThai);
    }

    @Override
    public List<NgayLv> findByCot(String cot) {
        log.info("Tìm ngày làm việc theo cột: {}", cot);
        return ngayLvRepository.findByCot(cot);
    }
}









