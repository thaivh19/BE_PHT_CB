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

import com.pht.entity.StramTp;
import com.pht.exception.BusinessException;
import com.pht.model.request.StramTpCreateRequest;
import com.pht.model.request.StramTpSearchRequest;
import com.pht.model.request.StramTpUpdateRequest;
import com.pht.model.response.CatalogSearchResponse;
import com.pht.repository.StramTpRepository;
import com.pht.service.StramTpService;
import com.pht.utils.QueryUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class StramTpServiceImpl extends BaseServiceImpl<StramTp, Long> implements StramTpService {

    @Autowired
    private StramTpRepository stramTpRepository;

    @Override
    public List<StramTp> getAllTramTp() {
        log.info("Lấy danh sách tất cả trạm thu phí");
        return stramTpRepository.findAll();
    }

    @Override
    public CatalogSearchResponse<StramTp> getAllTramTpWithPagination(int page, int size, String sortBy, String sortDir) {
        log.info("Lấy danh sách trạm thu phí với phân trang: page={}, size={}, sortBy={}, sortDir={}", page, size, sortBy, sortDir);
        Sort sort = Sort.by("ASC".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<StramTp> pageResult = stramTpRepository.findAll(pageable);
        return CatalogSearchResponse.<StramTp>builder()
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
    public StramTp getTramTpById(Long id) throws BusinessException {
        log.info("Lấy trạm thu phí theo ID: {}", id);
        Optional<StramTp> result = stramTpRepository.findById(id);
        if (result.isEmpty()) {
            throw new BusinessException("Không tìm thấy trạm thu phí với ID: " + id);
        }
        return result.get();
    }

    @Override
    public StramTp createTramTp(StramTpCreateRequest request) throws BusinessException {
        log.info("Tạo mới trạm thu phí: {}", request.getTenTramTp());
        
        // Kiểm tra trùng lặp mã trạm TP
        if (stramTpRepository.existsByMaTramTp(request.getMaTramTp())) {
            throw new BusinessException("Mã trạm thu phí đã tồn tại: " + request.getMaTramTp());
        }
        
        // Tạo entity mới
        StramTp entity = new StramTp();
        entity.setMaTramTp(request.getMaTramTp());
        entity.setTenTramTp(request.getTenTramTp());
        entity.setMasothue(request.getMasothue());
        entity.setDiaChi(request.getDiaChi());
        entity.setTenGiaoDich(request.getTenGiaoDich());
        entity.setTrangThai(request.getTrangThai());
        
        // Set audit fields
        LocalDateTime now = LocalDateTime.now();
        entity.setNgayTao(now);
        entity.setNgayCapNhat(now);
        
        return stramTpRepository.save(entity);
    }

    @Override
    public StramTp updateTramTp(StramTpUpdateRequest request) throws BusinessException {
        log.info("Cập nhật trạm thu phí với ID: {}", request.getId());
        
        StramTp existingEntity = getTramTpById(request.getId());
        
        // Cập nhật các trường
        existingEntity.setMaTramTp(request.getMaTramTp());
        existingEntity.setTenTramTp(request.getTenTramTp());
        existingEntity.setMasothue(request.getMasothue());
        existingEntity.setDiaChi(request.getDiaChi());
        existingEntity.setTenGiaoDich(request.getTenGiaoDich());
        existingEntity.setTrangThai(request.getTrangThai());
        
        // Set audit fields for update
        existingEntity.setNgayCapNhat(LocalDateTime.now());
        
        return stramTpRepository.save(existingEntity);
    }

    @Override
    public void deleteTramTp(Long id) throws BusinessException {
        log.info("Xóa trạm thu phí với ID: {}", id);
        if (!stramTpRepository.existsById(id)) {
            throw new BusinessException("Không tìm thấy trạm thu phí với ID: " + id);
        }
        stramTpRepository.deleteById(id);
    }

    @Override
    public CatalogSearchResponse<StramTp> searchTramTp(StramTpSearchRequest request) {
        long startTime = System.currentTimeMillis();
        log.info("Tìm kiếm trạm thu phí với maTramTp: {}, tenTramTp: {}, trangThai: {}",
                request.getMaTramTp(), request.getTenTramTp(), request.getTrangThai());

        int pageNumber = 0;
        int pageSize = 10;
        Sort sort = Sort.by(Sort.Direction.ASC, "maTramTp");
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        String maTramTp = StringUtils.hasText(request.getMaTramTp()) ?
                QueryUtils.createLikeValue(request.getMaTramTp()) : null;
        String tenTramTp = StringUtils.hasText(request.getTenTramTp()) ?
                QueryUtils.createLikeValue(request.getTenTramTp()) : null;
        String trangThai = StringUtils.hasText(request.getTrangThai()) ?
                request.getTrangThai() : null;

        Page<StramTp> page = stramTpRepository.findBySearchCriteria(maTramTp, tenTramTp, trangThai, pageable);

        long endTime = System.currentTimeMillis();

        return CatalogSearchResponse.<StramTp>builder()
                .content(page.getContent())
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .totalPages(page.getTotalPages())
                .numberOfElements(page.getNumberOfElements())
                .totalElements(page.getTotalElements())
                .searchKeyword(String.format("maTramTp=%s, tenTramTp=%s, trangThai=%s",
                        request.getMaTramTp(), request.getTenTramTp(), request.getTrangThai()))
                .searchTime(endTime - startTime)
                .build();
    }

    @Override
    public List<StramTp> exportTramTp(StramTpSearchRequest request) {
        log.info("Xuất dữ liệu trạm thu phí với maTramTp: {}, tenTramTp: {}, trangThai: {}",
                request.getMaTramTp(), request.getTenTramTp(), request.getTrangThai());

        String maTramTp = StringUtils.hasText(request.getMaTramTp()) ?
                QueryUtils.createLikeValue(request.getMaTramTp()) : null;
        String tenTramTp = StringUtils.hasText(request.getTenTramTp()) ?
                QueryUtils.createLikeValue(request.getTenTramTp()) : null;
        String trangThai = StringUtils.hasText(request.getTrangThai()) ?
                request.getTrangThai() : null;

        return stramTpRepository.findBySearchCriteria(maTramTp, tenTramTp, trangThai);
    }
}
