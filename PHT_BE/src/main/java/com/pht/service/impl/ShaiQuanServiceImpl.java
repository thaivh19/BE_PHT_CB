package com.pht.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.pht.entity.ShaiQuan;
import com.pht.exception.BusinessException;
import com.pht.model.request.ShaiQuanCreateRequest;
import com.pht.model.request.ShaiQuanSearchRequest;
import com.pht.model.request.ShaiQuanUpdateRequest;
import com.pht.model.response.CatalogSearchResponse;
import com.pht.repository.ShaiQuanRepository;
import com.pht.service.ShaiQuanService;
import com.pht.utils.QueryUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class ShaiQuanServiceImpl extends BaseServiceImpl<ShaiQuan, Long> implements ShaiQuanService {

    @Autowired
    private ShaiQuanRepository shaiQuanRepository;

    @Override
    public List<ShaiQuan> getAllHaiQuan() {
        log.info("Lấy danh sách tất cả hải quan");
        return shaiQuanRepository.findAll();
    }

    @Override
    public CatalogSearchResponse<ShaiQuan> getAllHaiQuanWithPagination(int page, int size, String sortBy, String sortDir) {
        log.info("Lấy danh sách hải quan với phân trang: page={}, size={}, sortBy={}, sortDir={}", page, size, sortBy, sortDir);
        
        // Setup sorting
        Sort sort = Sort.by("ASC".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<ShaiQuan> pageResult = shaiQuanRepository.findAll(pageable);
        
        return CatalogSearchResponse.<ShaiQuan>builder()
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
    public ShaiQuan getHaiQuanById(Long id) throws BusinessException {
        log.info("Lấy hải quan theo ID: {}", id);
        Optional<ShaiQuan> result = shaiQuanRepository.findById(id);
        if (result.isEmpty()) {
            throw new BusinessException("Không tìm thấy hải quan với ID: " + id);
        }
        return result.get();
    }

    @Override
    public ShaiQuan createHaiQuan(ShaiQuanCreateRequest request) throws BusinessException {
        log.info("Tạo mới hải quan: {}", request.getTenHq());
        
        // Kiểm tra trùng lặp mã HQ
        if (shaiQuanRepository.existsByMaHq(request.getMaHq())) {
            throw new BusinessException("Mã hải quan đã tồn tại: " + request.getMaHq());
        }
        
        // Tạo entity mới
        ShaiQuan entity = new ShaiQuan();
        entity.setMaHq(request.getMaHq());
        entity.setTenHq(request.getTenHq());
        entity.setDienGiai(request.getDienGiai());
        entity.setTrangThai(request.getTrangThai());
        
        // Set audit fields
        LocalDateTime now = LocalDateTime.now();
        entity.setNgayTao(now);
        entity.setNgayCapNhat(now);
        
        return shaiQuanRepository.save(entity);
    }

    @Override
    public ShaiQuan updateHaiQuan(ShaiQuanUpdateRequest request) throws BusinessException {
        log.info("Cập nhật hải quan với ID: {}", request.getId());
        
        ShaiQuan existingEntity = getHaiQuanById(request.getId());
        
        // Cập nhật các trường
        existingEntity.setMaHq(request.getMaHq());
        existingEntity.setTenHq(request.getTenHq());
        existingEntity.setDienGiai(request.getDienGiai());
        existingEntity.setTrangThai(request.getTrangThai());
        
        // Set audit fields for update
        existingEntity.setNgayCapNhat(LocalDateTime.now());
        
        return shaiQuanRepository.save(existingEntity);
    }

    @Override
    public void deleteHaiQuan(Long id) throws BusinessException {
        log.info("Xóa hải quan với ID: {}", id);
        
        if (!shaiQuanRepository.existsById(id)) {
            throw new BusinessException("Không tìm thấy hải quan với ID: " + id);
        }
        
        shaiQuanRepository.deleteById(id);
    }


    @Override
    public CatalogSearchResponse<ShaiQuan> searchHaiQuan(ShaiQuanSearchRequest request) {
        long startTime = System.currentTimeMillis();
        log.info("Tìm kiếm hải quan với maHq: {}, tenHq: {}, trangThai: {}", 
                request.getMaHq(), request.getTenHq(), request.getTrangThai());
        
        // Setup pagination - default values
        int pageNumber = 0;
        int pageSize = 10;
        
        // Setup sorting - default by maHq
        Sort sort = Sort.by(Sort.Direction.ASC, "maHq");
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        
        // Build search criteria
        String maHq = StringUtils.hasText(request.getMaHq()) ? 
                QueryUtils.createLikeValue(request.getMaHq()) : null;
        String tenHq = StringUtils.hasText(request.getTenHq()) ? 
                QueryUtils.createLikeValue(request.getTenHq()) : null;
        String trangThai = StringUtils.hasText(request.getTrangThai()) ? 
                request.getTrangThai() : null;
        
        Page<ShaiQuan> page = shaiQuanRepository.findBySearchCriteria(maHq, tenHq, trangThai, pageable);
        
        long endTime = System.currentTimeMillis();
        
        return CatalogSearchResponse.<ShaiQuan>builder()
                .content(page.getContent())
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .totalPages(page.getTotalPages())
                .numberOfElements(page.getNumberOfElements())
                .totalElements(page.getTotalElements())
                .searchKeyword(String.format("maHq=%s, tenHq=%s, trangThai=%s", 
                        request.getMaHq(), request.getTenHq(), request.getTrangThai()))
                .searchTime(endTime - startTime)
                .build();
    }



    @Override
    public List<ShaiQuan> exportHaiQuan(ShaiQuanSearchRequest request) {
        log.info("Xuất dữ liệu hải quan với maHq: {}, tenHq: {}, trangThai: {}", 
                request.getMaHq(), request.getTenHq(), request.getTrangThai());
        
        // Build search criteria
        String maHq = StringUtils.hasText(request.getMaHq()) ? 
                QueryUtils.createLikeValue(request.getMaHq()) : null;
        String tenHq = StringUtils.hasText(request.getTenHq()) ? 
                QueryUtils.createLikeValue(request.getTenHq()) : null;
        String trangThai = StringUtils.hasText(request.getTrangThai()) ? 
                request.getTrangThai() : null;
        
        return shaiQuanRepository.findBySearchCriteria(maHq, tenHq, trangThai);
    }
}
