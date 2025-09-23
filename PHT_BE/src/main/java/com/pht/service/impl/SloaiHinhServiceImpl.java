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

import com.pht.entity.SloaiHinh;
import com.pht.exception.BusinessException;
import com.pht.model.request.SloaiHinhCreateRequest;
import com.pht.model.request.SloaiHinhSearchRequest;
import com.pht.model.request.SloaiHinhUpdateRequest;
import com.pht.model.response.CatalogSearchResponse;
import com.pht.repository.SloaiHinhRepository;
import com.pht.service.SloaiHinhService;
import com.pht.utils.QueryUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class SloaiHinhServiceImpl extends BaseServiceImpl<SloaiHinh, Long> implements SloaiHinhService {

    @Autowired
    private SloaiHinhRepository sloaiHinhRepository;

    @Override
    public List<SloaiHinh> getAllLoaiHinh() {
        log.info("Lấy danh sách tất cả loại hình");
        return sloaiHinhRepository.findAll();
    }

    @Override
    public CatalogSearchResponse<SloaiHinh> getAllLoaiHinhWithPagination(int page, int size, String sortBy, String sortDir) {
        log.info("Lấy danh sách loại hình với phân trang: page={}, size={}, sortBy={}, sortDir={}", page, size, sortBy, sortDir);
        Sort sort = Sort.by("ASC".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<SloaiHinh> pageResult = sloaiHinhRepository.findAll(pageable);
        return CatalogSearchResponse.<SloaiHinh>builder()
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
    public SloaiHinh getLoaiHinhById(Long id) throws BusinessException {
        log.info("Lấy loại hình theo ID: {}", id);
        Optional<SloaiHinh> result = sloaiHinhRepository.findById(id);
        if (result.isEmpty()) {
            throw new BusinessException("Không tìm thấy loại hình với ID: " + id);
        }
        return result.get();
    }

    @Override
    public SloaiHinh createLoaiHinh(SloaiHinhCreateRequest request) throws BusinessException {
        log.info("Tạo mới loại hình: {}", request.getTenLoaiHinh());
        
        // Kiểm tra trùng lặp mã loại hình
        if (sloaiHinhRepository.existsByMaLoaiHinh(request.getMaLoaiHinh())) {
            throw new BusinessException("Mã loại hình đã tồn tại: " + request.getMaLoaiHinh());
        }
        
        // Tạo entity mới
        SloaiHinh entity = new SloaiHinh();
        entity.setNhomLoaiHinh(request.getNhomLoaiHinh());
        entity.setMaLoaiHinh(request.getMaLoaiHinh());
        entity.setTenLoaiHinh(request.getTenLoaiHinh());
        entity.setDienGiai(request.getDienGiai());
        entity.setTrangThai(request.getTrangThai());
        
        // Set audit fields
        LocalDateTime now = LocalDateTime.now();
        entity.setNgayTao(now);
        entity.setNgayCapNhat(now);
        
        return sloaiHinhRepository.save(entity);
    }

    @Override
    public SloaiHinh updateLoaiHinh(SloaiHinhUpdateRequest request) throws BusinessException {
        log.info("Cập nhật loại hình với ID: {}", request.getId());
        
        SloaiHinh existingEntity = getLoaiHinhById(request.getId());
        
        // Cập nhật các trường
        existingEntity.setNhomLoaiHinh(request.getNhomLoaiHinh());
        existingEntity.setMaLoaiHinh(request.getMaLoaiHinh());
        existingEntity.setTenLoaiHinh(request.getTenLoaiHinh());
        existingEntity.setDienGiai(request.getDienGiai());
        existingEntity.setTrangThai(request.getTrangThai());
        
        // Set audit fields for update
        existingEntity.setNgayCapNhat(LocalDateTime.now());
        
        return sloaiHinhRepository.save(existingEntity);
    }

    @Override
    public void deleteLoaiHinh(Long id) throws BusinessException {
        log.info("Xóa loại hình với ID: {}", id);
        if (!sloaiHinhRepository.existsById(id)) {
            throw new BusinessException("Không tìm thấy loại hình với ID: " + id);
        }
        sloaiHinhRepository.deleteById(id);
    }

    @Override
    public CatalogSearchResponse<SloaiHinh> searchLoaiHinh(SloaiHinhSearchRequest request) {
        long startTime = System.currentTimeMillis();
        log.info("Tìm kiếm loại hình với nhomLoaiHinh: {}, maLoaiHinh: {}, tenLoaiHinh: {}, trangThai: {}",
                request.getNhomLoaiHinh(), request.getMaLoaiHinh(), request.getTenLoaiHinh(), request.getTrangThai());

        int pageNumber = 0;
        int pageSize = 10;
        Sort sort = Sort.by(Sort.Direction.ASC, "maLoaiHinh");
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        String nhomLoaiHinh = StringUtils.hasText(request.getNhomLoaiHinh()) ?
                QueryUtils.createLikeValue(request.getNhomLoaiHinh()) : null;
        String maLoaiHinh = StringUtils.hasText(request.getMaLoaiHinh()) ?
                QueryUtils.createLikeValue(request.getMaLoaiHinh()) : null;
        String tenLoaiHinh = StringUtils.hasText(request.getTenLoaiHinh()) ?
                QueryUtils.createLikeValue(request.getTenLoaiHinh()) : null;
        String trangThai = StringUtils.hasText(request.getTrangThai()) ?
                request.getTrangThai() : null;

        Page<SloaiHinh> page = sloaiHinhRepository.findBySearchCriteria(nhomLoaiHinh, maLoaiHinh, tenLoaiHinh, trangThai, pageable);

        long endTime = System.currentTimeMillis();

        return CatalogSearchResponse.<SloaiHinh>builder()
                .content(page.getContent())
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .totalPages(page.getTotalPages())
                .numberOfElements(page.getNumberOfElements())
                .totalElements(page.getTotalElements())
                .searchKeyword(String.format("nhomLoaiHinh=%s, maLoaiHinh=%s, tenLoaiHinh=%s, trangThai=%s",
                        request.getNhomLoaiHinh(), request.getMaLoaiHinh(), request.getTenLoaiHinh(), request.getTrangThai()))
                .searchTime(endTime - startTime)
                .build();
    }

    @Override
    public List<SloaiHinh> exportLoaiHinh(SloaiHinhSearchRequest request) {
        log.info("Xuất dữ liệu loại hình với nhomLoaiHinh: {}, maLoaiHinh: {}, tenLoaiHinh: {}, trangThai: {}",
                request.getNhomLoaiHinh(), request.getMaLoaiHinh(), request.getTenLoaiHinh(), request.getTrangThai());

        String nhomLoaiHinh = StringUtils.hasText(request.getNhomLoaiHinh()) ?
                QueryUtils.createLikeValue(request.getNhomLoaiHinh()) : null;
        String maLoaiHinh = StringUtils.hasText(request.getMaLoaiHinh()) ?
                QueryUtils.createLikeValue(request.getMaLoaiHinh()) : null;
        String tenLoaiHinh = StringUtils.hasText(request.getTenLoaiHinh()) ?
                QueryUtils.createLikeValue(request.getTenLoaiHinh()) : null;
        String trangThai = StringUtils.hasText(request.getTrangThai()) ?
                request.getTrangThai() : null;

        return sloaiHinhRepository.findBySearchCriteria(nhomLoaiHinh, maLoaiHinh, tenLoaiHinh, trangThai);
    }
}
