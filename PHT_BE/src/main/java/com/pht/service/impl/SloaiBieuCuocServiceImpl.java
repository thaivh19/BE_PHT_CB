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

import com.pht.entity.SloaiBieuCuoc;
import com.pht.exception.BusinessException;
import com.pht.model.request.SloaiBieuCuocCreateRequest;
import com.pht.model.request.SloaiBieuCuocSearchRequest;
import com.pht.model.request.SloaiBieuCuocUpdateRequest;
import com.pht.model.response.CatalogSearchResponse;
import com.pht.repository.SloaiBieuCuocRepository;
import com.pht.service.SloaiBieuCuocService;
import com.pht.utils.QueryUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class SloaiBieuCuocServiceImpl extends BaseServiceImpl<SloaiBieuCuoc, Long> implements SloaiBieuCuocService {

    @Autowired
    private SloaiBieuCuocRepository sloaiBieuCuocRepository;

    @Override
    public List<SloaiBieuCuoc> getAllLoaiBieuCuoc() {
        log.info("Lấy danh sách tất cả loại biểu cước");
        return sloaiBieuCuocRepository.findAll();
    }

    @Override
    public CatalogSearchResponse<SloaiBieuCuoc> getAllLoaiBieuCuocWithPagination(int page, int size, String sortBy, String sortDir) {
        log.info("Lấy danh sách loại biểu cước với phân trang: page={}, size={}, sortBy={}, sortDir={}", page, size, sortBy, sortDir);
        Sort sort = Sort.by("ASC".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<SloaiBieuCuoc> pageResult = sloaiBieuCuocRepository.findAll(pageable);
        return CatalogSearchResponse.<SloaiBieuCuoc>builder()
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
    public SloaiBieuCuoc getLoaiBieuCuocById(Long id) throws BusinessException {
        log.info("Lấy loại biểu cước theo ID: {}", id);
        Optional<SloaiBieuCuoc> result = sloaiBieuCuocRepository.findById(id);
        if (result.isEmpty()) {
            throw new BusinessException("Không tìm thấy loại biểu cước với ID: " + id);
        }
        return result.get();
    }

    @Override
    public SloaiBieuCuoc createLoaiBieuCuoc(SloaiBieuCuocCreateRequest request) throws BusinessException {
        log.info("Tạo mới loại biểu cước: {}", request.getTen());
        
        // Kiểm tra trùng lặp mã
        if (sloaiBieuCuocRepository.existsByMa(request.getMa())) {
            throw new BusinessException("Mã loại biểu cước đã tồn tại: " + request.getMa());
        }
        
        // Tạo entity mới
        SloaiBieuCuoc entity = new SloaiBieuCuoc();
        entity.setMa(request.getMa());
        entity.setTen(request.getTen());
        entity.setDienGiai(request.getDienGiai());
        entity.setTrangThai(request.getTrangThai());
        
        // Set audit fields
        LocalDateTime now = LocalDateTime.now();
        entity.setNgayTao(now);
        entity.setNgayCapNhat(now);
        
        return sloaiBieuCuocRepository.save(entity);
    }

    @Override
    public SloaiBieuCuoc updateLoaiBieuCuoc(SloaiBieuCuocUpdateRequest request) throws BusinessException {
        log.info("Cập nhật loại biểu cước với ID: {}", request.getId());
        
        SloaiBieuCuoc existingEntity = getLoaiBieuCuocById(request.getId());
        
        // Cập nhật các trường
        existingEntity.setMa(request.getMa());
        existingEntity.setTen(request.getTen());
        existingEntity.setDienGiai(request.getDienGiai());
        existingEntity.setTrangThai(request.getTrangThai());
        
        // Set audit fields for update
        existingEntity.setNgayCapNhat(LocalDateTime.now());
        
        return sloaiBieuCuocRepository.save(existingEntity);
    }

    @Override
    public void deleteLoaiBieuCuoc(Long id) throws BusinessException {
        log.info("Xóa loại biểu cước với ID: {}", id);
        if (!sloaiBieuCuocRepository.existsById(id)) {
            throw new BusinessException("Không tìm thấy loại biểu cước với ID: " + id);
        }
        sloaiBieuCuocRepository.deleteById(id);
    }

    @Override
    public CatalogSearchResponse<SloaiBieuCuoc> searchLoaiBieuCuoc(SloaiBieuCuocSearchRequest request) {
        long startTime = System.currentTimeMillis();
        log.info("Tìm kiếm loại biểu cước với ma: {}, ten: {}, trangThai: {}",
                request.getMa(), request.getTen(), request.getTrangThai());

        int pageNumber = 0;
        int pageSize = 10;
        Sort sort = Sort.by(Sort.Direction.ASC, "ma");
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        String ma = StringUtils.hasText(request.getMa()) ?
                QueryUtils.createLikeValue(request.getMa()) : null;
        String ten = StringUtils.hasText(request.getTen()) ?
                QueryUtils.createLikeValue(request.getTen()) : null;
        String trangThai = StringUtils.hasText(request.getTrangThai()) ?
                request.getTrangThai() : null;

        Page<SloaiBieuCuoc> page = sloaiBieuCuocRepository.findBySearchCriteria(ma, ten, trangThai, pageable);

        long endTime = System.currentTimeMillis();

        return CatalogSearchResponse.<SloaiBieuCuoc>builder()
                .content(page.getContent())
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .totalPages(page.getTotalPages())
                .numberOfElements(page.getNumberOfElements())
                .totalElements(page.getTotalElements())
                .searchKeyword(String.format("ma=%s, ten=%s, trangThai=%s",
                        request.getMa(), request.getTen(), request.getTrangThai()))
                .searchTime(endTime - startTime)
                .build();
    }

    @Override
    public List<SloaiBieuCuoc> exportLoaiBieuCuoc(SloaiBieuCuocSearchRequest request) {
        log.info("Xuất dữ liệu loại biểu cước với ma: {}, ten: {}, trangThai: {}",
                request.getMa(), request.getTen(), request.getTrangThai());

        String ma = StringUtils.hasText(request.getMa()) ?
                QueryUtils.createLikeValue(request.getMa()) : null;
        String ten = StringUtils.hasText(request.getTen()) ?
                QueryUtils.createLikeValue(request.getTen()) : null;
        String trangThai = StringUtils.hasText(request.getTrangThai()) ?
                request.getTrangThai() : null;

        return sloaiBieuCuocRepository.findBySearchCriteria(ma, ten, trangThai);
    }
}
