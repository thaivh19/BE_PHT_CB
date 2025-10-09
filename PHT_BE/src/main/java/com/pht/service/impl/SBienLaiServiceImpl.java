package com.pht.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

import com.pht.entity.SBienLai;
import com.pht.entity.SBienLaiCt;
import com.pht.exception.BusinessException;
import com.pht.model.request.SBienLaiCreateRequest;
import com.pht.model.request.SBienLaiSearchRequest;
import com.pht.model.request.SBienLaiUpdateRequest;
import com.pht.model.response.CatalogSearchResponse;
import com.pht.model.response.BlThuReportItem;
import com.pht.model.response.KhoBlReportItem;
import com.pht.repository.SBienLaiRepository;
import com.pht.repository.ToKhaiThongTinRepository;
import com.pht.service.SBienLaiService;
import com.pht.utils.QueryUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class SBienLaiServiceImpl extends BaseServiceImpl<SBienLai, Long> implements SBienLaiService {

    @Autowired
    private SBienLaiRepository sBienLaiRepository;
    
    @Autowired
    private ToKhaiThongTinRepository toKhaiThongTinRepository;

    @Override
    public SBienLaiRepository getRepository() {
        return sBienLaiRepository;
    }

    @Override
    public List<SBienLai> getAllBienLai() {
        log.info("Lấy danh sách tất cả biên lai");
        return sBienLaiRepository.findAll();
    }

    @Override
    public CatalogSearchResponse<SBienLai> getAllBienLaiWithPagination(int page, int size, String sortBy, String sortDir) {
        log.info("Lấy danh sách biên lai với phân trang: page={}, size={}, sortBy={}, sortDir={}", page, size, sortBy, sortDir);
        Sort sort = Sort.by("ASC".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<SBienLai> pageResult = sBienLaiRepository.findAll(pageable);
        return CatalogSearchResponse.<SBienLai>builder()
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
    public SBienLai getBienLaiById(Long id) throws BusinessException {
        log.info("Lấy biên lai theo ID: {}", id);
        Optional<SBienLai> result = sBienLaiRepository.findById(id);
        if (result.isEmpty()) {
            throw new BusinessException("Không tìm thấy biên lai với ID: " + id);
        }
        return result.get();
    }

    @Override
    public SBienLai createBienLai(SBienLaiCreateRequest request) throws BusinessException {
        log.info("Tạo mới biên lai: {}", request.getMaBl());
        
        // Kiểm tra trùng lặp mã biên lai
        if (StringUtils.hasText(request.getMaBl()) && sBienLaiRepository.existsByMaBl(request.getMaBl())) {
            throw new BusinessException("Mã biên lai đã tồn tại: " + request.getMaBl());
        }
        
        // Kiểm tra trùng lặp số biên lai
        if (StringUtils.hasText(request.getSoBl()) && sBienLaiRepository.existsBySoBl(request.getSoBl())) {
            throw new BusinessException("Số biên lai đã tồn tại: " + request.getSoBl());
        }
        
        // Tạo entity mới
        SBienLai entity = new SBienLai();
        entity.setMst(request.getMst());
        entity.setTenDvi(request.getTenDvi());
        entity.setDiaChi(request.getDiaChi());
        entity.setEmail(request.getEmail());
        entity.setSdt(request.getSdt());
        entity.setMaBl(request.getMaBl());
        entity.setSoBl(request.getSoBl());
        entity.setHthucTtoan(request.getHthucTtoan());
        entity.setNgayBl(request.getNgayBl());
        entity.setLoaiCtiet(request.getLoaiCtiet());
        entity.setGhiChu(request.getGhiChu());
        entity.setStb(request.getStb());
        entity.setNgayNop(request.getNgayNop());
        entity.setSoTk(request.getSoTk());
        entity.setNgayTk(request.getNgayTk());
        entity.setMaKho(request.getMaKho());
        entity.setNguoiTao(request.getNguoiTao());
        entity.setIdPhatHanh(request.getIdPhatHanh());
        entity.setImageBl(request.getImageBl());
        
        // Set audit fields
        LocalDateTime now = LocalDateTime.now();
        entity.setNgayTao(now);
        entity.setNgaySua(now);
        
        // Lưu biên lai chính trước
        SBienLai savedEntity = sBienLaiRepository.save(entity);
        
        // Xử lý chi tiết biên lai nếu có
        if (request.getChiTietList() != null && !request.getChiTietList().isEmpty()) {
            Long entityId = savedEntity.getId();
            List<SBienLaiCt> chiTietList = request.getChiTietList().stream()
                    .map(ctRequest -> {
                        SBienLaiCt chiTiet = new SBienLaiCt();
                        chiTiet.setBlId(entityId);
                        chiTiet.setNdungTp(ctRequest.getNdungTp());
                        chiTiet.setDvt(ctRequest.getDvt());
                        chiTiet.setSoLuong(ctRequest.getSoLuong());
                        chiTiet.setDonGia(ctRequest.getDonGia());
                        chiTiet.setSoTien(ctRequest.getSoTien());
                        return chiTiet;
                    })
                    .collect(java.util.stream.Collectors.toList());
            
            // Khởi tạo list nếu null
            if (savedEntity.getChiTietList() == null) {
                savedEntity.setChiTietList(new java.util.ArrayList<>());
            }
            savedEntity.getChiTietList().addAll(chiTietList);
            savedEntity = sBienLaiRepository.save(savedEntity);
        }
        
        // Cập nhật ID biên lai vào bảng StoKhai nếu có toKhaiId
        if (request.getToKhaiId() != null) {
            updateToKhaiWithBienLaiId(request.getToKhaiId(), savedEntity.getId(), savedEntity.getSoBl(), savedEntity.getNgayBl());
        }
        
        return savedEntity;
    }

    @Override
    public SBienLai updateBienLai(SBienLaiUpdateRequest request) throws BusinessException {
        log.info("Cập nhật biên lai với ID: {}", request.getId());
        
        SBienLai existingEntity = getBienLaiById(request.getId());
        
        // Cập nhật các trường
        existingEntity.setMst(request.getMst());
        existingEntity.setTenDvi(request.getTenDvi());
        existingEntity.setDiaChi(request.getDiaChi());
        existingEntity.setEmail(request.getEmail());
        existingEntity.setSdt(request.getSdt());
        existingEntity.setMaBl(request.getMaBl());
        existingEntity.setSoBl(request.getSoBl());
        existingEntity.setHthucTtoan(request.getHthucTtoan());
        existingEntity.setNgayBl(request.getNgayBl());
        existingEntity.setLoaiCtiet(request.getLoaiCtiet());
        existingEntity.setGhiChu(request.getGhiChu());
        existingEntity.setStb(request.getStb());
        existingEntity.setNgayNop(request.getNgayNop());
        existingEntity.setSoTk(request.getSoTk());
        existingEntity.setNgayTk(request.getNgayTk());
        existingEntity.setMaKho(request.getMaKho());
        existingEntity.setNguoiSua(request.getNguoiSua());
        existingEntity.setIdPhatHanh(request.getIdPhatHanh());
        existingEntity.setImageBl(request.getImageBl());
        
        // Set audit fields for update
        existingEntity.setNgaySua(LocalDateTime.now());
        
        // Khởi tạo list nếu null
        if (existingEntity.getChiTietList() == null) {
            existingEntity.setChiTietList(new java.util.ArrayList<>());
        }
        
        // Xử lý chi tiết biên lai nếu có
        if (request.getChiTietList() != null && !request.getChiTietList().isEmpty()) {
            // Xóa tất cả chi tiết cũ trước
            existingEntity.getChiTietList().clear();
            
            // Thêm chi tiết mới
            List<SBienLaiCt> chiTietList = request.getChiTietList().stream()
                    .map(ctRequest -> {
                        SBienLaiCt chiTiet = new SBienLaiCt();
                        chiTiet.setBlId(existingEntity.getId());
                        chiTiet.setNdungTp(ctRequest.getNdungTp());
                        chiTiet.setDvt(ctRequest.getDvt());
                        chiTiet.setSoLuong(ctRequest.getSoLuong());
                        chiTiet.setDonGia(ctRequest.getDonGia());
                        chiTiet.setSoTien(ctRequest.getSoTien());
                        return chiTiet;
                    })
                    .collect(java.util.stream.Collectors.toList());
            
            existingEntity.getChiTietList().addAll(chiTietList);
        } else {
            // Nếu không có chi tiết mới, xóa tất cả chi tiết cũ
            existingEntity.getChiTietList().clear();
        }
        
        return sBienLaiRepository.save(existingEntity);
    }

    @Override
    public void deleteBienLai(Long id) throws BusinessException {
        log.info("Xóa biên lai với ID: {}", id);
        if (!sBienLaiRepository.existsById(id)) {
            throw new BusinessException("Không tìm thấy biên lai với ID: " + id);
        }
        sBienLaiRepository.deleteById(id);
    }

    @Override
    public CatalogSearchResponse<SBienLai> searchBienLai(SBienLaiSearchRequest request) {
        long startTime = System.currentTimeMillis();
        log.info("Tìm kiếm biên lai với mst: {}, tenDvi: {}, maBl: {}, soBl: {}, soTk: {}, maKho: {}",
                request.getMst(), request.getTenDvi(), request.getMaBl(), request.getSoBl(), request.getSoTk(), request.getMaKho());

        int pageNumber = 0;
        int pageSize = 10;
        Sort sort = Sort.by(Sort.Direction.ASC, "ngayTao");
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        String mst = StringUtils.hasText(request.getMst()) ?
                QueryUtils.createLikeValue(request.getMst()) : null;
        String tenDvi = StringUtils.hasText(request.getTenDvi()) ?
                QueryUtils.createLikeValue(request.getTenDvi()) : null;
        String maBl = StringUtils.hasText(request.getMaBl()) ?
                QueryUtils.createLikeValue(request.getMaBl()) : null;
        String soBl = StringUtils.hasText(request.getSoBl()) ?
                QueryUtils.createLikeValue(request.getSoBl()) : null;
        String soTk = StringUtils.hasText(request.getSoTk()) ?
                QueryUtils.createLikeValue(request.getSoTk()) : null;
        String maKho = StringUtils.hasText(request.getMaKho()) ?
                QueryUtils.createLikeValue(request.getMaKho()) : null;

        Page<SBienLai> page = sBienLaiRepository.findBySearchCriteria(mst, tenDvi, maBl, soBl, soTk, maKho, pageable);

        long endTime = System.currentTimeMillis();

        return CatalogSearchResponse.<SBienLai>builder()
                .content(page.getContent())
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .totalPages(page.getTotalPages())
                .numberOfElements(page.getNumberOfElements())
                .totalElements(page.getTotalElements())
                .searchKeyword(String.format("mst=%s, tenDvi=%s, maBl=%s, soBl=%s, soTk=%s, maKho=%s",
                        request.getMst(), request.getTenDvi(), request.getMaBl(), request.getSoBl(), request.getSoTk(), request.getMaKho()))
                .searchTime(endTime - startTime)
                .build();
    }

    @Override
    public List<SBienLai> exportBienLai(SBienLaiSearchRequest request) {
        log.info("Xuất dữ liệu biên lai với mst: {}, tenDvi: {}, maBl: {}, soBl: {}, soTk: {}, maKho: {}",
                request.getMst(), request.getTenDvi(), request.getMaBl(), request.getSoBl(), request.getSoTk(), request.getMaKho());

        String mst = StringUtils.hasText(request.getMst()) ?
                QueryUtils.createLikeValue(request.getMst()) : null;
        String tenDvi = StringUtils.hasText(request.getTenDvi()) ?
                QueryUtils.createLikeValue(request.getTenDvi()) : null;
        String maBl = StringUtils.hasText(request.getMaBl()) ?
                QueryUtils.createLikeValue(request.getMaBl()) : null;
        String soBl = StringUtils.hasText(request.getSoBl()) ?
                QueryUtils.createLikeValue(request.getSoBl()) : null;
        String soTk = StringUtils.hasText(request.getSoTk()) ?
                QueryUtils.createLikeValue(request.getSoTk()) : null;
        String maKho = StringUtils.hasText(request.getMaKho()) ?
                QueryUtils.createLikeValue(request.getMaKho()) : null;

        return sBienLaiRepository.findBySearchCriteria(mst, tenDvi, maBl, soBl, soTk, maKho);
    }
    
    @Override
    public SBienLai findByMaBl(String maBl) {
        log.info("Tìm kiếm biên lai theo mã BL: {}", maBl);
        if (StringUtils.hasText(maBl)) {
            return sBienLaiRepository.findByMaBl(maBl);
        }
        return null;
    }
    
    @Override
    public List<BlThuReportItem> reportBlThu(LocalDate fromDate, LocalDate toDate) {
        log.info("Báo cáo BL thu từ {} đến {}", fromDate, toDate);
        if (fromDate == null || toDate == null) {
            throw new IllegalArgumentException("fromDate và toDate không được null");
        }
        // Quy ước: khoảng [fromDate, toDate] bao gồm cả toDate -> chuyển toDate+1 ngày cho truy vấn < toDateExclusive
        LocalDateTime fromDateTime = fromDate.atStartOfDay();
        LocalDateTime toDateExclusive = toDate.plusDays(1).atStartOfDay();
        List<Object[]> rows = sBienLaiRepository.reportBlThuRaw(fromDateTime, toDateExclusive);
        List<BlThuReportItem> result = new ArrayList<>();
        for (Object[] row : rows) {
            String mst = (String) row[0];
            String tenDvi = (String) row[1];
            java.sql.Date ngaySql = (java.sql.Date) row[2];
            LocalDate ngay = ngaySql.toLocalDate();
            BigDecimal tongTien = (BigDecimal) row[3];
            Long soBienLai = ((Number) row[4]).longValue();
            result.add(new BlThuReportItem(mst, tenDvi, ngay, tongTien, soBienLai));
        }
        return result;
    }

    @Override
    public List<KhoBlReportItem> reportByKho(LocalDate fromDate, LocalDate toDate) {
        log.info("Báo cáo BL theo kho từ {} đến {}", fromDate, toDate);
        if (fromDate == null || toDate == null) {
            throw new IllegalArgumentException("fromDate và toDate không được null");
        }
        LocalDateTime fromDateTime = fromDate.atStartOfDay();
        LocalDateTime toDateExclusive = toDate.plusDays(1).atStartOfDay();
        List<Object[]> rows = sBienLaiRepository.reportByKhoRaw(fromDateTime, toDateExclusive);
        java.math.BigDecimal totalAmount = rows.stream()
            .map(r -> r[1] == null ? java.math.BigDecimal.ZERO : (java.math.BigDecimal) r[1])
            .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        java.util.List<KhoBlReportItem> result = new java.util.ArrayList<>();
        for (Object[] r : rows) {
            String maKho = (String) r[0];
            java.math.BigDecimal tongTien = r[1] == null ? java.math.BigDecimal.ZERO : (java.math.BigDecimal) r[1];
            Long soBienLai = r[2] == null ? 0L : ((Number) r[2]).longValue();
            java.math.BigDecimal tyLe = java.math.BigDecimal.ZERO;
            if (totalAmount.compareTo(java.math.BigDecimal.ZERO) > 0) {
                tyLe = tongTien.multiply(java.math.BigDecimal.valueOf(100))
                        .divide(totalAmount, java.math.MathContext.DECIMAL64);
            }
            result.add(new KhoBlReportItem(maKho, tongTien, soBienLai, tyLe));
        }
        return result;
    }
    
    /**
     * Cập nhật ID biên lai vào bảng StoKhai
     */
    private void updateToKhaiWithBienLaiId(Long toKhaiId, Long bienLaiId, String soBienLai, LocalDateTime ngayBienLai) {
        try {
            log.info("Cập nhật ID biên lai {} vào tờ khai {}", bienLaiId, toKhaiId);
            
            // Kiểm tra tờ khai có tồn tại không
            if (!toKhaiThongTinRepository.existsById(toKhaiId)) {
                log.warn("Tờ khai với ID {} không tồn tại trong hệ thống", toKhaiId);
                return;
            }
            
            Optional<com.pht.entity.StoKhai> toKhaiOpt = toKhaiThongTinRepository.findById(toKhaiId);
            if (toKhaiOpt.isPresent()) {
                com.pht.entity.StoKhai toKhai = toKhaiOpt.get();
                toKhai.setIdBienLai(bienLaiId);
                toKhai.setSoBienLai(soBienLai);
                if (ngayBienLai != null) {
                    toKhai.setNgayBienLai(ngayBienLai.toLocalDate());
                }
                
                toKhaiThongTinRepository.save(toKhai);
                log.info("Đã cập nhật thành công ID biên lai {} vào tờ khai {}", bienLaiId, toKhaiId);
            } else {
                log.warn("Không tìm thấy tờ khai với ID: {} sau khi kiểm tra existsById", toKhaiId);
            }
        } catch (Exception e) {
            log.error("Lỗi khi cập nhật ID biên lai {} vào tờ khai {}: {}", bienLaiId, toKhaiId, e.getMessage(), e);
            // Không throw exception để không ảnh hưởng đến việc tạo biên lai
        }
    }

    @Override
    public CatalogSearchResponse<SBienLai> searchBienLaiByDateRange(LocalDate fromDate, LocalDate toDate, int page, int size) {
        long startTime = System.currentTimeMillis();
        log.info("Tìm kiếm biên lai theo ngày từ {} đến {}", fromDate, toDate);

        // Chuyển đổi LocalDate thành LocalDateTime
        LocalDateTime fromDateTime = fromDate.atStartOfDay();
        LocalDateTime toDateTime = toDate.plusDays(1).atStartOfDay().minusSeconds(1);

        Sort sort = Sort.by(Sort.Direction.ASC, "ngayBl");
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<SBienLai> pageResult = sBienLaiRepository.findByNgayBlBetween(fromDateTime, toDateTime, pageable);

        long endTime = System.currentTimeMillis();

        return CatalogSearchResponse.<SBienLai>builder()
                .content(pageResult.getContent())
                .pageNumber(page)
                .pageSize(size)
                .totalPages(pageResult.getTotalPages())
                .numberOfElements(pageResult.getNumberOfElements())
                .totalElements(pageResult.getTotalElements())
                .searchKeyword(String.format("fromDate=%s, toDate=%s", fromDate, toDate))
                .searchTime(endTime - startTime)
                .build();
    }

    @Override
    public List<SBienLai> searchBienLaiByDateRange(LocalDate fromDate, LocalDate toDate) {
        log.info("Tìm kiếm biên lai theo ngày từ {} đến {} (không phân trang)", fromDate, toDate);

        // Chuyển đổi LocalDate thành LocalDateTime
        LocalDateTime fromDateTime = fromDate.atStartOfDay();
        LocalDateTime toDateTime = toDate.plusDays(1).atStartOfDay().minusSeconds(1);

        return sBienLaiRepository.findByNgayBlBetween(fromDateTime, toDateTime);
    }
}
