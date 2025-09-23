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

import com.pht.entity.SkhoCang;
import com.pht.exception.BusinessException;
import com.pht.model.request.SkhoCangCreateRequest;
import com.pht.model.request.SkhoCangSearchRequest;
import com.pht.model.request.SkhoCangUpdateRequest;
import com.pht.model.response.CatalogSearchResponse;
import com.pht.repository.SkhoCangRepository;
import com.pht.service.SkhoCangService;
import com.pht.utils.QueryUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class SkhoCangServiceImpl extends BaseServiceImpl<SkhoCang, Long> implements SkhoCangService {

    @Autowired
    private SkhoCangRepository skhoCangRepository;

    @Override
    public List<SkhoCang> getAllKhoCang() {
        log.info("Lấy danh sách tất cả kho cảng");
        return skhoCangRepository.findAll();
    }

    @Override
    public CatalogSearchResponse<SkhoCang> getAllKhoCangWithPagination(int page, int size, String sortBy, String sortDir) {
        log.info("Lấy danh sách kho cảng với phân trang: page={}, size={}, sortBy={}, sortDir={}", page, size, sortBy, sortDir);
        
        // Setup sorting
        Sort sort = Sort.by("ASC".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<SkhoCang> pageResult = skhoCangRepository.findAll(pageable);
        
        return CatalogSearchResponse.<SkhoCang>builder()
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
    public SkhoCang getKhoCangById(Long id) throws BusinessException {
        log.info("Lấy kho cảng theo ID: {}", id);
        Optional<SkhoCang> result = skhoCangRepository.findById(id);
        if (result.isEmpty()) {
            throw new BusinessException("Không tìm thấy kho cảng với ID: " + id);
        }
        return result.get();
    }

    @Override
    public SkhoCang createKhoCang(SkhoCangCreateRequest request) throws BusinessException {
        log.info("Tạo mới kho cảng: {}", request.getTen());
        
        // Kiểm tra trùng lặp mã
        if (skhoCangRepository.existsByMa(request.getMa())) {
            throw new BusinessException("Mã kho cảng đã tồn tại: " + request.getMa());
        }
        
        // Tạo entity mới
        SkhoCang entity = new SkhoCang();
        entity.setMa(request.getMa());
        entity.setTen(request.getTen());
        entity.setMaCk(request.getMaCk());
        entity.setMaHq(request.getMaHq());
        entity.setDiaChi(request.getDiaChi());
        entity.setGhiChu(request.getGhiChu());
        entity.setTrangThai(request.getTrangThai());
        
        // Set audit fields
        LocalDateTime now = LocalDateTime.now();
        entity.setNgayTao(now);
        entity.setNgayCapNhat(now);
        
        return skhoCangRepository.save(entity);
    }

    @Override
    public SkhoCang updateKhoCang(SkhoCangUpdateRequest request) throws BusinessException {
        log.info("Cập nhật kho cảng với ID: {}", request.getId());
        
        SkhoCang existingEntity = getKhoCangById(request.getId());
        
        // Cập nhật các trường
        existingEntity.setMa(request.getMa());
        existingEntity.setTen(request.getTen());
        existingEntity.setMaCk(request.getMaCk());
        existingEntity.setMaHq(request.getMaHq());
        existingEntity.setDiaChi(request.getDiaChi());
        existingEntity.setGhiChu(request.getGhiChu());
        existingEntity.setTrangThai(request.getTrangThai());
        
        // Set audit fields for update
        existingEntity.setNgayCapNhat(LocalDateTime.now());
        
        return skhoCangRepository.save(existingEntity);
    }

    @Override
    public void deleteKhoCang(Long id) throws BusinessException {
        log.info("Xóa kho cảng với ID: {}", id);
        
        if (!skhoCangRepository.existsById(id)) {
            throw new BusinessException("Không tìm thấy kho cảng với ID: " + id);
        }
        
        skhoCangRepository.deleteById(id);
    }

    @Override
    public CatalogSearchResponse<SkhoCang> searchKhoCang(SkhoCangSearchRequest request) {
        long startTime = System.currentTimeMillis();
        log.info("Tìm kiếm kho cảng với ma: {}, ten: {}, trangThai: {}", 
                request.getMa(), request.getTen(), request.getTrangThai());
        
        // Setup pagination - default values
        int pageNumber = 0;
        int pageSize = 10;
        
        // Setup sorting - default by ma
        Sort sort = Sort.by(Sort.Direction.ASC, "ma");
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        
        // Build search criteria
        String ma = StringUtils.hasText(request.getMa()) ? 
                QueryUtils.createLikeValue(request.getMa()) : null;
        String ten = StringUtils.hasText(request.getTen()) ? 
                QueryUtils.createLikeValue(request.getTen()) : null;
        String trangThai = StringUtils.hasText(request.getTrangThai()) ? 
                request.getTrangThai() : null;
        
        Page<SkhoCang> page = skhoCangRepository.findBySearchCriteria(ma, ten, trangThai, pageable);
        
        long endTime = System.currentTimeMillis();
        
        return CatalogSearchResponse.<SkhoCang>builder()
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
    public List<SkhoCang> exportKhoCang(SkhoCangSearchRequest request) {
        log.info("Xuất dữ liệu kho cảng với ma: {}, ten: {}, trangThai: {}", 
                request.getMa(), request.getTen(), request.getTrangThai());
        
        // Build search criteria
        String ma = StringUtils.hasText(request.getMa()) ? 
                QueryUtils.createLikeValue(request.getMa()) : null;
        String ten = StringUtils.hasText(request.getTen()) ? 
                QueryUtils.createLikeValue(request.getTen()) : null;
        String trangThai = StringUtils.hasText(request.getTrangThai()) ? 
                request.getTrangThai() : null;
        
        return skhoCangRepository.findBySearchCriteria(ma, ten, trangThai);
    }
}
