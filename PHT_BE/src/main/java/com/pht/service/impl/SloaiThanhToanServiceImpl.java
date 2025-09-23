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

import com.pht.entity.SloaiThanhToan;
import com.pht.exception.BusinessException;
import com.pht.model.request.SloaiThanhToanCreateRequest;
import com.pht.model.request.SloaiThanhToanSearchRequest;
import com.pht.model.request.SloaiThanhToanUpdateRequest;
import com.pht.model.response.CatalogSearchResponse;
import com.pht.repository.SloaiThanhToanRepository;
import com.pht.service.SloaiThanhToanService;
import com.pht.utils.QueryUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class SloaiThanhToanServiceImpl extends BaseServiceImpl<SloaiThanhToan, Long> implements SloaiThanhToanService {

    @Autowired
    private SloaiThanhToanRepository sloaiThanhToanRepository;

    @Override
    public List<SloaiThanhToan> getAllLoaiThanhToan() {
        log.info("Lấy danh sách tất cả loại thanh toán");
        return sloaiThanhToanRepository.findAll();
    }

    @Override
    public CatalogSearchResponse<SloaiThanhToan> getAllLoaiThanhToanWithPagination(int page, int size, String sortBy, String sortDir) {
        log.info("Lấy danh sách loại thanh toán với phân trang: page={}, size={}, sortBy={}, sortDir={}", page, size, sortBy, sortDir);
        Sort sort = Sort.by("ASC".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<SloaiThanhToan> pageResult = sloaiThanhToanRepository.findAll(pageable);
        return CatalogSearchResponse.<SloaiThanhToan>builder()
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
    public SloaiThanhToan getLoaiThanhToanById(Long id) throws BusinessException {
        log.info("Lấy loại thanh toán theo ID: {}", id);
        Optional<SloaiThanhToan> result = sloaiThanhToanRepository.findById(id);
        if (result.isEmpty()) {
            throw new BusinessException("Không tìm thấy loại thanh toán với ID: " + id);
        }
        return result.get();
    }

    @Override
    public SloaiThanhToan createLoaiThanhToan(SloaiThanhToanCreateRequest request) throws BusinessException {
        log.info("Tạo mới loại thanh toán: {}", request.getTenLoaiThanhToan());
        
        // Kiểm tra trùng lặp mã loại thanh toán
        if (sloaiThanhToanRepository.existsByMaLoaiThanhToan(request.getMaLoaiThanhToan())) {
            throw new BusinessException("Mã loại thanh toán đã tồn tại: " + request.getMaLoaiThanhToan());
        }
        
        // Tạo entity mới
        SloaiThanhToan entity = new SloaiThanhToan();
        entity.setMaLoaiThanhToan(request.getMaLoaiThanhToan());
        entity.setTenLoaiThanhToan(request.getTenLoaiThanhToan());
        entity.setDienGiai(request.getDienGiai());
        entity.setTrangThai(request.getTrangThai());
        
        // Set audit fields
        LocalDateTime now = LocalDateTime.now();
        entity.setNgayTao(now);
        entity.setNgayCapNhat(now);
        
        return sloaiThanhToanRepository.save(entity);
    }

    @Override
    public SloaiThanhToan updateLoaiThanhToan(SloaiThanhToanUpdateRequest request) throws BusinessException {
        log.info("Cập nhật loại thanh toán với ID: {}", request.getId());
        
        SloaiThanhToan existingEntity = getLoaiThanhToanById(request.getId());
        
        // Cập nhật các trường
        existingEntity.setMaLoaiThanhToan(request.getMaLoaiThanhToan());
        existingEntity.setTenLoaiThanhToan(request.getTenLoaiThanhToan());
        existingEntity.setDienGiai(request.getDienGiai());
        existingEntity.setTrangThai(request.getTrangThai());
        
        // Set audit fields for update
        existingEntity.setNgayCapNhat(LocalDateTime.now());
        
        return sloaiThanhToanRepository.save(existingEntity);
    }

    @Override
    public void deleteLoaiThanhToan(Long id) throws BusinessException {
        log.info("Xóa loại thanh toán với ID: {}", id);
        if (!sloaiThanhToanRepository.existsById(id)) {
            throw new BusinessException("Không tìm thấy loại thanh toán với ID: " + id);
        }
        sloaiThanhToanRepository.deleteById(id);
    }

    @Override
    public CatalogSearchResponse<SloaiThanhToan> searchLoaiThanhToan(SloaiThanhToanSearchRequest request) {
        long startTime = System.currentTimeMillis();
        log.info("Tìm kiếm loại thanh toán với maLoaiThanhToan: {}, tenLoaiThanhToan: {}, trangThai: {}",
                request.getMaLoaiThanhToan(), request.getTenLoaiThanhToan(), request.getTrangThai());

        int pageNumber = 0;
        int pageSize = 10;
        Sort sort = Sort.by(Sort.Direction.ASC, "maLoaiThanhToan");
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        String maLoaiThanhToan = StringUtils.hasText(request.getMaLoaiThanhToan()) ?
                QueryUtils.createLikeValue(request.getMaLoaiThanhToan()) : null;
        String tenLoaiThanhToan = StringUtils.hasText(request.getTenLoaiThanhToan()) ?
                QueryUtils.createLikeValue(request.getTenLoaiThanhToan()) : null;
        String trangThai = StringUtils.hasText(request.getTrangThai()) ?
                request.getTrangThai() : null;

        Page<SloaiThanhToan> page = sloaiThanhToanRepository.findBySearchCriteria(maLoaiThanhToan, tenLoaiThanhToan, trangThai, pageable);

        long endTime = System.currentTimeMillis();

        return CatalogSearchResponse.<SloaiThanhToan>builder()
                .content(page.getContent())
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .totalPages(page.getTotalPages())
                .numberOfElements(page.getNumberOfElements())
                .totalElements(page.getTotalElements())
                .searchKeyword(String.format("maLoaiThanhToan=%s, tenLoaiThanhToan=%s, trangThai=%s",
                        request.getMaLoaiThanhToan(), request.getTenLoaiThanhToan(), request.getTrangThai()))
                .searchTime(endTime - startTime)
                .build();
    }

    @Override
    public List<SloaiThanhToan> exportLoaiThanhToan(SloaiThanhToanSearchRequest request) {
        log.info("Xuất dữ liệu loại thanh toán với maLoaiThanhToan: {}, tenLoaiThanhToan: {}, trangThai: {}",
                request.getMaLoaiThanhToan(), request.getTenLoaiThanhToan(), request.getTrangThai());

        String maLoaiThanhToan = StringUtils.hasText(request.getMaLoaiThanhToan()) ?
                QueryUtils.createLikeValue(request.getMaLoaiThanhToan()) : null;
        String tenLoaiThanhToan = StringUtils.hasText(request.getTenLoaiThanhToan()) ?
                QueryUtils.createLikeValue(request.getTenLoaiThanhToan()) : null;
        String trangThai = StringUtils.hasText(request.getTrangThai()) ?
                request.getTrangThai() : null;

        return sloaiThanhToanRepository.findBySearchCriteria(maLoaiThanhToan, tenLoaiThanhToan, trangThai);
    }
}
