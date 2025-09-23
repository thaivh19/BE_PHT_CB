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

import com.pht.entity.SphuongThucVanTai;
import com.pht.exception.BusinessException;
import com.pht.model.request.SphuongThucVanTaiCreateRequest;
import com.pht.model.request.SphuongThucVanTaiSearchRequest;
import com.pht.model.request.SphuongThucVanTaiUpdateRequest;
import com.pht.model.response.CatalogSearchResponse;
import com.pht.repository.SphuongThucVanTaiRepository;
import com.pht.service.SphuongThucVanTaiService;
import com.pht.utils.QueryUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class SphuongThucVanTaiServiceImpl extends BaseServiceImpl<SphuongThucVanTai, Long> implements SphuongThucVanTaiService {

    @Autowired
    private SphuongThucVanTaiRepository sphuongThucVanTaiRepository;

    @Override
    public List<SphuongThucVanTai> getAllPhuongThucVanTai() {
        log.info("Lấy danh sách tất cả phương thức vận tải");
        return sphuongThucVanTaiRepository.findAll();
    }

    @Override
    public CatalogSearchResponse<SphuongThucVanTai> getAllPhuongThucVanTaiWithPagination(int page, int size, String sortBy, String sortDir) {
        log.info("Lấy danh sách phương thức vận tải với phân trang: page={}, size={}, sortBy={}, sortDir={}", page, size, sortBy, sortDir);
        Sort sort = Sort.by("ASC".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<SphuongThucVanTai> pageResult = sphuongThucVanTaiRepository.findAll(pageable);
        return CatalogSearchResponse.<SphuongThucVanTai>builder()
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
    public SphuongThucVanTai getPhuongThucVanTaiById(Long id) throws BusinessException {
        log.info("Lấy phương thức vận tải theo ID: {}", id);
        Optional<SphuongThucVanTai> result = sphuongThucVanTaiRepository.findById(id);
        if (result.isEmpty()) {
            throw new BusinessException("Không tìm thấy phương thức vận tải với ID: " + id);
        }
        return result.get();
    }

    @Override
    public SphuongThucVanTai createPhuongThucVanTai(SphuongThucVanTaiCreateRequest request) throws BusinessException {
        log.info("Tạo mới phương thức vận tải: {}", request.getTenPtvt());
        
        // Kiểm tra trùng lặp mã phương thức vận tải
        if (sphuongThucVanTaiRepository.existsByMaPtvt(request.getMaPtvt())) {
            throw new BusinessException("Mã phương thức vận tải đã tồn tại: " + request.getMaPtvt());
        }
        
        // Tạo entity mới
        SphuongThucVanTai entity = new SphuongThucVanTai();
        entity.setMaPtvt(request.getMaPtvt());
        entity.setTenPtvt(request.getTenPtvt());
        entity.setTenPtvt1(request.getTenPtvt1());
        entity.setVnacss(request.getVnacss());
        entity.setDienGiai(request.getDienGiai());
        entity.setTrangThai(request.getTrangThai());
        
        // Set audit fields
        LocalDateTime now = LocalDateTime.now();
        entity.setNgayTao(now);
        entity.setNgayCapNhat(now);
        
        return sphuongThucVanTaiRepository.save(entity);
    }

    @Override
    public SphuongThucVanTai updatePhuongThucVanTai(SphuongThucVanTaiUpdateRequest request) throws BusinessException {
        log.info("Cập nhật phương thức vận tải với ID: {}", request.getId());
        
        SphuongThucVanTai existingEntity = getPhuongThucVanTaiById(request.getId());
        
        // Cập nhật các trường
        existingEntity.setMaPtvt(request.getMaPtvt());
        existingEntity.setTenPtvt(request.getTenPtvt());
        existingEntity.setTenPtvt1(request.getTenPtvt1());
        existingEntity.setVnacss(request.getVnacss());
        existingEntity.setDienGiai(request.getDienGiai());
        existingEntity.setTrangThai(request.getTrangThai());
        
        // Set audit fields for update
        existingEntity.setNgayCapNhat(LocalDateTime.now());
        
        return sphuongThucVanTaiRepository.save(existingEntity);
    }

    @Override
    public void deletePhuongThucVanTai(Long id) throws BusinessException {
        log.info("Xóa phương thức vận tải với ID: {}", id);
        if (!sphuongThucVanTaiRepository.existsById(id)) {
            throw new BusinessException("Không tìm thấy phương thức vận tải với ID: " + id);
        }
        sphuongThucVanTaiRepository.deleteById(id);
    }

    @Override
    public CatalogSearchResponse<SphuongThucVanTai> searchPhuongThucVanTai(SphuongThucVanTaiSearchRequest request) {
        long startTime = System.currentTimeMillis();
        log.info("Tìm kiếm phương thức vận tải với maPtvt: {}, tenPtvt: {}, trangThai: {}",
                request.getMaPtvt(), request.getTenPtvt(), request.getTrangThai());

        int pageNumber = 0;
        int pageSize = 10;
        Sort sort = Sort.by(Sort.Direction.ASC, "maPtvt");
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        String maPtvt = StringUtils.hasText(request.getMaPtvt()) ?
                QueryUtils.createLikeValue(request.getMaPtvt()) : null;
        String tenPtvt = StringUtils.hasText(request.getTenPtvt()) ?
                QueryUtils.createLikeValue(request.getTenPtvt()) : null;
        String trangThai = StringUtils.hasText(request.getTrangThai()) ?
                request.getTrangThai() : null;

        Page<SphuongThucVanTai> page = sphuongThucVanTaiRepository.findBySearchCriteria(maPtvt, tenPtvt, trangThai, pageable);

        long endTime = System.currentTimeMillis();

        return CatalogSearchResponse.<SphuongThucVanTai>builder()
                .content(page.getContent())
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .totalPages(page.getTotalPages())
                .numberOfElements(page.getNumberOfElements())
                .totalElements(page.getTotalElements())
                .searchKeyword(String.format("maPtvt=%s, tenPtvt=%s, trangThai=%s",
                        request.getMaPtvt(), request.getTenPtvt(), request.getTrangThai()))
                .searchTime(endTime - startTime)
                .build();
    }

    @Override
    public List<SphuongThucVanTai> exportPhuongThucVanTai(SphuongThucVanTaiSearchRequest request) {
        log.info("Xuất dữ liệu phương thức vận tải với maPtvt: {}, tenPtvt: {}, trangThai: {}",
                request.getMaPtvt(), request.getTenPtvt(), request.getTrangThai());

        String maPtvt = StringUtils.hasText(request.getMaPtvt()) ?
                QueryUtils.createLikeValue(request.getMaPtvt()) : null;
        String tenPtvt = StringUtils.hasText(request.getTenPtvt()) ?
                QueryUtils.createLikeValue(request.getTenPtvt()) : null;
        String trangThai = StringUtils.hasText(request.getTrangThai()) ?
                request.getTrangThai() : null;

        return sphuongThucVanTaiRepository.findBySearchCriteria(maPtvt, tenPtvt, trangThai);
    }
}
