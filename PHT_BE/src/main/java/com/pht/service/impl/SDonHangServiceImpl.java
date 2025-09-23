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

import com.pht.entity.SDonHang;
import com.pht.entity.SDonHangCt;
import com.pht.exception.BusinessException;
import com.pht.model.request.SDonHangCreateRequest;
import com.pht.model.request.SDonHangSearchRequest;
import com.pht.model.request.SDonHangUpdateRequest;
import com.pht.model.response.CatalogSearchResponse;
import com.pht.repository.SDonHangRepository;
import com.pht.repository.ToKhaiThongTinRepository;
import com.pht.service.SDonHangService;
import com.pht.utils.QueryUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class SDonHangServiceImpl extends BaseServiceImpl<SDonHang, Long> implements SDonHangService {

    @Autowired
    private SDonHangRepository sDonHangRepository;

    @Autowired
    private ToKhaiThongTinRepository toKhaiThongTinRepository;

    @Override
    public SDonHangRepository getRepository() {
        return sDonHangRepository;
    }

    @Override
    public List<SDonHang> getAllDonHang() {
        log.info("Lấy danh sách tất cả đơn hàng");
        return sDonHangRepository.findAll();
    }

    @Override
    public CatalogSearchResponse<SDonHang> getAllDonHangWithPagination(int page, int size, String sortBy, String sortDir) {
        log.info("Lấy danh sách đơn hàng với phân trang: page={}, size={}, sortBy={}, sortDir={}", page, size, sortBy, sortDir);
        Sort sort = Sort.by("ASC".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<SDonHang> pageResult = sDonHangRepository.findAll(pageable);
        return CatalogSearchResponse.<SDonHang>builder()
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
    public SDonHang getDonHangById(Long id) throws BusinessException {
        log.info("Lấy đơn hàng theo ID: {}", id);
        Optional<SDonHang> result = sDonHangRepository.findById(id);
        if (result.isEmpty()) {
            throw new BusinessException("Không tìm thấy đơn hàng với ID: " + id);
        }
        return result.get();
    }

    @Override
    public SDonHang createDonHang(SDonHangCreateRequest request) throws BusinessException {
        log.info("Tạo mới đơn hàng: {}", request.getSoDonHang());

        // Tạo entity mới
        SDonHang entity = new SDonHang();
        entity.setMst(request.getMst());
        entity.setTenDn(request.getTenDn());
        entity.setDiaChi(request.getDiaChi());
        entity.setEmail(request.getEmail());
        entity.setSdt(request.getSdt());
        
        // Tự động generate soDonHang nếu không có hoặc trùng
        String soDonHang = request.getSoDonHang();
        if (!StringUtils.hasText(soDonHang) || sDonHangRepository.existsBySoDonHang(soDonHang)) {
            soDonHang = generateSoDonHang();
        }
        entity.setSoDonHang(soDonHang);
        
        // Tự động set ngayDonHang = now
        LocalDateTime now = LocalDateTime.now();
        entity.setNgayDonHang(now);
        entity.setLoaiThanhToan(request.getLoaiThanhToan());
        entity.setTongTien(request.getTongTien());
        entity.setNganHang(request.getNganHang());
        entity.setTrangThai(request.getTrangThai());
        entity.setMoTa(request.getMoTa());
        entity.setNguoiTao(request.getNguoiTao());
        entity.setXmlKy(request.getXmlKy());

        // Set audit fields
        entity.setNgayTao(now);
        entity.setNgaySua(now);

        // Lưu đơn hàng chính trước
        SDonHang savedEntity = sDonHangRepository.save(entity);

        // Xử lý chi tiết đơn hàng nếu có
        if (request.getChiTietList() != null && !request.getChiTietList().isEmpty()) {
            Long entityId = savedEntity.getId();
            List<SDonHangCt> chiTietList = request.getChiTietList().stream()
                    .map(ctRequest -> {
                        SDonHangCt chiTiet = new SDonHangCt();
                        chiTiet.setDonHangId(entityId);
                        chiTiet.setIdTokhai(ctRequest.getIdTokhai());
                        chiTiet.setSoThongBao(ctRequest.getSoThongBao());
                        // Convert LocalDate to LocalDateTime (set time to 00:00:00)
                        if (ctRequest.getNgayThongBao() != null) {
                            chiTiet.setNgayThongBao(ctRequest.getNgayThongBao().atStartOfDay());
                        }
                        chiTiet.setThanhTien(ctRequest.getThanhTien());
                        return chiTiet;
                    })
                    .collect(java.util.stream.Collectors.toList());

            // Khởi tạo list nếu null
            if (savedEntity.getChiTietList() == null) {
                savedEntity.setChiTietList(new java.util.ArrayList<>());
            }
            savedEntity.getChiTietList().addAll(chiTietList);
            savedEntity = sDonHangRepository.save(savedEntity);

            // Sau khi lưu chi tiết đơn hàng, cập nhật TT_NH (trangThaiNganHang) của các tờ khai về "01"
            List<Long> toKhaiIds = chiTietList.stream()
                    .map(SDonHangCt::getIdTokhai)
                    .filter(java.util.Objects::nonNull)
                    .distinct()
                    .collect(java.util.stream.Collectors.toList());
            if (!toKhaiIds.isEmpty()) {
                toKhaiThongTinRepository.findAllById(toKhaiIds).forEach(tk -> {
                    tk.setTrangThaiNganHang("01");
                });
                toKhaiThongTinRepository.flush();
            }
        }

        return savedEntity;
    }

    @Override
    public SDonHang updateDonHang(SDonHangUpdateRequest request) throws BusinessException {
        log.info("Cập nhật đơn hàng ID: {}", request.getId());

        // Lấy entity hiện tại
        SDonHang existingEntity = getDonHangById(request.getId());

        // Kiểm tra trùng lặp số đơn hàng (nếu có thay đổi)
        if (StringUtils.hasText(request.getSoDonHang()) && 
            !request.getSoDonHang().equals(existingEntity.getSoDonHang()) &&
            sDonHangRepository.existsBySoDonHang(request.getSoDonHang())) {
            throw new BusinessException("Số đơn hàng đã tồn tại: " + request.getSoDonHang());
        }

        // Cập nhật thông tin
        existingEntity.setMst(request.getMst());
        existingEntity.setTenDn(request.getTenDn());
        existingEntity.setDiaChi(request.getDiaChi());
        existingEntity.setEmail(request.getEmail());
        existingEntity.setSdt(request.getSdt());
        existingEntity.setSoDonHang(request.getSoDonHang());
        existingEntity.setNgayDonHang(request.getNgayDonHang());
        existingEntity.setLoaiThanhToan(request.getLoaiThanhToan());
        existingEntity.setTongTien(request.getTongTien());
        existingEntity.setNganHang(request.getNganHang());
        existingEntity.setTrangThai(request.getTrangThai());
        existingEntity.setMoTa(request.getMoTa());
        existingEntity.setNguoiSua(request.getNguoiSua());
        existingEntity.setXmlKy(request.getXmlKy());

        // Set audit fields for update
        existingEntity.setNgaySua(LocalDateTime.now());

        // Khởi tạo list nếu null
        if (existingEntity.getChiTietList() == null) {
            existingEntity.setChiTietList(new java.util.ArrayList<>());
        }

        // Xử lý chi tiết đơn hàng
        if (request.getChiTietList() != null) {
            // Clear existing chi tiết
            existingEntity.getChiTietList().clear();

            // Add new chi tiết
            List<SDonHangCt> chiTietList = request.getChiTietList().stream()
                    .map(ctRequest -> {
                        SDonHangCt chiTiet = new SDonHangCt();
                        chiTiet.setDonHangId(existingEntity.getId());
                        chiTiet.setIdTokhai(ctRequest.getIdTokhai());
                        chiTiet.setSoThongBao(ctRequest.getSoThongBao());
                        // Convert LocalDate to LocalDateTime (set time to 00:00:00)
                        if (ctRequest.getNgayThongBao() != null) {
                            chiTiet.setNgayThongBao(ctRequest.getNgayThongBao().atStartOfDay());
                        }
                        chiTiet.setThanhTien(ctRequest.getThanhTien());
                        return chiTiet;
                    })
                    .collect(java.util.stream.Collectors.toList());

            existingEntity.getChiTietList().addAll(chiTietList);
        }

        return sDonHangRepository.save(existingEntity);
    }

    @Override
    public void deleteDonHang(Long id) throws BusinessException {
        log.info("Xóa đơn hàng với ID: {}", id);
        if (!sDonHangRepository.existsById(id)) {
            throw new BusinessException("Không tìm thấy đơn hàng với ID: " + id);
        }
        sDonHangRepository.deleteById(id);
    }

    @Override
    public CatalogSearchResponse<SDonHang> searchDonHang(SDonHangSearchRequest request) {
        long startTime = System.currentTimeMillis();
        log.info("Tìm kiếm đơn hàng với mst: {}, tenDn: {}, soDonHang: {}, loaiThanhToan: {}, trangThai: {}, nganHang: {}",
                request.getMst(), request.getTenDn(), request.getSoDonHang(), request.getLoaiThanhToan(), 
                request.getTrangThai(), request.getNganHang());

        int pageNumber = 0;
        int pageSize = 10;
        Sort sort = Sort.by(Sort.Direction.ASC, "ngayTao");
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        String mst = StringUtils.hasText(request.getMst()) ?
                QueryUtils.createLikeValue(request.getMst()) : null;
        String tenDn = StringUtils.hasText(request.getTenDn()) ?
                QueryUtils.createLikeValue(request.getTenDn()) : null;
        String soDonHang = StringUtils.hasText(request.getSoDonHang()) ?
                QueryUtils.createLikeValue(request.getSoDonHang()) : null;
        String loaiThanhToan = StringUtils.hasText(request.getLoaiThanhToan()) ?
                QueryUtils.createLikeValue(request.getLoaiThanhToan()) : null;
        String trangThai = StringUtils.hasText(request.getTrangThai()) ?
                QueryUtils.createLikeValue(request.getTrangThai()) : null;
        String nganHang = StringUtils.hasText(request.getNganHang()) ?
                QueryUtils.createLikeValue(request.getNganHang()) : null;

        Page<SDonHang> page = sDonHangRepository.findBySearchCriteria(mst, tenDn, soDonHang, loaiThanhToan, trangThai, nganHang, pageable);

        long endTime = System.currentTimeMillis();

        return CatalogSearchResponse.<SDonHang>builder()
                .content(page.getContent())
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .totalPages(page.getTotalPages())
                .numberOfElements(page.getNumberOfElements())
                .totalElements(page.getTotalElements())
                .searchKeyword(String.format("mst=%s, tenDn=%s, soDonHang=%s, loaiThanhToan=%s, trangThai=%s, nganHang=%s",
                        request.getMst(), request.getTenDn(), request.getSoDonHang(), request.getLoaiThanhToan(), 
                        request.getTrangThai(), request.getNganHang()))
                .searchTime(endTime - startTime)
                .build();
    }

    @Override
    public List<SDonHang> exportDonHang(SDonHangSearchRequest request) {
        log.info("Xuất dữ liệu đơn hàng với mst: {}, tenDn: {}, soDonHang: {}, loaiThanhToan: {}, trangThai: {}, nganHang: {}",
                request.getMst(), request.getTenDn(), request.getSoDonHang(), request.getLoaiThanhToan(), 
                request.getTrangThai(), request.getNganHang());

        String mst = StringUtils.hasText(request.getMst()) ?
                QueryUtils.createLikeValue(request.getMst()) : null;
        String tenDn = StringUtils.hasText(request.getTenDn()) ?
                QueryUtils.createLikeValue(request.getTenDn()) : null;
        String soDonHang = StringUtils.hasText(request.getSoDonHang()) ?
                QueryUtils.createLikeValue(request.getSoDonHang()) : null;
        String loaiThanhToan = StringUtils.hasText(request.getLoaiThanhToan()) ?
                QueryUtils.createLikeValue(request.getLoaiThanhToan()) : null;
        String trangThai = StringUtils.hasText(request.getTrangThai()) ?
                QueryUtils.createLikeValue(request.getTrangThai()) : null;
        String nganHang = StringUtils.hasText(request.getNganHang()) ?
                QueryUtils.createLikeValue(request.getNganHang()) : null;

        return sDonHangRepository.findBySearchCriteria(mst, tenDn, soDonHang, loaiThanhToan, trangThai, nganHang);
    }

    @Override
    public SDonHang findBySoDonHang(String soDonHang) {
        log.info("Tìm kiếm đơn hàng theo số đơn hàng: {}", soDonHang);
        if (StringUtils.hasText(soDonHang)) {
            return sDonHangRepository.findBySoDonHang(soDonHang);
        }
        return null;
    }

    @Override
    public String generateSoDonHang() {
        log.info("Generating unique soDonHang");
        
        Long maxNumber = sDonHangRepository.findMaxSoDonHangNumber();
        if (maxNumber == null) {
            maxNumber = 0L;
        }
        
        Long nextNumber = maxNumber + 1;
        String soDonHang = String.format("DH%06d", nextNumber);
        
        // Kiểm tra xem số đơn hàng đã tồn tại chưa (để đảm bảo không trùng)
        while (sDonHangRepository.existsBySoDonHang(soDonHang)) {
            nextNumber++;
            soDonHang = String.format("DH%06d", nextNumber);
        }
        
        log.info("Generated soDonHang: {}", soDonHang);
        return soDonHang;
    }
}
