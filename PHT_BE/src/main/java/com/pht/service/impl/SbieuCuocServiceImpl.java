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

import com.pht.entity.SbieuCuoc;
import com.pht.exception.BusinessException;
import com.pht.model.request.SbieuCuocCreateRequest;
import com.pht.model.request.SbieuCuocSearchRequest;
import com.pht.model.request.SbieuCuocUpdateRequest;
import com.pht.model.response.CatalogSearchResponse;
import com.pht.repository.SbieuCuocRepository;
import com.pht.service.SbieuCuocService;
import com.pht.utils.QueryUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class SbieuCuocServiceImpl extends BaseServiceImpl<SbieuCuoc, Long> implements SbieuCuocService {

    @Autowired
    private SbieuCuocRepository sbieuCuocRepository;

    @Override
    public List<SbieuCuoc> getAllBieuCuoc() {
        log.info("Lấy danh sách tất cả biểu cước");
        return sbieuCuocRepository.findAll();
    }

    @Override
    public CatalogSearchResponse<SbieuCuoc> getAllBieuCuocWithPagination(int page, int size, String sortBy, String sortDir) {
        log.info("Lấy danh sách biểu cước với phân trang: page={}, size={}, sortBy={}, sortDir={}", page, size, sortBy, sortDir);
        Sort sort = Sort.by("ASC".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<SbieuCuoc> pageResult = sbieuCuocRepository.findAll(pageable);
        return CatalogSearchResponse.<SbieuCuoc>builder()
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
    public SbieuCuoc getBieuCuocById(Long id) throws BusinessException {
        log.info("Lấy biểu cước theo ID: {}", id);
        Optional<SbieuCuoc> result = sbieuCuocRepository.findById(id);
        if (result.isEmpty()) {
            throw new BusinessException("Không tìm thấy biểu cước với ID: " + id);
        }
        return result.get();
    }

    @Override
    public SbieuCuoc createBieuCuoc(SbieuCuocCreateRequest request) throws BusinessException {
        log.info("Tạo mới biểu cước: {}", request.getTenBieuCuoc());
        
        // Kiểm tra trùng lặp mã biểu cước
        if (sbieuCuocRepository.existsByMaBieuCuoc(request.getMaBieuCuoc())) {
            throw new BusinessException("Mã biểu cước đã tồn tại: " + request.getMaBieuCuoc());
        }
        
        // Tạo entity mới
        SbieuCuoc entity = new SbieuCuoc();
        entity.setMaBieuCuoc(request.getMaBieuCuoc());
        entity.setTenBieuCuoc(request.getTenBieuCuoc());
        entity.setNhomLoaiHinh(request.getNhomLoaiHinh());
        entity.setLoaiCont(request.getLoaiCont());
        entity.setTinhChatCont(request.getTinhChatCont());
        entity.setDvt(request.getDvt());
        entity.setHang(request.getHang());
        entity.setDonGia(request.getDonGia());
        entity.setLoaiBc(request.getLoaiBc());
        entity.setMaLoaiCont(request.getMaLoaiCont());
        entity.setMaTcCont(request.getMaTcCont());
        entity.setTrangThai(request.getTrangThai());
        
        // Set audit fields
        LocalDateTime now = LocalDateTime.now();
        entity.setNgayTao(now);
        entity.setNgayCapNhat(now);
        
        return sbieuCuocRepository.save(entity);
    }

    @Override
    public SbieuCuoc updateBieuCuoc(SbieuCuocUpdateRequest request) throws BusinessException {
        log.info("Cập nhật biểu cước với ID: {}", request.getId());
        
        SbieuCuoc existingEntity = getBieuCuocById(request.getId());
        
        // Cập nhật các trường
        existingEntity.setMaBieuCuoc(request.getMaBieuCuoc());
        existingEntity.setTenBieuCuoc(request.getTenBieuCuoc());
        existingEntity.setNhomLoaiHinh(request.getNhomLoaiHinh());
        existingEntity.setLoaiCont(request.getLoaiCont());
        existingEntity.setTinhChatCont(request.getTinhChatCont());
        existingEntity.setDvt(request.getDvt());
        existingEntity.setHang(request.getHang());
        existingEntity.setDonGia(request.getDonGia());
        existingEntity.setLoaiBc(request.getLoaiBc());
        existingEntity.setTrangThai(request.getTrangThai());
        
        // Set audit fields for update
        existingEntity.setNgayCapNhat(LocalDateTime.now());
        
        return sbieuCuocRepository.save(existingEntity);
    }

    @Override
    public void deleteBieuCuoc(Long id) throws BusinessException {
        log.info("Xóa biểu cước với ID: {}", id);
        if (!sbieuCuocRepository.existsById(id)) {
            throw new BusinessException("Không tìm thấy biểu cước với ID: " + id);
        }
        sbieuCuocRepository.deleteById(id);
    }

    @Override
    public CatalogSearchResponse<SbieuCuoc> searchBieuCuoc(SbieuCuocSearchRequest request) {
        long startTime = System.currentTimeMillis();
        log.info("Tìm kiếm biểu cước với maBieuCuoc: {}, tenBieuCuoc: {}, nhomLoaiHinh: {}, loaiCont: {}, loaiBc: {}, trangThai: {}",
                request.getMaBieuCuoc(), request.getTenBieuCuoc(), request.getNhomLoaiHinh(), request.getLoaiCont(), request.getLoaiBc(), request.getTrangThai());

        int pageNumber = 0;
        int pageSize = 10;
        Sort sort = Sort.by(Sort.Direction.ASC, "maBieuCuoc");
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        String maBieuCuoc = StringUtils.hasText(request.getMaBieuCuoc()) ?
                QueryUtils.createLikeValue(request.getMaBieuCuoc()) : null;
        String tenBieuCuoc = StringUtils.hasText(request.getTenBieuCuoc()) ?
                QueryUtils.createLikeValue(request.getTenBieuCuoc()) : null;
        String nhomLoaiHinh = StringUtils.hasText(request.getNhomLoaiHinh()) ?
                QueryUtils.createLikeValue(request.getNhomLoaiHinh()) : null;
        String loaiCont = StringUtils.hasText(request.getLoaiCont()) ?
                QueryUtils.createLikeValue(request.getLoaiCont()) : null;
        String loaiBc = StringUtils.hasText(request.getLoaiBc()) ?
                QueryUtils.createLikeValue(request.getLoaiBc()) : null;
        String trangThai = StringUtils.hasText(request.getTrangThai()) ?
                request.getTrangThai() : null;

        Page<SbieuCuoc> page = sbieuCuocRepository.findBySearchCriteria(maBieuCuoc, tenBieuCuoc, nhomLoaiHinh, loaiCont, loaiBc, trangThai, pageable);

        long endTime = System.currentTimeMillis();

        return CatalogSearchResponse.<SbieuCuoc>builder()
                .content(page.getContent())
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .totalPages(page.getTotalPages())
                .numberOfElements(page.getNumberOfElements())
                .totalElements(page.getTotalElements())
                .searchKeyword(String.format("maBieuCuoc=%s, tenBieuCuoc=%s, nhomLoaiHinh=%s, loaiCont=%s, trangThai=%s",
                        request.getMaBieuCuoc(), request.getTenBieuCuoc(), request.getNhomLoaiHinh(), request.getLoaiCont(), request.getTrangThai()))
                .searchTime(endTime - startTime)
                .build();
    }

    @Override
    public List<SbieuCuoc> exportBieuCuoc(SbieuCuocSearchRequest request) {
        log.info("Xuất dữ liệu biểu cước với maBieuCuoc: {}, tenBieuCuoc: {}, nhomLoaiHinh: {}, loaiCont: {}, loaiBc: {}, trangThai: {}",
                request.getMaBieuCuoc(), request.getTenBieuCuoc(), request.getNhomLoaiHinh(), request.getLoaiCont(), request.getLoaiBc(), request.getTrangThai());

        String maBieuCuoc = StringUtils.hasText(request.getMaBieuCuoc()) ?
                QueryUtils.createLikeValue(request.getMaBieuCuoc()) : null;
        String tenBieuCuoc = StringUtils.hasText(request.getTenBieuCuoc()) ?
                QueryUtils.createLikeValue(request.getTenBieuCuoc()) : null;
        String nhomLoaiHinh = StringUtils.hasText(request.getNhomLoaiHinh()) ?
                QueryUtils.createLikeValue(request.getNhomLoaiHinh()) : null;
        String loaiCont = StringUtils.hasText(request.getLoaiCont()) ?
                QueryUtils.createLikeValue(request.getLoaiCont()) : null;
        String loaiBc = StringUtils.hasText(request.getLoaiBc()) ?
                QueryUtils.createLikeValue(request.getLoaiBc()) : null;
        String trangThai = StringUtils.hasText(request.getTrangThai()) ?
                request.getTrangThai() : null;

        return sbieuCuocRepository.findBySearchCriteria(maBieuCuoc, tenBieuCuoc, nhomLoaiHinh, loaiCont, loaiBc, trangThai);
    }
}
