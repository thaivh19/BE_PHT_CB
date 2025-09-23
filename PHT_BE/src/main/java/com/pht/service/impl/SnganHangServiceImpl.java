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

import com.pht.entity.SnganHang;
import com.pht.exception.BusinessException;
import com.pht.model.request.SnganHangCreateRequest;
import com.pht.model.request.SnganHangSearchRequest;
import com.pht.model.request.SnganHangUpdateRequest;
import com.pht.model.response.CatalogSearchResponse;
import com.pht.repository.SnganHangRepository;
import com.pht.service.SnganHangService;
import com.pht.utils.QueryUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class SnganHangServiceImpl extends BaseServiceImpl<SnganHang, Long> implements SnganHangService {

    @Autowired
    private SnganHangRepository snganHangRepository;

    @Override
    public List<SnganHang> getAllNganHang() {
        log.info("Lấy danh sách tất cả ngân hàng");
        return snganHangRepository.findAll();
    }

    @Override
    public CatalogSearchResponse<SnganHang> getAllNganHangWithPagination(int page, int size, String sortBy, String sortDir) {
        log.info("Lấy danh sách ngân hàng với phân trang: page={}, size={}, sortBy={}, sortDir={}", page, size, sortBy, sortDir);
        
        // Setup sorting
        Sort sort = Sort.by("ASC".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<SnganHang> pageResult = snganHangRepository.findAll(pageable);
        
        return CatalogSearchResponse.<SnganHang>builder()
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
    public SnganHang getNganHangById(Long id) throws BusinessException {
        log.info("Lấy ngân hàng theo ID: {}", id);
        Optional<SnganHang> result = snganHangRepository.findById(id);
        if (result.isEmpty()) {
            throw new BusinessException("Không tìm thấy ngân hàng với ID: " + id);
        }
        return result.get();
    }

    @Override
    public SnganHang createNganHang(SnganHangCreateRequest request) throws BusinessException {
        log.info("Tạo mới ngân hàng: {}", request.getTenNh());
        
        // Kiểm tra trùng lặp mã NH
        if (snganHangRepository.existsByMaNh(request.getMaNh())) {
            throw new BusinessException("Mã ngân hàng đã tồn tại: " + request.getMaNh());
        }
        
        // Tạo entity mới
        SnganHang entity = new SnganHang();
        entity.setMaNh(request.getMaNh());
        entity.setTenNh(request.getTenNh());
        entity.setDienGiai(request.getDienGiai());
        entity.setTrangThai(request.getTrangThai());
        
        // Set audit fields
        LocalDateTime now = LocalDateTime.now();
        entity.setNgayTao(now);
        entity.setNgayCapNhat(now);
        
        return snganHangRepository.save(entity);
    }

    @Override
    public SnganHang updateNganHang(SnganHangUpdateRequest request) throws BusinessException {
        log.info("Cập nhật ngân hàng với ID: {}", request.getId());
        
        SnganHang existingEntity = getNganHangById(request.getId());
        
        // Cập nhật các trường
        existingEntity.setMaNh(request.getMaNh());
        existingEntity.setTenNh(request.getTenNh());
        existingEntity.setDienGiai(request.getDienGiai());
        existingEntity.setTrangThai(request.getTrangThai());
        
        // Set audit fields for update
        existingEntity.setNgayCapNhat(LocalDateTime.now());
        
        return snganHangRepository.save(existingEntity);
    }

    @Override
    public void deleteNganHang(Long id) throws BusinessException {
        log.info("Xóa ngân hàng với ID: {}", id);
        
        if (!snganHangRepository.existsById(id)) {
            throw new BusinessException("Không tìm thấy ngân hàng với ID: " + id);
        }
        
        snganHangRepository.deleteById(id);
    }

    @Override
    public CatalogSearchResponse<SnganHang> searchNganHang(SnganHangSearchRequest request) {
        long startTime = System.currentTimeMillis();
        log.info("Tìm kiếm ngân hàng với maNh: {}, tenNh: {}, trangThai: {}", 
                request.getMaNh(), request.getTenNh(), request.getTrangThai());
        
        // Setup pagination - default values
        int pageNumber = 0;
        int pageSize = 10;
        
        // Setup sorting - default by maNh
        Sort sort = Sort.by(Sort.Direction.ASC, "maNh");
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        
        // Build search criteria
        String maNh = StringUtils.hasText(request.getMaNh()) ? 
                QueryUtils.createLikeValue(request.getMaNh()) : null;
        String tenNh = StringUtils.hasText(request.getTenNh()) ? 
                QueryUtils.createLikeValue(request.getTenNh()) : null;
        String trangThai = StringUtils.hasText(request.getTrangThai()) ? 
                request.getTrangThai() : null;
        
        Page<SnganHang> page = snganHangRepository.findBySearchCriteria(maNh, tenNh, trangThai, pageable);
        
        long endTime = System.currentTimeMillis();
        
        return CatalogSearchResponse.<SnganHang>builder()
                .content(page.getContent())
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .totalPages(page.getTotalPages())
                .numberOfElements(page.getNumberOfElements())
                .totalElements(page.getTotalElements())
                .searchKeyword(String.format("maNh=%s, tenNh=%s, trangThai=%s", 
                        request.getMaNh(), request.getTenNh(), request.getTrangThai()))
                .searchTime(endTime - startTime)
                .build();
    }

    @Override
    public List<SnganHang> exportNganHang(SnganHangSearchRequest request) {
        log.info("Xuất dữ liệu ngân hàng với maNh: {}, tenNh: {}, trangThai: {}", 
                request.getMaNh(), request.getTenNh(), request.getTrangThai());
        
        // Build search criteria
        String maNh = StringUtils.hasText(request.getMaNh()) ? 
                QueryUtils.createLikeValue(request.getMaNh()) : null;
        String tenNh = StringUtils.hasText(request.getTenNh()) ? 
                QueryUtils.createLikeValue(request.getTenNh()) : null;
        String trangThai = StringUtils.hasText(request.getTrangThai()) ? 
                request.getTrangThai() : null;
        
        return snganHangRepository.findBySearchCriteria(maNh, tenNh, trangThai);
    }
}
