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

import com.pht.entity.SdoanhNghiep;
import com.pht.exception.BusinessException;
import com.pht.model.request.SdoanhNghiepCreateRequest;
import com.pht.model.request.SdoanhNghiepSearchRequest;
import com.pht.model.request.SdoanhNghiepUpdateRequest;
import com.pht.model.response.CatalogSearchResponse;
import com.pht.repository.SdoanhNghiepRepository;
import com.pht.service.SdoanhNghiepService;
import com.pht.utils.QueryUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class SdoanhNghiepServiceImpl extends BaseServiceImpl<SdoanhNghiep, Long> implements SdoanhNghiepService {

    @Autowired
    private SdoanhNghiepRepository sdoanhNghiepRepository;

    @Override
    public List<SdoanhNghiep> getAllDoanhNghiep() {
        log.info("Lấy danh sách tất cả doanh nghiệp");
        return sdoanhNghiepRepository.findAll();
    }

    @Override
    public CatalogSearchResponse<SdoanhNghiep> getAllDoanhNghiepWithPagination(int page, int size, String sortBy, String sortDir) {
        log.info("Lấy danh sách doanh nghiệp với phân trang: page={}, size={}, sortBy={}, sortDir={}", page, size, sortBy, sortDir);
        Sort sort = Sort.by("ASC".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<SdoanhNghiep> pageResult = sdoanhNghiepRepository.findAll(pageable);
        return CatalogSearchResponse.<SdoanhNghiep>builder()
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
    public SdoanhNghiep getDoanhNghiepById(Long id) throws BusinessException {
        log.info("Lấy doanh nghiệp theo ID: {}", id);
        Optional<SdoanhNghiep> result = sdoanhNghiepRepository.findById(id);
        if (result.isEmpty()) {
            throw new BusinessException("Không tìm thấy doanh nghiệp với ID: " + id);
        }
        return result.get();
    }

    @Override
    public SdoanhNghiep createDoanhNghiep(SdoanhNghiepCreateRequest request) throws BusinessException {
        log.info("Tạo mới doanh nghiệp: {}", request.getTenDn());
        
        // Kiểm tra trùng lặp mã doanh nghiệp
        if (sdoanhNghiepRepository.existsByMaDn(request.getMaDn())) {
            throw new BusinessException("Mã doanh nghiệp đã tồn tại: " + request.getMaDn());
        }
        
        // Tạo entity mới
        SdoanhNghiep entity = new SdoanhNghiep();
        entity.setMaDn(request.getMaDn());
        entity.setTenDn(request.getTenDn());
        entity.setDienGiai(request.getDienGiai());
        entity.setTrangThai(request.getTrangThai());
        
        // Set audit fields
        LocalDateTime now = LocalDateTime.now();
        entity.setNgayTao(now);
        entity.setNgayCapNhat(now);
        
        return sdoanhNghiepRepository.save(entity);
    }

    @Override
    public SdoanhNghiep updateDoanhNghiep(SdoanhNghiepUpdateRequest request) throws BusinessException {
        log.info("Cập nhật doanh nghiệp với ID: {}", request.getId());
        
        SdoanhNghiep existingEntity = getDoanhNghiepById(request.getId());
        
        // Cập nhật các trường
        existingEntity.setMaDn(request.getMaDn());
        existingEntity.setTenDn(request.getTenDn());
        existingEntity.setDienGiai(request.getDienGiai());
        existingEntity.setTrangThai(request.getTrangThai());
        
        // Set audit fields for update
        existingEntity.setNgayCapNhat(LocalDateTime.now());
        
        return sdoanhNghiepRepository.save(existingEntity);
    }

    @Override
    public void deleteDoanhNghiep(Long id) throws BusinessException {
        log.info("Xóa doanh nghiệp với ID: {}", id);
        if (!sdoanhNghiepRepository.existsById(id)) {
            throw new BusinessException("Không tìm thấy doanh nghiệp với ID: " + id);
        }
        sdoanhNghiepRepository.deleteById(id);
    }

    @Override
    public CatalogSearchResponse<SdoanhNghiep> searchDoanhNghiep(SdoanhNghiepSearchRequest request) {
        long startTime = System.currentTimeMillis();
        log.info("Tìm kiếm doanh nghiệp với maDn: {}, tenDn: {}, trangThai: {}",
                request.getMaDn(), request.getTenDn(), request.getTrangThai());

        int pageNumber = 0;
        int pageSize = 10;
        Sort sort = Sort.by(Sort.Direction.ASC, "maDn");
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        String maDn = StringUtils.hasText(request.getMaDn()) ?
                QueryUtils.createLikeValue(request.getMaDn()) : null;
        String tenDn = StringUtils.hasText(request.getTenDn()) ?
                QueryUtils.createLikeValue(request.getTenDn()) : null;
        String trangThai = StringUtils.hasText(request.getTrangThai()) ?
                request.getTrangThai() : null;

        Page<SdoanhNghiep> page = sdoanhNghiepRepository.findBySearchCriteria(maDn, tenDn, trangThai, pageable);

        long endTime = System.currentTimeMillis();

        return CatalogSearchResponse.<SdoanhNghiep>builder()
                .content(page.getContent())
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .totalPages(page.getTotalPages())
                .numberOfElements(page.getNumberOfElements())
                .totalElements(page.getTotalElements())
                .searchKeyword(String.format("maDn=%s, tenDn=%s, trangThai=%s",
                        request.getMaDn(), request.getTenDn(), request.getTrangThai()))
                .searchTime(endTime - startTime)
                .build();
    }

    @Override
    public List<SdoanhNghiep> exportDoanhNghiep(SdoanhNghiepSearchRequest request) {
        log.info("Xuất dữ liệu doanh nghiệp với maDn: {}, tenDn: {}, trangThai: {}",
                request.getMaDn(), request.getTenDn(), request.getTrangThai());

        String maDn = StringUtils.hasText(request.getMaDn()) ?
                QueryUtils.createLikeValue(request.getMaDn()) : null;
        String tenDn = StringUtils.hasText(request.getTenDn()) ?
                QueryUtils.createLikeValue(request.getTenDn()) : null;
        String trangThai = StringUtils.hasText(request.getTrangThai()) ?
                request.getTrangThai() : null;

        return sdoanhNghiepRepository.findBySearchCriteria(maDn, tenDn, trangThai);
    }
}
