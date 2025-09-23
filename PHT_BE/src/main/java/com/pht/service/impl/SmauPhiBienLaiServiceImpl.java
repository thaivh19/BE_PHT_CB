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

import com.pht.entity.SmauPhiBienLai;
import com.pht.exception.BusinessException;
import com.pht.model.request.SmauPhiBienLaiCreateRequest;
import com.pht.model.request.SmauPhiBienLaiSearchRequest;
import com.pht.model.request.SmauPhiBienLaiUpdateRequest;
import com.pht.model.response.CatalogSearchResponse;
import com.pht.repository.SmauPhiBienLaiRepository;
import com.pht.service.SmauPhiBienLaiService;
import com.pht.utils.QueryUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class SmauPhiBienLaiServiceImpl extends BaseServiceImpl<SmauPhiBienLai, Long> implements SmauPhiBienLaiService {

    @Autowired
    private SmauPhiBienLaiRepository smauPhiBienLaiRepository;

    @Override
    public SmauPhiBienLaiRepository getRepository() {
        return smauPhiBienLaiRepository;
    }

    @Override
    public List<SmauPhiBienLai> getAllMauPhiBienLai() {
        log.info("Lấy danh sách tất cả mẫu phí biên lai");
        return smauPhiBienLaiRepository.findAll();
    }

    @Override
    public CatalogSearchResponse<SmauPhiBienLai> getAllMauPhiBienLaiWithPagination(int page, int size, String sortBy, String sortDir) {
        log.info("Lấy danh sách mẫu phí biên lai với phân trang: page={}, size={}, sortBy={}, sortDir={}", page, size, sortBy, sortDir);
        Sort sort = Sort.by("ASC".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<SmauPhiBienLai> pageResult = smauPhiBienLaiRepository.findAll(pageable);
        return CatalogSearchResponse.<SmauPhiBienLai>builder()
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
    public SmauPhiBienLai getMauPhiBienLaiById(Long id) throws BusinessException {
        log.info("Lấy mẫu phí biên lai theo ID: {}", id);
        Optional<SmauPhiBienLai> result = smauPhiBienLaiRepository.findById(id);
        if (result.isEmpty()) {
            throw new BusinessException("Không tìm thấy mẫu phí biên lai với ID: " + id);
        }
        return result.get();
    }

    @Override
    public SmauPhiBienLai createMauPhiBienLai(SmauPhiBienLaiCreateRequest request) throws BusinessException {
        log.info("Tạo mới mẫu phí biên lai: {}", request.getMauBienLai());
        
        // Kiểm tra trùng lặp ký hiệu
        if (smauPhiBienLaiRepository.existsByKyHieu(request.getKyHieu())) {
            throw new BusinessException("Ký hiệu đã tồn tại: " + request.getKyHieu());
        }
        
        // Tạo entity mới
        SmauPhiBienLai entity = new SmauPhiBienLai();
        entity.setMauBienLai(request.getMauBienLai());
        entity.setKyHieu(request.getKyHieu());
        entity.setTuSo(request.getTuSo());
        entity.setDenSo(request.getDenSo());
        entity.setNgayHieuLuc(request.getNgayHieuLuc());
        entity.setDiemThuPhi(request.getDiemThuPhi());
        entity.setTrangThai(request.getTrangThai());
        entity.setPhatHanh(request.getPhatHanh());
        entity.setNguoiTao(request.getNguoiTao());
        
        // Set audit fields
        LocalDateTime now = LocalDateTime.now();
        entity.setNgayTao(now);
        entity.setNgayCapNhat(now);
        
        return smauPhiBienLaiRepository.save(entity);
    }

    @Override
    public SmauPhiBienLai updateMauPhiBienLai(SmauPhiBienLaiUpdateRequest request) throws BusinessException {
        log.info("Cập nhật mẫu phí biên lai với ID: {}", request.getId());
        
        SmauPhiBienLai existingEntity = getMauPhiBienLaiById(request.getId());
        
        // Cập nhật các trường
        existingEntity.setMauBienLai(request.getMauBienLai());
        existingEntity.setKyHieu(request.getKyHieu());
        existingEntity.setTuSo(request.getTuSo());
        existingEntity.setDenSo(request.getDenSo());
        existingEntity.setNgayHieuLuc(request.getNgayHieuLuc());
        existingEntity.setDiemThuPhi(request.getDiemThuPhi());
        existingEntity.setTrangThai(request.getTrangThai());
        existingEntity.setPhatHanh(request.getPhatHanh());
        existingEntity.setNguoiTao(request.getNguoiTao());
        
        // Set audit fields for update
        existingEntity.setNgayCapNhat(LocalDateTime.now());
        
        return smauPhiBienLaiRepository.save(existingEntity);
    }

    @Override
    public void deleteMauPhiBienLai(Long id) throws BusinessException {
        log.info("Xóa mẫu phí biên lai với ID: {}", id);
        if (!smauPhiBienLaiRepository.existsById(id)) {
            throw new BusinessException("Không tìm thấy mẫu phí biên lai với ID: " + id);
        }
        smauPhiBienLaiRepository.deleteById(id);
    }

    @Override
    public CatalogSearchResponse<SmauPhiBienLai> searchMauPhiBienLai(SmauPhiBienLaiSearchRequest request) {
        long startTime = System.currentTimeMillis();
        log.info("Tìm kiếm mẫu phí biên lai với mauBienLai: {}, kyHieu: {}, diemThuPhi: {}, trangThai: {}",
                request.getMauBienLai(), request.getKyHieu(), request.getDiemThuPhi(), request.getTrangThai());

        int pageNumber = 0;
        int pageSize = 10;
        Sort sort = Sort.by(Sort.Direction.ASC, "kyHieu");
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        String mauBienLai = StringUtils.hasText(request.getMauBienLai()) ?
                QueryUtils.createLikeValue(request.getMauBienLai()) : null;
        String kyHieu = StringUtils.hasText(request.getKyHieu()) ?
                QueryUtils.createLikeValue(request.getKyHieu()) : null;
        String diemThuPhi = StringUtils.hasText(request.getDiemThuPhi()) ?
                QueryUtils.createLikeValue(request.getDiemThuPhi()) : null;
        String trangThai = StringUtils.hasText(request.getTrangThai()) ?
                request.getTrangThai() : null;

        Page<SmauPhiBienLai> page = smauPhiBienLaiRepository.findBySearchCriteria(mauBienLai, kyHieu, diemThuPhi, trangThai, pageable);

        long endTime = System.currentTimeMillis();

        return CatalogSearchResponse.<SmauPhiBienLai>builder()
                .content(page.getContent())
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .totalPages(page.getTotalPages())
                .numberOfElements(page.getNumberOfElements())
                .totalElements(page.getTotalElements())
                .searchKeyword(String.format("mauBienLai=%s, kyHieu=%s, diemThuPhi=%s, trangThai=%s",
                        request.getMauBienLai(), request.getKyHieu(), request.getDiemThuPhi(), request.getTrangThai()))
                .searchTime(endTime - startTime)
                .build();
    }

    @Override
    public List<SmauPhiBienLai> exportMauPhiBienLai(SmauPhiBienLaiSearchRequest request) {
        log.info("Xuất dữ liệu mẫu phí biên lai với mauBienLai: {}, kyHieu: {}, diemThuPhi: {}, trangThai: {}",
                request.getMauBienLai(), request.getKyHieu(), request.getDiemThuPhi(), request.getTrangThai());

        String mauBienLai = StringUtils.hasText(request.getMauBienLai()) ?
                QueryUtils.createLikeValue(request.getMauBienLai()) : null;
        String kyHieu = StringUtils.hasText(request.getKyHieu()) ?
                QueryUtils.createLikeValue(request.getKyHieu()) : null;
        String diemThuPhi = StringUtils.hasText(request.getDiemThuPhi()) ?
                QueryUtils.createLikeValue(request.getDiemThuPhi()) : null;
        String trangThai = StringUtils.hasText(request.getTrangThai()) ?
                request.getTrangThai() : null;

        return smauPhiBienLaiRepository.findBySearchCriteria(mauBienLai, kyHieu, diemThuPhi, trangThai);
    }
}
