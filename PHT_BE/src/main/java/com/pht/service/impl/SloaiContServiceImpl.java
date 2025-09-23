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

import com.pht.entity.SloaiCont;
import com.pht.exception.BusinessException;
import com.pht.model.request.SloaiContCreateRequest;
import com.pht.model.request.SloaiContSearchRequest;
import com.pht.model.request.SloaiContUpdateRequest;
import com.pht.model.response.CatalogSearchResponse;
import com.pht.repository.SloaiContRepository;
import com.pht.service.SloaiContService;
import com.pht.utils.QueryUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class SloaiContServiceImpl extends BaseServiceImpl<SloaiCont, Long> implements SloaiContService {

    @Autowired
    private SloaiContRepository sloaiContRepository;

    @Override
    public List<SloaiCont> getAllLoaiCont() {
        log.info("Lấy danh sách tất cả loại container");
        return sloaiContRepository.findAll();
    }

    @Override
    public CatalogSearchResponse<SloaiCont> getAllLoaiContWithPagination(int page, int size, String sortBy, String sortDir) {
        log.info("Lấy danh sách loại container với phân trang: page={}, size={}, sortBy={}, sortDir={}", page, size, sortBy, sortDir);
        Sort sort = Sort.by("ASC".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<SloaiCont> pageResult = sloaiContRepository.findAll(pageable);
        return CatalogSearchResponse.<SloaiCont>builder()
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
    public SloaiCont getLoaiContById(Long id) throws BusinessException {
        log.info("Lấy loại container theo ID: {}", id);
        Optional<SloaiCont> result = sloaiContRepository.findById(id);
        if (result.isEmpty()) {
            throw new BusinessException("Không tìm thấy loại container với ID: " + id);
        }
        return result.get();
    }

    @Override
    public SloaiCont createLoaiCont(SloaiContCreateRequest request) throws BusinessException {
        log.info("Tạo mới loại container: {}", request.getTen());
        
        // Kiểm tra trùng lặp mã
        if (sloaiContRepository.existsByMa(request.getMa())) {
            throw new BusinessException("Mã loại container đã tồn tại: " + request.getMa());
        }
        
        // Tạo entity mới
        SloaiCont entity = new SloaiCont();
        entity.setMa(request.getMa());
        entity.setTen(request.getTen());
        entity.setDienGiai(request.getDienGiai());
        entity.setTrangThai(request.getTrangThai());
        
        // Set audit fields
        LocalDateTime now = LocalDateTime.now();
        entity.setNgayTao(now);
        entity.setNgayCapNhat(now);
        
        return sloaiContRepository.save(entity);
    }

    @Override
    public SloaiCont updateLoaiCont(SloaiContUpdateRequest request) throws BusinessException {
        log.info("Cập nhật loại container với ID: {}", request.getId());
        
        SloaiCont existingEntity = getLoaiContById(request.getId());
        
        // Cập nhật các trường
        existingEntity.setMa(request.getMa());
        existingEntity.setTen(request.getTen());
        existingEntity.setDienGiai(request.getDienGiai());
        existingEntity.setTrangThai(request.getTrangThai());
        
        // Set audit fields for update
        existingEntity.setNgayCapNhat(LocalDateTime.now());
        
        return sloaiContRepository.save(existingEntity);
    }

    @Override
    public void deleteLoaiCont(Long id) throws BusinessException {
        log.info("Xóa loại container với ID: {}", id);
        if (!sloaiContRepository.existsById(id)) {
            throw new BusinessException("Không tìm thấy loại container với ID: " + id);
        }
        sloaiContRepository.deleteById(id);
    }

    @Override
    public CatalogSearchResponse<SloaiCont> searchLoaiCont(SloaiContSearchRequest request) {
        long startTime = System.currentTimeMillis();
        log.info("Tìm kiếm loại container với ma: {}, ten: {}, trangThai: {}",
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

        Page<SloaiCont> page = sloaiContRepository.findBySearchCriteria(ma, ten, trangThai, pageable);

        long endTime = System.currentTimeMillis();

        return CatalogSearchResponse.<SloaiCont>builder()
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
    public List<SloaiCont> exportLoaiCont(SloaiContSearchRequest request) {
        log.info("Xuất dữ liệu loại container với ma: {}, ten: {}, trangThai: {}",
                request.getMa(), request.getTen(), request.getTrangThai());

        String ma = StringUtils.hasText(request.getMa()) ?
                QueryUtils.createLikeValue(request.getMa()) : null;
        String ten = StringUtils.hasText(request.getTen()) ?
                QueryUtils.createLikeValue(request.getTen()) : null;
        String trangThai = StringUtils.hasText(request.getTrangThai()) ?
                request.getTrangThai() : null;

        return sloaiContRepository.findBySearchCriteria(ma, ten, trangThai);
    }
}
