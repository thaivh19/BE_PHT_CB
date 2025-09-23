package com.pht.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.pht.entity.SdiaDiemLuuKho;
import com.pht.exception.BusinessException;
import com.pht.model.request.SdiaDiemLuuKhoCreateRequest;
import com.pht.model.request.SdiaDiemLuuKhoSearchRequest;
import com.pht.model.request.SdiaDiemLuuKhoUpdateRequest;
import com.pht.model.response.CatalogSearchResponse;
import com.pht.repository.SdiaDiemLuuKhoRepository;
import com.pht.service.SdiaDiemLuuKhoService;
import com.pht.utils.QueryUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class SdiaDiemLuuKhoServiceImpl extends BaseServiceImpl<SdiaDiemLuuKho, Long> implements SdiaDiemLuuKhoService {

    @Autowired
    private SdiaDiemLuuKhoRepository sdiaDiemLuuKhoRepository;

    @Override
    public List<SdiaDiemLuuKho> getAllDiaDiemLuuKho() {
        log.info("Lấy danh sách tất cả địa điểm lưu kho");
        return sdiaDiemLuuKhoRepository.findAll();
    }

    @Override
    public CatalogSearchResponse<SdiaDiemLuuKho> getAllDiaDiemLuuKhoWithPagination(int page, int size, String sortBy, String sortDir) {
        log.info("Lấy danh sách địa điểm lưu kho với phân trang: page={}, size={}, sortBy={}, sortDir={}", page, size, sortBy, sortDir);
        Sort sort = Sort.by("ASC".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<SdiaDiemLuuKho> pageResult = sdiaDiemLuuKhoRepository.findAll(pageable);
        return CatalogSearchResponse.<SdiaDiemLuuKho>builder()
                .content(pageResult.getContent())
                .pageNumber(page)
                .pageSize(size)
                .totalPages(pageResult.getTotalPages())
                .numberOfElements(pageResult.getNumberOfElements())
                .totalElements(pageResult.getTotalElements())
                .searchKeyword("All records")
                .searchTime(0)
                .build();
    }

    @Override
    public SdiaDiemLuuKho getDiaDiemLuuKhoById(Long id) throws BusinessException {
        log.info("Lấy địa điểm lưu kho theo ID: {}", id);
        Optional<SdiaDiemLuuKho> result = sdiaDiemLuuKhoRepository.findById(id);
        if (result.isEmpty()) {
            throw new BusinessException("Không tìm thấy địa điểm lưu kho với ID: " + id);
        }
        return result.get();
    }

    @Override
    public SdiaDiemLuuKho createDiaDiemLuuKho(SdiaDiemLuuKhoCreateRequest request) throws BusinessException {
        log.info("Tạo mới địa điểm lưu kho: {}", request.getTenDiaDiem());
        
        // Kiểm tra trùng lặp mã địa điểm lưu kho
        if (sdiaDiemLuuKhoRepository.existsByMaDiaDiemLuuKho(request.getMaDiaDiemLuuKho())) {
            throw new BusinessException("Mã địa điểm lưu kho đã tồn tại: " + request.getMaDiaDiemLuuKho());
        }
        
        // Tạo entity mới
        SdiaDiemLuuKho entity = new SdiaDiemLuuKho();
        entity.setMaDiaDiemLuuKho(request.getMaDiaDiemLuuKho());
        entity.setTenDiaDiem(request.getTenDiaDiem());
        entity.setTenDiaDiemTcVN(request.getTenDiaDiemTcVN());
        entity.setDiaDiem(request.getDiaDiem());
        entity.setDienGiai(request.getDienGiai());
        entity.setTrangThai(request.getTrangThai());
        entity.setLoai(request.getLoai());
        
        // Set audit fields
        LocalDateTime now = LocalDateTime.now();
        entity.setNgayTao(now);
        entity.setNgayCapNhat(now);
        
        return sdiaDiemLuuKhoRepository.save(entity);
    }

    @Override
    public SdiaDiemLuuKho updateDiaDiemLuuKho(SdiaDiemLuuKhoUpdateRequest request) throws BusinessException {
        log.info("Cập nhật địa điểm lưu kho với ID: {}", request.getId());
        
        SdiaDiemLuuKho existingEntity = getDiaDiemLuuKhoById(request.getId());
        
        // Cập nhật các trường
        existingEntity.setMaDiaDiemLuuKho(request.getMaDiaDiemLuuKho());
        existingEntity.setTenDiaDiem(request.getTenDiaDiem());
        existingEntity.setTenDiaDiemTcVN(request.getTenDiaDiemTcVN());
        existingEntity.setDiaDiem(request.getDiaDiem());
        existingEntity.setDienGiai(request.getDienGiai());
        existingEntity.setTrangThai(request.getTrangThai());
        existingEntity.setLoai(request.getLoai());
        
        // Set audit fields for update
        existingEntity.setNgayCapNhat(LocalDateTime.now());
        
        return sdiaDiemLuuKhoRepository.save(existingEntity);
    }

    @Override
    public void deleteDiaDiemLuuKho(Long id) throws BusinessException {
        log.info("Xóa địa điểm lưu kho với ID: {}", id);
        if (!sdiaDiemLuuKhoRepository.existsById(id)) {
            throw new BusinessException("Không tìm thấy địa điểm lưu kho với ID: " + id);
        }
        sdiaDiemLuuKhoRepository.deleteById(id);
    }

    @Override
    public CatalogSearchResponse<SdiaDiemLuuKho> searchDiaDiemLuuKho(SdiaDiemLuuKhoSearchRequest request) {
        long startTime = System.currentTimeMillis();
        log.info("Tìm kiếm địa điểm lưu kho với maDiaDiemLuuKho: {}, tenDiaDiem: {}, trangThai: {}",
                request.getMaDiaDiemLuuKho(), request.getTenDiaDiem(), request.getTrangThai());

        int pageNumber = 0;
        int pageSize = 10;
        Sort sort = Sort.by(Sort.Direction.ASC, "maDiaDiemLuuKho");
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        String maDiaDiemLuuKho = StringUtils.hasText(request.getMaDiaDiemLuuKho()) ?
                QueryUtils.createLikeValue(request.getMaDiaDiemLuuKho()) : null;
        String tenDiaDiem = StringUtils.hasText(request.getTenDiaDiem()) ?
                QueryUtils.createLikeValue(request.getTenDiaDiem()) : null;
        String trangThai = StringUtils.hasText(request.getTrangThai()) ?
                request.getTrangThai() : null;

        Page<SdiaDiemLuuKho> page = sdiaDiemLuuKhoRepository.findBySearchCriteria(maDiaDiemLuuKho, tenDiaDiem, trangThai, pageable);

        long endTime = System.currentTimeMillis();

        return CatalogSearchResponse.<SdiaDiemLuuKho>builder()
                .content(page.getContent())
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .totalPages(page.getTotalPages())
                .numberOfElements(page.getNumberOfElements())
                .totalElements(page.getTotalElements())
                .searchKeyword(String.format("maDiaDiemLuuKho=%s, tenDiaDiem=%s, trangThai=%s",
                        request.getMaDiaDiemLuuKho(), request.getTenDiaDiem(), request.getTrangThai()))
                .searchTime(endTime - startTime)
                .build();
    }

    @Override
    public List<SdiaDiemLuuKho> exportDiaDiemLuuKho(SdiaDiemLuuKhoSearchRequest request) {
        log.info("Xuất dữ liệu địa điểm lưu kho với maDiaDiemLuuKho: {}, tenDiaDiem: {}, trangThai: {}",
                request.getMaDiaDiemLuuKho(), request.getTenDiaDiem(), request.getTrangThai());

        String maDiaDiemLuuKho = StringUtils.hasText(request.getMaDiaDiemLuuKho()) ?
                QueryUtils.createLikeValue(request.getMaDiaDiemLuuKho()) : null;
        String tenDiaDiem = StringUtils.hasText(request.getTenDiaDiem()) ?
                QueryUtils.createLikeValue(request.getTenDiaDiem()) : null;
        String trangThai = StringUtils.hasText(request.getTrangThai()) ?
                request.getTrangThai() : null;

        return sdiaDiemLuuKhoRepository.findBySearchCriteria(maDiaDiemLuuKho, tenDiaDiem, trangThai);
    }
}
