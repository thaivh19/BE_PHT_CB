package com.pht.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pht.dto.DoiSoatExportRequest;
import com.pht.dto.DoiSoatSearchRequest;
import com.pht.entity.SDoiSoat;
import com.pht.entity.SDoiSoatCt;
import com.pht.entity.StoKhai;
import com.pht.exception.BusinessException;
import com.pht.repository.SDoiSoatCtRepository;
import com.pht.repository.SDoiSoatRepository;
import com.pht.repository.ToKhaiThongTinRepository;
import com.pht.service.DoiSoatService;
import com.pht.service.NgayLamViecService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class DoiSoatServiceImpl implements DoiSoatService {

    @Autowired
    private SDoiSoatRepository doiSoatRepository;
    
    @Autowired
    private SDoiSoatCtRepository doiSoatCtRepository;
    
    @Autowired
    private ToKhaiThongTinRepository stoKhaiRepository;
    
    @Autowired
    private NgayLamViecService ngayLamViecService;
    

    @Override
    public SDoiSoat chayDoiSoatTuDongTheoNgayLamViec(LocalDate ngayLamViecGanNhat, LocalDate ngayHienTai) {
        log.info("Chạy đối soát tự động từ ngày làm việc gần nhất: {} đến ngày hiện tại: {}", 
                ngayLamViecGanNhat, ngayHienTai);
        
        try {
            // Lấy thời gian COT từ ngày làm việc gần nhất
            String thoiGianCotGanNhat = ngayLamViecService.layCotTuNgayLamViec(ngayLamViecGanNhat);
            log.info("Thời gian COT ngày làm việc gần nhất ({}): {}", ngayLamViecGanNhat, thoiGianCotGanNhat);
            
            // Lấy thời gian COT từ ngày làm việc hiện tại
            String thoiGianCotHienTai = ngayLamViecService.layCotTuNgayLamViec(ngayHienTai);
            log.info("Thời gian COT ngày làm việc hiện tại ({}): {}", ngayHienTai, thoiGianCotHienTai);
            
            // Tính khoảng thời gian đối soát: từ ngày làm việc gần nhất + COT của nó đến ngày hiện tại + COT của nó
            LocalDateTime thoiDiemBatDau = ngayLamViecGanNhat.atTime(LocalTime.parse(thoiGianCotGanNhat));
            LocalDateTime thoiDiemKetThuc = ngayHienTai.atTime(LocalTime.parse(thoiGianCotHienTai));
            log.info("Khoảng thời gian đối soát: từ {} đến {}", thoiDiemBatDau, thoiDiemKetThuc);
            
            // Lấy tờ khai có TTNH = "02" hoặc "03" trong khoảng thời gian đối soát
            List<StoKhai> danhSachToKhai = layToKhaiDoiSoat(thoiDiemBatDau, thoiDiemKetThuc);
            log.info("Tìm thấy {} tờ khai để đối soát", danhSachToKhai.size());
            
            if (danhSachToKhai.isEmpty()) {
                log.info("Không có tờ khai nào để đối soát");
                return null;
            }
            
            // Tạo bản ghi đối soát chính
            SDoiSoat doiSoat = taoDoiSoatChinh(ngayHienTai, danhSachToKhai);
            doiSoat = doiSoatRepository.save(doiSoat);
            log.info("Tạo đối soát chính với ID: {}", doiSoat.getId());
            
            // Tạo chi tiết đối soát
            List<SDoiSoatCt> chiTietList = taoChiTietDoiSoat(doiSoat.getId(), danhSachToKhai);
            doiSoatCtRepository.saveAll(chiTietList);
            log.info("Tạo {} chi tiết đối soát", chiTietList.size());
            
            // Cập nhật tổng số và tổng tiền
            capNhatTongSoVaTongTien(doiSoat, chiTietList);
            
            log.info("Hoàn thành đối soát từ {} đến {} với {} tờ khai", 
                    ngayLamViecGanNhat, ngayHienTai, danhSachToKhai.size());
            return doiSoat;
            
        } catch (Exception e) {
            log.error("Lỗi khi chạy đối soát từ {} đến {}: ", ngayLamViecGanNhat, ngayHienTai, e);
            throw new RuntimeException("Lỗi khi chạy đối soát: " + e.getMessage());
        }
    }



    private SDoiSoat taoDoiSoatChinh(LocalDate ngayDoiSoat, List<StoKhai> danhSachToKhai) {
        SDoiSoat doiSoat = new SDoiSoat();
        
        // Tạo số bảng kê tự động
        String soBk = "BK" + ngayDoiSoat.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        doiSoat.setSoBk(soBk);
        doiSoat.setNgayBk(ngayDoiSoat);
        doiSoat.setNgayDs(ngayDoiSoat);
        doiSoat.setLanDs(1); // Lần đối soát đầu tiên
        doiSoat.setTrangThai("00"); // Trạng thái mới tạo
        doiSoat.setTongSo(danhSachToKhai.size());
        
        // Tính tổng tiền
        BigDecimal tongTien = danhSachToKhai.stream()
                .map(StoKhai::getTongTienPhi)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        doiSoat.setTongTien(tongTien);
        
        // Set các giá trị đối soát mặc định
        doiSoat.setNhDs("00"); // Ngân hàng đối soát
        doiSoat.setKbDs("00"); // Kho bạc đối soát
        // doiSoat.setTongSoTkDds(0); // Tổng số tờ khai đối soát - TODO: Thêm field này vào entity
        
        log.info("Tạo đối soát: Số BK={}, Ngày={}, Tổng số={}, Tổng tiền={}", 
                soBk, ngayDoiSoat, danhSachToKhai.size(), tongTien);
        
        return doiSoat;
    }

    @Override
    public SDoiSoat chayDoiSoatThuCong(LocalDate ngayLamViecGanNhat, LocalDate ngayDoiSoat) {
        log.info("Chạy đối soát thủ công từ ngày làm việc gần nhất: {} đến ngày đối soát: {}", 
                ngayLamViecGanNhat, ngayDoiSoat);
        
        try {
            // Lấy thời gian COT từ ngày làm việc gần nhất
            String thoiGianCotGanNhat = ngayLamViecService.layCotTuNgayLamViec(ngayLamViecGanNhat);
            log.info("Thời gian COT ngày làm việc gần nhất ({}): {}", ngayLamViecGanNhat, thoiGianCotGanNhat);
            
            // Lấy thời gian COT từ ngày đối soát
            String thoiGianCotDoiSoat = ngayLamViecService.layCotTuNgayLamViec(ngayDoiSoat);
            log.info("Thời gian COT ngày đối soát ({}): {}", ngayDoiSoat, thoiGianCotDoiSoat);
            
            // Tính khoảng thời gian đối soát: từ ngày làm việc gần nhất + COT của nó đến ngày đối soát + COT của nó
            LocalDateTime thoiDiemBatDau = ngayLamViecGanNhat.atTime(LocalTime.parse(thoiGianCotGanNhat));
            LocalDateTime thoiDiemKetThuc = ngayDoiSoat.atTime(LocalTime.parse(thoiGianCotDoiSoat));
            log.info("Khoảng thời gian đối soát: từ {} đến {}", thoiDiemBatDau, thoiDiemKetThuc);
            
            // Lấy tờ khai có TTNH = "02" hoặc "03" trong khoảng thời gian đối soát
            List<StoKhai> danhSachToKhai = layToKhaiDoiSoat(thoiDiemBatDau, thoiDiemKetThuc);
            log.info("Tìm thấy {} tờ khai để đối soát", danhSachToKhai.size());
            
            if (danhSachToKhai.isEmpty()) {
                log.info("Không có tờ khai nào để đối soát");
                return null;
            }
            
            // Tìm lần đối soát cao nhất cho ngày này
            Integer lanDoiSoatCaoNhat = timLanDoiSoatCaoNhat(ngayDoiSoat);
            Integer lanDoiSoatMoi = lanDoiSoatCaoNhat + 1;
            log.info("Lần đối soát cao nhất: {}, Lần đối soát mới: {}", lanDoiSoatCaoNhat, lanDoiSoatMoi);
            
            // Tạo đối soát với lần đối soát tăng dần
            SDoiSoat doiSoat = taoDoiSoatThuCong(ngayDoiSoat, danhSachToKhai, lanDoiSoatMoi);
            
            // Lưu đối soát chính
            doiSoat = doiSoatRepository.save(doiSoat);
            log.info("Lưu đối soát thành công với ID: {}", doiSoat.getId());
            
            // Tạo chi tiết đối soát
            List<SDoiSoatCt> chiTietList = taoChiTietDoiSoat(doiSoat.getId(), danhSachToKhai);
            
            // Lưu chi tiết đối soát
            doiSoatCtRepository.saveAll(chiTietList);
            log.info("Lưu {} chi tiết đối soát thành công", chiTietList.size());
            
            // Cập nhật tổng số và tổng tiền
            capNhatTongSoVaTongTien(doiSoat, chiTietList);
            
            // Cập nhật trạng thái tờ khai
            capNhatTrangThaiToKhai(danhSachToKhai);
            
            log.info("Đối soát thủ công hoàn thành thành công với ID: {}", doiSoat.getId());
            return doiSoat;
            
        } catch (Exception e) {
            log.error("Lỗi khi chạy đối soát thủ công: ", e);
            throw new RuntimeException("Lỗi khi chạy đối soát thủ công: " + e.getMessage(), e);
        }
    }

    private List<SDoiSoatCt> taoChiTietDoiSoat(Long doiSoatId, List<StoKhai> danhSachToKhai) {
        List<SDoiSoatCt> chiTietList = new ArrayList<>();
        
        for (StoKhai toKhai : danhSachToKhai) {
            SDoiSoatCt chiTiet = new SDoiSoatCt();
            chiTiet.setDoiSoatId(doiSoatId);
            chiTiet.setStoKhaiId(toKhai.getId()); // ID của tờ khai
            chiTiet.setSoToKhai(toKhai.getSoToKhai());
            chiTiet.setNgayToKhai(toKhai.getNgayToKhai());
            chiTiet.setSoTnKp(toKhai.getSoToKhai()); // Sử dụng soToKhai làm soTnKp
            chiTiet.setNgayTnKp(toKhai.getNgayToKhai()); // Sử dụng ngayToKhai làm ngayTnKp
            chiTiet.setMaDoanhNghiep(toKhai.getMaDoanhNghiepKhaiPhi()); // Sử dụng maDoanhNghiepKhaiPhi
            chiTiet.setTenDoanhNghiep(toKhai.getTenDoanhNghiepKhaiPhi()); // Sử dụng tenDoanhNghiepKhaiPhi
            chiTiet.setTongTienPhi(toKhai.getTongTienPhi());
            
            // Thêm transId từ tờ khai
            chiTiet.setTransId(toKhai.getTransId());
            
            // Lấy thông tin ngân hàng từ bảng SDON_HANG thông qua ID tờ khai
            String nganHang = layThongTinNganHang(toKhai.getId());
            chiTiet.setNganHang(nganHang);
            
            // Set các giá trị đối soát cho chi tiết
            chiTiet.setNhDs("00"); // Ngân hàng đối soát
            chiTiet.setKbDs("00"); // Kho bạc đối soát
            chiTiet.setGhiChu(""); // Ghi chú mặc định
            
            chiTietList.add(chiTiet);
        }
        
        log.info("Tạo {} chi tiết đối soát", chiTietList.size());
        return chiTietList;
    }
    
    /**
     * Lấy thông tin ngân hàng từ tờ khai
     * Tạm thời trả về thông tin từ tờ khai hoặc giá trị mặc định
     */
    private String layThongTinNganHang(Long stoKhaiId) {
        try {
            log.info("Lấy thông tin ngân hàng cho tờ khai ID: {}", stoKhaiId);
            
            // Tạm thời trả về giá trị mặc định
            // TODO: Implement logic lấy thông tin ngân hàng từ SDON_HANG nếu có liên kết
            return "Ngân hàng mặc định";
            
        } catch (Exception e) {
            log.error("Lỗi khi lấy thông tin ngân hàng cho tờ khai ID {}: ", stoKhaiId, e);
            return "Không xác định";
        }
    }
    
    /**
     * Tìm lần đối soát cao nhất cho ngày cụ thể
     */
    private Integer timLanDoiSoatCaoNhat(LocalDate ngayDoiSoat) {
        try {
            // Tìm đối soát có ngày đối soát = ngàyDoiSoat và lấy lần đối soát cao nhất
            List<SDoiSoat> doiSoatList = doiSoatRepository.findByNgayDsOrderByLanDsDesc(ngayDoiSoat);
            
            if (doiSoatList.isEmpty()) {
                return 0; // Chưa có đối soát nào cho ngày này
            }
            
            return doiSoatList.get(0).getLanDs(); // Lấy lần đối soát cao nhất
            
        } catch (Exception e) {
            log.error("Lỗi khi tìm lần đối soát cao nhất cho ngày {}: ", ngayDoiSoat, e);
            return 0; // Trả về 0 nếu có lỗi
        }
    }
    
    /**
     * Tạo đối soát thủ công với lần đối soát tăng dần
     */
    private SDoiSoat taoDoiSoatThuCong(LocalDate ngayDoiSoat, List<StoKhai> danhSachToKhai, Integer lanDoiSoat) {
        SDoiSoat doiSoat = new SDoiSoat();
        
        // Tạo số bảng kê với format: BK + ngày + lần đối soát
        String soBk = "BK" + ngayDoiSoat.format(DateTimeFormatter.ofPattern("yyyyMMdd")) + lanDoiSoat;
        doiSoat.setSoBk(soBk);
        doiSoat.setNgayBk(ngayDoiSoat);
        doiSoat.setNgayDs(ngayDoiSoat);
        doiSoat.setLanDs(lanDoiSoat); // Lần đối soát tăng dần
        doiSoat.setTrangThai("00"); // Trạng thái mới tạo
        doiSoat.setTongSo(danhSachToKhai.size());
        
        // Tính tổng tiền
        BigDecimal tongTien = danhSachToKhai.stream()
                .map(StoKhai::getTongTienPhi)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        doiSoat.setTongTien(tongTien);
        
        // Set các giá trị đối soát mặc định
        doiSoat.setNhDs("00"); // Ngân hàng đối soát
        doiSoat.setKbDs("00"); // Kho bạc đối soát
        // doiSoat.setTongSoTkDds(0); // Tổng số tờ khai đối soát - TODO: Thêm field này vào entity
        
        log.info("Tạo đối soát thủ công: Số BK={}, Ngày={}, Lần đối soát={}, Tổng số={}, Tổng tiền={}", 
                soBk, ngayDoiSoat, lanDoiSoat, danhSachToKhai.size(), tongTien);
        
        return doiSoat;
    }
    
    /**
     * Cập nhật trạng thái tờ khai sau khi đối soát
     */
    private void capNhatTrangThaiToKhai(List<StoKhai> danhSachToKhai) {
        try {
            for (StoKhai toKhai : danhSachToKhai) {
                // Cập nhật trạng thái ngân hàng thành "02" (đã đối soát)
                toKhai.setTrangThaiNganHang("02");
                
                // Cập nhật trạng thái tờ khai thành "04" (đã đối soát)
                toKhai.setTrangThai("04");
            }
            
            // Lưu tất cả tờ khai đã cập nhật
            stoKhaiRepository.saveAll(danhSachToKhai);
            log.info("Cập nhật trạng thái {} tờ khai thành công", danhSachToKhai.size());
            
        } catch (Exception e) {
            log.error("Lỗi khi cập nhật trạng thái tờ khai: ", e);
            throw new RuntimeException("Lỗi khi cập nhật trạng thái tờ khai: " + e.getMessage(), e);
        }
    }
    
    /**
     * Lấy danh sách tờ khai để đối soát trong khoảng thời gian
     */
    private List<StoKhai> layToKhaiDoiSoat(LocalDateTime thoiDiemBatDau, LocalDateTime thoiDiemKetThuc) {
        try {
            // Lấy tờ khai có TTNH = "02" hoặc "03" trong khoảng thời gian đối soát
            List<StoKhai> danhSachToKhai = stoKhaiRepository.findByTrangThaiNganHangInAndNgayTtBetween(
                thoiDiemBatDau, thoiDiemKetThuc);
            
            log.info("Tìm thấy {} tờ khai có TTNH='02' hoặc '03' trong khoảng thời gian từ {} đến {}", 
                    danhSachToKhai.size(), thoiDiemBatDau, thoiDiemKetThuc);
            
            return danhSachToKhai;
            
        } catch (Exception e) {
            log.error("Lỗi khi lấy tờ khai đối soát: ", e);
            throw new RuntimeException("Lỗi khi lấy tờ khai đối soát: " + e.getMessage(), e);
        }
    }
    
    /**
     * Cập nhật tổng số và tổng tiền cho đối soát
     */
    private void capNhatTongSoVaTongTien(SDoiSoat doiSoat, List<SDoiSoatCt> chiTietList) {
        doiSoat.setTongSo(chiTietList.size());
        
        BigDecimal tongTien = chiTietList.stream()
                .map(SDoiSoatCt::getTongTienPhi)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        doiSoat.setTongTien(tongTien);
        
        doiSoatRepository.save(doiSoat);
        log.info("Cập nhật tổng số: {}, tổng tiền: {}", chiTietList.size(), tongTien);
    }

    @Override
    public List<SDoiSoat> getAll() {
        log.info("Lấy danh sách tất cả đối soát");
        try {
            List<SDoiSoat> result = doiSoatRepository.findAll();
            log.info("Lấy thành công {} bản ghi đối soát", result.size());
            return result;
        } catch (Exception e) {
            log.error("Lỗi khi lấy danh sách đối soát: ", e);
            throw new RuntimeException("Lỗi khi lấy danh sách đối soát: " + e.getMessage(), e);
        }
    }

    @Override
    public SDoiSoat getById(Long id) throws BusinessException {
        log.info("Lấy thông tin đối soát với ID: {}", id);
        try {
            SDoiSoat result = doiSoatRepository.findById(id)
                    .orElseThrow(() -> new BusinessException("Không tìm thấy đối soát với ID: " + id));
            log.info("Lấy thành công đối soát với ID: {}", id);
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Lỗi khi lấy đối soát với ID {}: ", id, e);
            throw new RuntimeException("Lỗi khi lấy đối soát: " + e.getMessage(), e);
        }
    }

    @Override
    public List<SDoiSoatCt> exportDoiSoatByDate(DoiSoatExportRequest request) throws BusinessException {
        log.info("Export dữ liệu đối soát theo ngày: {}", request.getNgayDs());
        
        try {
            // Validate request
            if (request.getNgayDs() == null) {
                throw new BusinessException("Ngày đối soát không được để trống");
            }
            
            // Lấy dữ liệu đối soát chi tiết theo ngày với LAN_DS lớn nhất
            List<SDoiSoatCt> result;
            
            if (request.getLoaiDoiSoat() != null && !request.getLoaiDoiSoat().trim().isEmpty()) {
                // Filter theo loại đối soát
                if ("NH".equals(request.getLoaiDoiSoat())) {
                    // Chỉ lấy dữ liệu đã đối soát với ngân hàng
                    result = doiSoatCtRepository.findByNgayDsAndNganHangMaxLanDs(
                        request.getNgayDs(), request.getNganHang());
                } else if ("KB".equals(request.getLoaiDoiSoat())) {
                    // Chỉ lấy dữ liệu đã đối soát với kho bạc
                    result = doiSoatCtRepository.findByNgayDsMaxLanDs(request.getNgayDs());
                } else {
                    // Lấy tất cả
                    result = doiSoatCtRepository.findByNgayDsMaxLanDs(request.getNgayDs());
                }
            } else {
                // Lấy tất cả dữ liệu đối soát chi tiết theo ngày
                result = doiSoatCtRepository.findByNgayDsMaxLanDs(request.getNgayDs());
            }
            
            log.info("Export thành công {} bản ghi đối soát chi tiết cho ngày {}", 
                    result.size(), request.getNgayDs());
            
            return result;
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Lỗi khi export dữ liệu đối soát: ", e);
            throw new BusinessException("Lỗi khi export dữ liệu đối soát: " + e.getMessage());
        }
    }

    @Override
    public List<SDoiSoat> getDoiSoatByDate(LocalDate ngayDs) throws BusinessException {
        log.info("Lấy danh sách đối soát theo ngày: {}", ngayDs);
        
        try {
            // Lấy danh sách SDoiSoat theo ngày với LAN_DS lớn nhất
            List<SDoiSoat> result = doiSoatRepository.findByNgayDsAndMaxLanDs(ngayDs);
            
            log.info("Lấy thành công {} bản ghi đối soát cho ngày {}", result.size(), ngayDs);
            
            return result;
            
        } catch (Exception e) {
            log.error("Lỗi khi lấy danh sách đối soát theo ngày: ", e);
            throw new BusinessException("Lỗi khi lấy danh sách đối soát: " + e.getMessage());
        }
    }

    @Override
    public List<SDoiSoat> searchDoiSoat(DoiSoatSearchRequest request) throws BusinessException {
        log.info("Tìm kiếm đối soát từ {} đến {}", request.getTuNgay(), request.getDenNgay());
        
        try {
            // Validate request
            if (request.getTuNgay() == null || request.getDenNgay() == null) {
                throw new BusinessException("Từ ngày và đến ngày không được để trống");
            }
            
            if (request.getTuNgay().isAfter(request.getDenNgay())) {
                throw new BusinessException("Từ ngày không được lớn hơn đến ngày");
            }
            
            // Sử dụng repository method có sẵn hoặc tạo query mới
            // Tạm thời sử dụng findAll và filter trong memory
            List<SDoiSoat> allDoiSoat = doiSoatRepository.findAll();
            
            List<SDoiSoat> result = allDoiSoat.stream()
                .filter(ds -> ds.getNgayDs() != null)
                .filter(ds -> !ds.getNgayDs().isBefore(request.getTuNgay()) && 
                             !ds.getNgayDs().isAfter(request.getDenNgay()))
                .filter(ds -> request.getNganHang() == null || request.getNganHang().trim().isEmpty() || 
                             // TODO: Add filter by nganHang when available in SDoiSoat
                             true)
                .filter(ds -> request.getNhDs() == null || request.getNhDs().trim().isEmpty() ||
                             request.getNhDs().equals(ds.getNhDs()))
                .filter(ds -> request.getKbDs() == null || request.getKbDs().trim().isEmpty() ||
                             request.getKbDs().equals(ds.getKbDs()))
                .filter(ds -> request.getTrangThai() == null || request.getTrangThai().trim().isEmpty() ||
                             request.getTrangThai().equals(ds.getTrangThai()))
                .collect(java.util.stream.Collectors.toList());
            
            log.info("Tìm thấy {} bản ghi đối soát thỏa mãn điều kiện", result.size());
            
            return result;
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Lỗi khi tìm kiếm đối soát: ", e);
            throw new BusinessException("Lỗi khi tìm kiếm đối soát: " + e.getMessage());
        }
    }
}