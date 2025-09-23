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

import com.pht.entity.Sdvt;
import com.pht.exception.BusinessException;
import com.pht.model.request.SdvtCreateRequest;
import com.pht.model.request.SdvtSearchRequest;
import com.pht.model.request.SdvtUpdateRequest;
import com.pht.model.response.CatalogSearchResponse;
import com.pht.repository.SdvtRepository;
import com.pht.service.SdvtService;
import com.pht.utils.QueryUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class SdvtServiceImpl extends BaseServiceImpl<Sdvt, Long> implements SdvtService {

    @Autowired
    private SdvtRepository sdvtRepository;

    @Override
    public List<Sdvt> getAllDvt() {
        log.info("Lấy danh sách tất cả đơn vị tính");
        return sdvtRepository.findAll();
    }

    @Override
    public CatalogSearchResponse<Sdvt> getAllDvtWithPagination(int page, int size, String sortBy, String sortDir) {
        log.info("Lấy danh sách đơn vị tính với phân trang: page={}, size={}, sortBy={}, sortDir={}", page, size, sortBy, sortDir);
        Sort sort = Sort.by("ASC".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Sdvt> pageResult = sdvtRepository.findAll(pageable);
        return CatalogSearchResponse.<Sdvt>builder()
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
    public Sdvt getDvtById(Long id) throws BusinessException {
        log.info("Lấy đơn vị tính theo ID: {}", id);
        Optional<Sdvt> result = sdvtRepository.findById(id);
        if (result.isEmpty()) {
            throw new BusinessException("Không tìm thấy đơn vị tính với ID: " + id);
        }
        return result.get();
    }

    @Override
    public Sdvt createDvt(SdvtCreateRequest request) throws BusinessException {
        log.info("Tạo mới đơn vị tính: {}", request.getTenDvt());
        
        // Kiểm tra trùng lặp mã đơn vị tính
        if (sdvtRepository.existsByMaDvt(request.getMaDvt())) {
            throw new BusinessException("Mã đơn vị tính đã tồn tại: " + request.getMaDvt());
        }
        
        // Tạo entity mới
        Sdvt entity = new Sdvt();
        entity.setMaDvt(request.getMaDvt());
        entity.setTenDvt(request.getTenDvt());
        entity.setLoaiDvt(request.getLoaiDvt());
        entity.setDienGiai(request.getDienGiai());
        entity.setTrangThai(request.getTrangThai());
        entity.setCvTon(request.getCvTon());
        
        // Set audit fields
        LocalDateTime now = LocalDateTime.now();
        entity.setNgayTao(now);
        entity.setNgayCapNhat(now);
        
        return sdvtRepository.save(entity);
    }

    @Override
    public Sdvt updateDvt(SdvtUpdateRequest request) throws BusinessException {
        log.info("Cập nhật đơn vị tính với ID: {}", request.getId());
        
        Sdvt existingEntity = getDvtById(request.getId());
        
        // Cập nhật các trường
        existingEntity.setMaDvt(request.getMaDvt());
        existingEntity.setTenDvt(request.getTenDvt());
        existingEntity.setLoaiDvt(request.getLoaiDvt());
        existingEntity.setDienGiai(request.getDienGiai());
        existingEntity.setTrangThai(request.getTrangThai());
        existingEntity.setCvTon(request.getCvTon());
        
        // Set audit fields for update
        existingEntity.setNgayCapNhat(LocalDateTime.now());
        
        return sdvtRepository.save(existingEntity);
    }

    @Override
    public void deleteDvt(Long id) throws BusinessException {
        log.info("Xóa đơn vị tính với ID: {}", id);
        if (!sdvtRepository.existsById(id)) {
            throw new BusinessException("Không tìm thấy đơn vị tính với ID: " + id);
        }
        sdvtRepository.deleteById(id);
    }

    @Override
    public CatalogSearchResponse<Sdvt> searchDvt(SdvtSearchRequest request) {
        long startTime = System.currentTimeMillis();
        log.info("Tìm kiếm đơn vị tính với maDvt: {}, tenDvt: {}, loaiDvt: {}, trangThai: {}, cvTon: {}",
                request.getMaDvt(), request.getTenDvt(), request.getLoaiDvt(), request.getTrangThai(), request.getCvTon());

        int pageNumber = 0;
        int pageSize = 10;
        Sort sort = Sort.by(Sort.Direction.ASC, "maDvt");
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        String maDvt = StringUtils.hasText(request.getMaDvt()) ?
                QueryUtils.createLikeValue(request.getMaDvt()) : null;
        String tenDvt = StringUtils.hasText(request.getTenDvt()) ?
                QueryUtils.createLikeValue(request.getTenDvt()) : null;
        String loaiDvt = StringUtils.hasText(request.getLoaiDvt()) ?
                QueryUtils.createLikeValue(request.getLoaiDvt()) : null;
        String trangThai = StringUtils.hasText(request.getTrangThai()) ?
                request.getTrangThai() : null;
        java.math.BigDecimal cvTon = request.getCvTon();

        Page<Sdvt> page = sdvtRepository.findBySearchCriteria(maDvt, tenDvt, loaiDvt, trangThai, cvTon, pageable);

        long endTime = System.currentTimeMillis();

        return CatalogSearchResponse.<Sdvt>builder()
                .content(page.getContent())
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .totalPages(page.getTotalPages())
                .numberOfElements(page.getNumberOfElements())
                .totalElements(page.getTotalElements())
                .searchKeyword(String.format("maDvt=%s, tenDvt=%s, loaiDvt=%s, trangThai=%s, cvTon=%s",
                        request.getMaDvt(), request.getTenDvt(), request.getLoaiDvt(), request.getTrangThai(), request.getCvTon()))
                .searchTime(endTime - startTime)
                .build();
    }

    @Override
    public List<Sdvt> exportDvt(SdvtSearchRequest request) {
        log.info("Xuất dữ liệu đơn vị tính với maDvt: {}, tenDvt: {}, loaiDvt: {}, trangThai: {}, cvTon: {}",
                request.getMaDvt(), request.getTenDvt(), request.getLoaiDvt(), request.getTrangThai(), request.getCvTon());

        String maDvt = StringUtils.hasText(request.getMaDvt()) ?
                QueryUtils.createLikeValue(request.getMaDvt()) : null;
        String tenDvt = StringUtils.hasText(request.getTenDvt()) ?
                QueryUtils.createLikeValue(request.getTenDvt()) : null;
        String loaiDvt = StringUtils.hasText(request.getLoaiDvt()) ?
                QueryUtils.createLikeValue(request.getLoaiDvt()) : null;
        String trangThai = StringUtils.hasText(request.getTrangThai()) ?
                request.getTrangThai() : null;
        java.math.BigDecimal cvTon = request.getCvTon();

        return sdvtRepository.findBySearchCriteria(maDvt, tenDvt, loaiDvt, trangThai, cvTon);
    }
}
