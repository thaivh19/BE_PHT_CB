package com.pht.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import com.pht.entity.StoKhai;
import com.pht.entity.StoKhaiCt;
import com.pht.exception.BusinessException;
import com.pht.model.request.NotificationRequest;
import com.pht.model.request.ToKhaiFilterRequest;
import com.pht.model.request.ToKhaiThongTinChiTietRequest;
import com.pht.model.request.ToKhaiThongTinRequest;
import com.pht.model.request.UpdateTrangThaiRequest;
import com.pht.model.request.UpdateTrangThaiPhatHanhRequest;
import com.pht.model.response.NotificationResponse;
import com.pht.repository.ToKhaiThongTinChiTietRepository;
import com.pht.repository.ToKhaiThongTinRepository;
import com.pht.repository.SbieuCuocRepository;
import com.pht.service.ToKhaiThongTinService;
import com.pht.util.SequenceGenerator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ToKhaiThongTinServiceImpl extends BaseServiceImpl<StoKhai, Long> implements ToKhaiThongTinService {

    private final ToKhaiThongTinRepository toKhaiThongTinRepository;
    private final ToKhaiThongTinChiTietRepository toKhaiThongTinChiTietRepository;
    private final SbieuCuocRepository sbieuCuocRepository;
    private final SequenceGenerator sequenceGenerator;
    
    @PersistenceContext
    private EntityManager entityManager;
    
    // Atomic counter để tạo số thông báo duy nhất trong cùng thời điểm
    private static final AtomicInteger notificationCounter = new AtomicInteger(0);

    @Override
    public ToKhaiThongTinRepository getRepository() {
        return toKhaiThongTinRepository;
    }

    @Override
    public List<StoKhai> getAllToKhaiThongTin() {
        return toKhaiThongTinRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public StoKhai getToKhaiThongTinById(Long id) throws BusinessException {
        return toKhaiThongTinRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Không tìm thấy tờ khai thông tin với ID: " + id));
    }

    @Override
    @Transactional
    public StoKhai createToKhaiThongTin(ToKhaiThongTinRequest request) throws BusinessException {
        try {
            // Tạo entity chính
            StoKhai toKhaiThongTin = new StoKhai();
            BeanUtils.copyProperties(request, toKhaiThongTin);
            
            // Generate SoTiepNhanKhaiPhi theo format [YYYY][sequence tăng dần] - 12 số
            String soTiepNhanKhaiPhi = sequenceGenerator.generateSoTiepNhanKhaiPhi();
            toKhaiThongTin.setSoTiepNhanKhaiPhi(soTiepNhanKhaiPhi);
            
            // Set NgayKhaiPhi = ngày hiện tại
            toKhaiThongTin.setNgayKhaiPhi(LocalDate.now());
            
            // Đảm bảo trangThaiPhatHanh có giá trị mặc định là "00"
            if (toKhaiThongTin.getTrangThaiPhatHanh() == null || toKhaiThongTin.getTrangThaiPhatHanh().isEmpty()) {
                toKhaiThongTin.setTrangThaiPhatHanh("00");
            }
            
            log.info("Tạo tờ khai thông tin với SoTiepNhanKhaiPhi: {}, NgayKhaiPhi: {}", 
                    soTiepNhanKhaiPhi, toKhaiThongTin.getNgayKhaiPhi());
            
            // Lưu entity chính trước
            StoKhai savedToKhai = toKhaiThongTinRepository.save(toKhaiThongTin);
            
            // Xử lý chi tiết nếu có
            if (request.getChiTietList() != null && !request.getChiTietList().isEmpty()) {
                for (ToKhaiThongTinChiTietRequest chiTietRequest : request.getChiTietList()) {
                    StoKhaiCt chiTiet = new StoKhaiCt();
                    BeanUtils.copyProperties(chiTietRequest, chiTiet);
                    chiTiet.setToKhaiThongTinID(savedToKhai.getId());
                    toKhaiThongTinChiTietRepository.save(chiTiet);
                }
            }
            
            return savedToKhai;
        } catch (Exception e) {
            log.error("Lỗi khi tạo tờ khai thông tin: ", e);
            throw new BusinessException("Lỗi khi tạo tờ khai thông tin: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public StoKhai updateTrangThai(UpdateTrangThaiRequest request) throws BusinessException {
        try {
            StoKhai toKhaiThongTin = getToKhaiThongTinById(request.getId());
            toKhaiThongTin.setTrangThai(request.getTrangThai());
            return toKhaiThongTinRepository.save(toKhaiThongTin);
        } catch (Exception e) {
            log.error("Lỗi khi cập nhật trạng thái tờ khai thông tin: ", e);
            throw new BusinessException("Lỗi khi cập nhật trạng thái: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public StoKhai updateTrangThaiPhatHanh(UpdateTrangThaiPhatHanhRequest request) throws BusinessException {
        try {
            log.info("Bắt đầu cập nhật trạng thái phát hành cho tờ khai ID: {}, trạng thái mới: {}", 
                    request.getId(), request.getTrangThaiPhatHanh());
            
            StoKhai toKhaiThongTin = getToKhaiThongTinById(request.getId());
            
            // Lưu trạng thái cũ để log
            String trangThaiCu = toKhaiThongTin.getTrangThaiPhatHanh();
            
            // Cập nhật trạng thái phát hành mới
            toKhaiThongTin.setTrangThaiPhatHanh(request.getTrangThaiPhatHanh());
            
            StoKhai savedToKhai = toKhaiThongTinRepository.save(toKhaiThongTin);
            
            log.info("Cập nhật trạng thái phát hành thành công cho tờ khai ID: {}, từ '{}' sang '{}'", 
                    request.getId(), trangThaiCu, request.getTrangThaiPhatHanh());
            
            return savedToKhai;
        } catch (BusinessException e) {
            log.error("Lỗi khi cập nhật trạng thái phát hành: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Lỗi không xác định khi cập nhật trạng thái phát hành: ", e);
            throw new BusinessException("Lỗi hệ thống khi cập nhật trạng thái phát hành: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public NotificationResponse createNotification(NotificationRequest request) throws BusinessException {
        try {
            log.info("Bắt đầu tạo thông báo cho tờ khai ID: {}", request.getToKhaiId());
            
            // Lấy thông tin tờ khai
            StoKhai toKhaiThongTin = getToKhaiThongTinById(request.getToKhaiId());
            
            // Kiểm tra trạng thái hiện tại
            if (!"01".equals(toKhaiThongTin.getTrangThai())) {
                throw new BusinessException("Tờ khai không ở trạng thái có thể tạo thông báo. Trạng thái hiện tại: " + toKhaiThongTin.getTrangThai());
            }
            
            // Tạo số thông báo theo format: YYYYMMDD + 6 số random
            String soThongBao = generateSoThongBao();
            
            // Tạo msgId (UUID)
            String msgId = UUID.randomUUID().toString().toUpperCase();
            
            // Cập nhật trạng thái sang "02" và lưu số thông báo + msgId
            toKhaiThongTin.setTrangThai("02");
            toKhaiThongTin.setSoThongBao(soThongBao);
            toKhaiThongTin.setSoThongBaoNopPhi(soThongBao); // Cập nhật soThongBaoNopPhi = soThongBao
            toKhaiThongTin.setMsgId(msgId);
            
            // Xử lý tính phí và tổng tiền
            String maPtVc = toKhaiThongTin.getMaPhuongThucVC();
            
            // Kiểm tra điều kiện ma_pt_vc
            if (maPtVc == null || (!maPtVc.equals("2") && !maPtVc.equals("3"))) {
                log.info("Tờ khai có ma_pt_vc = {} (khác 2,3,4), đặt TONG_TIEN_PHI = 0", maPtVc);
                toKhaiThongTin.setTongTienPhi(java.math.BigDecimal.ZERO);
            } else {
                log.info("Tờ khai có ma_pt_vc = {} (thuộc 2,3,4), xử lý tính phí theo loai_HH", maPtVc);
                
                if ("LBC001".equals(toKhaiThongTin.getLoaiHang())) {
                    log.info("Tờ khai có loai_HH = 'LBC001', bắt đầu xử lý tính phí");
                    xuLyTinhPhi(toKhaiThongTin.getId());
                } else {
                    log.info("Tờ khai có loai_HH != 'LBC001', tính tổng tiền từ các row hiện có");
                    tinhTongTienPhi(toKhaiThongTin.getId(), toKhaiThongTin.getLoaiHang());
                }
            }
            
            // Lưu vào database
            toKhaiThongTinRepository.save(toKhaiThongTin);
            
            log.info("Tạo thông báo thành công cho tờ khai ID: {}, số thông báo: {}, msgId: {}", 
                    request.getToKhaiId(), soThongBao, msgId);
            
            return NotificationResponse.builder()
                    .soThongBao(soThongBao)
                    .msgId(msgId)
                    .trangThaiMoi("02")
                    .toKhaiId(request.getToKhaiId())
                    .build();
                    
        } catch (BusinessException e) {
            log.error("Lỗi khi tạo thông báo: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Lỗi không xác định khi tạo thông báo: ", e);
            throw new BusinessException("Lỗi hệ thống khi tạo thông báo: " + e.getMessage());
        }
    }
    
    /**
     * Tạo số thông báo theo format: YYYYMMDD + 6 số duy nhất
     * Sử dụng sequence từ database + nanoTime để tránh trùng lặp hoàn toàn
     */
    private String generateSoThongBao() {
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        
        // Lấy số lượng thông báo đã tạo trong ngày từ database
        long todayCount = toKhaiThongTinRepository.countTodayNotifications(datePart);
        
        // Sử dụng nanoTime để tạo phần duy nhất
        long nanoTime = System.nanoTime();
        int counter = notificationCounter.incrementAndGet();
        
        // Kết hợp sequence từ DB + nanoTime + atomic counter
        long sequenceNumber = todayCount + 1;
        long uniqueValue = (sequenceNumber * 1000) + (nanoTime % 1000) + (counter % 100);
        String uniquePart = String.format("%06d", uniqueValue % 1000000);
        
        String soThongBao = datePart + uniquePart;
        
        // Kiểm tra xem số thông báo đã tồn tại chưa (để đảm bảo không trùng lặp)
        int retryCount = 0;
        while (toKhaiThongTinRepository.existsBySoThongBao(soThongBao) && retryCount < 3) {
            // Nếu trùng, tạo số mới với nanoTime + counter + retryCount
            long newNanoTime = System.nanoTime();
            int newCounter = notificationCounter.incrementAndGet();
            long newUniqueValue = (sequenceNumber * 1000) + (newNanoTime % 1000) + (newCounter % 100) + retryCount;
            uniquePart = String.format("%06d", newUniqueValue % 1000000);
            soThongBao = datePart + uniquePart;
            retryCount++;
        }
        
        if (retryCount >= 3) {
            // Nếu vẫn trùng sau 3 lần thử, sử dụng UUID
            String uuidPart = UUID.randomUUID().toString().replace("-", "").substring(0, 6);
            soThongBao = datePart + uuidPart;
            log.warn("Sử dụng UUID fallback cho số thông báo: {}", soThongBao);
        }
        
        log.info("Tạo số thông báo: {} (sequence: {}, nanoTime: {}, counter: {})", 
                soThongBao, sequenceNumber, nanoTime % 1000, counter);
        
        return soThongBao;
    }
    
    @Override
    public List<StoKhai> findByTrangThai(String trangThai) {
        log.info("Tìm tờ khai thông tin theo trạng thái: {}", trangThai);
        return toKhaiThongTinRepository.findByTrangThai(trangThai);
    }
    
    @Override
    public List<StoKhai> filterToKhai(ToKhaiFilterRequest request) {
        log.info("Lọc tờ khai theo điều kiện: tuNgay={}, denNgay={}, trangThai={}", 
                request.getTuNgay(), request.getDenNgay(), request.getTrangThai());
        
        // Xử lý null để tránh lỗi JDBC
        java.time.LocalDate tuNgay = request.getTuNgay();
        java.time.LocalDate denNgay = request.getDenNgay();
        String trangThai = request.getTrangThai();
        
        // Nếu không có điều kiện nào, lấy tất cả
        if (tuNgay == null && denNgay == null && (trangThai == null || trangThai.trim().isEmpty())) {
            return toKhaiThongTinRepository.findAll();
        }
        
        // Nếu chỉ có trạng thái
        if ((tuNgay == null && denNgay == null) && trangThai != null && !trangThai.trim().isEmpty()) {
            return toKhaiThongTinRepository.findByTrangThai(trangThai);
        }
        
        // Nếu có ngày, sử dụng query với điều kiện ngày
        if (tuNgay != null || denNgay != null) {
            // Tạo query động dựa trên tham số có sẵn
            StringBuilder query = new StringBuilder("SELECT * FROM STO_KHAI WHERE 1=1");
            
            if (tuNgay != null) {
                query.append(" AND NGAY_KP >= :tuNgay");
            }
            if (denNgay != null) {
                query.append(" AND NGAY_KP <= :denNgay");
            }
            if (trangThai != null && !trangThai.trim().isEmpty()) {
                query.append(" AND TRANGTHAI = :trangThai");
            }
            
            query.append(" ORDER BY NGAY_TK DESC, ID DESC");
            
            // Sử dụng EntityManager để thực thi query động
            return executeDynamicQuery(query.toString(), tuNgay, denNgay, trangThai);
        }
        
        return toKhaiThongTinRepository.findAll();
    }
    
    private List<StoKhai> executeDynamicQuery(String query, java.time.LocalDate tuNgay, 
                                            java.time.LocalDate denNgay, String trangThai) {
        try {
            jakarta.persistence.Query jpqlQuery = entityManager.createNativeQuery(query, StoKhai.class);
            
            if (tuNgay != null) {
                jpqlQuery.setParameter("tuNgay", tuNgay);
            }
            if (denNgay != null) {
                jpqlQuery.setParameter("denNgay", denNgay);
            }
            if (trangThai != null && !trangThai.trim().isEmpty()) {
                jpqlQuery.setParameter("trangThai", trangThai);
            }
            
            return jpqlQuery.getResultList();
        } catch (Exception e) {
            log.error("Lỗi khi thực thi query động: ", e);
            return toKhaiThongTinRepository.findAll();
        }
    }
    
    /**
     * Xử lý tính phí cho tờ khai có loai_HH = 'LBC001'
     */
    private void xuLyTinhPhi(Long toKhaiId) {
        try {
            log.info("Bắt đầu xử lý tính phí cho tờ khai ID: {}", toKhaiId);
            
            // Lấy danh sách chi tiết tờ khai
            List<StoKhaiCt> chiTietList = toKhaiThongTinChiTietRepository.findByToKhaiThongTinID(toKhaiId);
            
            if (chiTietList == null || chiTietList.isEmpty()) {
                log.warn("Không tìm thấy chi tiết tờ khai cho ID: {}", toKhaiId);
                return;
            }
            
            int soLuongCapNhat = 0;
            
            for (StoKhaiCt chiTiet : chiTietList) {
                // Kiểm tra ma_loai_cont và ma_tc_cont có giá trị không
                if (chiTiet.getMaLoaiCont() != null && !chiTiet.getMaLoaiCont().trim().isEmpty() &&
                    chiTiet.getMaTcCont() != null && !chiTiet.getMaTcCont().trim().isEmpty()) {
                    
                    // Query đơn giá từ bảng sbieu_cuoc
                        log.info("Query đơn giá với maLoaiCont: '{}', maTcCont: '{}' (direct mapping)", 
                                chiTiet.getMaLoaiCont(), chiTiet.getMaTcCont());
                        
                        List<java.math.BigDecimal> donGiaList = sbieuCuocRepository.findDonGiaByLoaiContAndTcCont(
                            chiTiet.getMaLoaiCont(), chiTiet.getMaTcCont());
                    
                    log.info("Kết quả query: {} đơn giá tìm được", donGiaList.size());
                    
                    if (!donGiaList.isEmpty()) {
                        // Lấy đơn giá đầu tiên (nếu có nhiều kết quả)
                        java.math.BigDecimal donGia = donGiaList.get(0);
                        
                        log.info("Đơn giá tìm được: {}", donGia);
                        
                        // Cập nhật DON_GIA
                        chiTiet.setDonGia(donGia);
                        
                        // Tính SO_TIEN (giả sử bằng đơn giá, có thể điều chỉnh logic tính toán)
                        chiTiet.setSoTien(donGia);
                        
                        // Lưu chi tiết đã cập nhật
                        toKhaiThongTinChiTietRepository.save(chiTiet);
                        
                        soLuongCapNhat++;
                        
                        log.info("Cập nhật đơn giá cho chi tiết ID: {}, ma_loai_cont: {}, ma_tc_cont: {}, đơn giá: {}", 
                                chiTiet.getId(), chiTiet.getMaLoaiCont(), chiTiet.getMaTcCont(), donGia);
                        } else {
                            log.warn("Không tìm thấy đơn giá cho maLoaiCont: '{}', maTcCont: '{}'", 
                                    chiTiet.getMaLoaiCont(), chiTiet.getMaTcCont());
                        
                        // Debug: Query tất cả biểu cước để xem có gì
                        log.info("Debug: Kiểm tra tất cả biểu cước trong database...");
                        List<com.pht.entity.SbieuCuoc> allBieuCuoc = sbieuCuocRepository.findAllActive();
                        log.info("Debug: Tìm thấy {} biểu cước LBC001 với trạng thái = '1'", allBieuCuoc.size());
                        for (com.pht.entity.SbieuCuoc bc : allBieuCuoc) {
                            log.info("Debug: loaiBc='{}', maLoaiCont='{}', maTcCont='{}', loaiCont='{}', tinhChatCont='{}', donGia={}, trangThai='{}'", 
                                    bc.getLoaiBc(), bc.getMaLoaiCont(), bc.getMaTcCont(), bc.getLoaiCont(), bc.getTinhChatCont(), bc.getDonGia(), bc.getTrangThai());
                        }
                    }
                } else {
                    log.debug("Chi tiết ID: {} không có đủ thông tin ma_loai_cont hoặc ma_tc_cont để tính phí", 
                             chiTiet.getId());
                }
            }
            
            // Tính tổng tiền phí từ tất cả chi tiết
            java.math.BigDecimal tongTienPhi = chiTietList.stream()
                .filter(chiTiet -> chiTiet.getSoTien() != null)
                .map(StoKhaiCt::getSoTien)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
            
            // Cập nhật TONG_TIEN_PHI vào tờ khai chính
            StoKhai toKhai = toKhaiThongTinRepository.findById(toKhaiId).orElse(null);
            if (toKhai != null) {
                toKhai.setTongTienPhi(tongTienPhi);
                toKhaiThongTinRepository.save(toKhai);
                
                log.info("Cập nhật TONG_TIEN_PHI cho tờ khai ID: {}, tổng tiền: {}", toKhaiId, tongTienPhi);
            }
            
            log.info("Hoàn thành xử lý tính phí cho tờ khai ID: {}, đã cập nhật {} chi tiết, tổng tiền phí: {}", 
                    toKhaiId, soLuongCapNhat, tongTienPhi);
                    
        } catch (Exception e) {
            log.error("Lỗi khi xử lý tính phí cho tờ khai ID: {}", toKhaiId, e);
            // Không throw exception để không ảnh hưởng đến việc tạo thông báo
        }
    }
    
    /**
     * Tính tổng tiền phí từ các row hiện có (khi loai_HH != 'LBC001')
     * Map với bảng bieucuoc để lấy đơn giá dựa trên ma_dvt
     */
    private void tinhTongTienPhi(Long toKhaiId, String loaiHang) {
        try {
            log.info("Bắt đầu tính tổng tiền phí cho tờ khai ID: {}, loaiHang: {}", toKhaiId, loaiHang);
            
            // Lấy danh sách chi tiết tờ khai
            List<StoKhaiCt> chiTietList = toKhaiThongTinChiTietRepository.findByToKhaiThongTinID(toKhaiId);
            
            if (chiTietList == null || chiTietList.isEmpty()) {
                log.warn("Không tìm thấy chi tiết tờ khai cho ID: {}", toKhaiId);
                return;
            }
            
            int soLuongCapNhat = 0;
            java.math.BigDecimal tongTienPhi = java.math.BigDecimal.ZERO;
            
            for (StoKhaiCt chiTiet : chiTietList) {
                try {
                    // Lấy ma_dvt từ donViTinh (giả sử donViTinh chứa mã đơn vị tính)
                    String maDvt = chiTiet.getDonViTinh();
                    
                    if (maDvt != null && !maDvt.trim().isEmpty() && 
                        chiTiet.getTongTrongLuong() != null && chiTiet.getTongTrongLuong().compareTo(java.math.BigDecimal.ZERO) > 0) {
                        
                        log.info("Xử lý chi tiết ID: {}, maDvt: {}, trongLuong: {}", 
                                chiTiet.getId(), maDvt, chiTiet.getTongTrongLuong());
                        
                        // Tìm đơn giá từ bảng bieucuoc dựa trên ma_dvt và loaiHang
                        java.math.BigDecimal donGia = timDonGiaTuBieuCuoc(maDvt, loaiHang);
                        
                        if (donGia != null && donGia.compareTo(java.math.BigDecimal.ZERO) > 0) {
                            // Tính số tiền = đơn giá * trọng lượng
                            java.math.BigDecimal soTien = donGia.multiply(chiTiet.getTongTrongLuong());
                            
                            // Cập nhật thông tin vào chi tiết tờ khai
                            chiTiet.setDonGia(donGia);
                            chiTiet.setSoTien(soTien);
                            
                            // Cộng vào tổng tiền phí
                            tongTienPhi = tongTienPhi.add(soTien);
                            soLuongCapNhat++;
                            
                            log.info("Cập nhật chi tiết ID: {}, donGia: {}, soTien: {}", 
                                    chiTiet.getId(), donGia, soTien);
                        } else {
                            log.warn("Không tìm thấy đơn giá cho maDvt: '{}', loaiHang: '{}'", maDvt, loaiHang);
                        }
                    } else {
                        log.debug("Chi tiết ID: {} không có đủ thông tin maDvt hoặc trọng lượng để tính phí", 
                                 chiTiet.getId());
                    }
                } catch (Exception e) {
                    log.error("Lỗi khi xử lý chi tiết ID: {}", chiTiet.getId(), e);
                }
            }
            
            // Lưu các chi tiết đã cập nhật
            if (soLuongCapNhat > 0) {
                toKhaiThongTinChiTietRepository.saveAll(chiTietList);
                log.info("Đã lưu {} chi tiết tờ khai", soLuongCapNhat);
            }
            
            // Cập nhật TONG_TIEN_PHI vào tờ khai chính
            StoKhai toKhai = toKhaiThongTinRepository.findById(toKhaiId).orElse(null);
            if (toKhai != null) {
                toKhai.setTongTienPhi(tongTienPhi);
                toKhaiThongTinRepository.save(toKhai);
                
                log.info("Cập nhật TONG_TIEN_PHI cho tờ khai ID: {}, tổng tiền: {}", toKhaiId, tongTienPhi);
            }
            
            log.info("Hoàn thành tính tổng tiền phí cho tờ khai ID: {}, đã cập nhật {} chi tiết, tổng tiền: {}", 
                    toKhaiId, soLuongCapNhat, tongTienPhi);
                    
        } catch (Exception e) {
            log.error("Lỗi khi tính tổng tiền phí cho tờ khai ID: {}", toKhaiId, e);
            // Không throw exception để không ảnh hưởng đến việc tạo thông báo
        }
    }
    
    /**
     * Tìm đơn giá từ bảng bieucuoc dựa trên ma_dvt và loaiHang
     */
    private java.math.BigDecimal timDonGiaTuBieuCuoc(String maDvt, String loaiHang) {
        try {
            log.info("Tìm đơn giá cho maDvt: {}, loaiHang: {}", maDvt, loaiHang);
            
            // Tìm trong bảng bieucuoc với điều kiện:
            // - dvt = maDvt (đơn vị tính)
            // - loaiBc = loaiHang (loại biểu cước)
            List<com.pht.entity.SbieuCuoc> bieuCuocList = sbieuCuocRepository.findByDvtAndLoaiBc(maDvt, loaiHang);
            
            if (bieuCuocList != null && !bieuCuocList.isEmpty()) {
                com.pht.entity.SbieuCuoc bieuCuoc = bieuCuocList.get(0); // Lấy record đầu tiên
                java.math.BigDecimal donGia = bieuCuoc.getDonGia();
                
                log.info("Tìm thấy đơn giá: {} cho maDvt: {}, loaiHang: {}", donGia, maDvt, loaiHang);
                return donGia;
            } else {
                log.warn("Không tìm thấy biểu cước cho maDvt: {}, loaiHang: {}", maDvt, loaiHang);
                
                // Debug: Query tất cả biểu cước để xem có gì
                log.info("Debug: Kiểm tra tất cả biểu cước trong database...");
                List<com.pht.entity.SbieuCuoc> allBieuCuoc = sbieuCuocRepository.findAllActive();
                log.info("Debug: Tìm thấy {} biểu cước với trạng thái = '1'", allBieuCuoc.size());
                for (com.pht.entity.SbieuCuoc bc : allBieuCuoc) {
                    log.info("Debug: dvt='{}', loaiBc='{}', donGia={}, trangThai='{}'", 
                            bc.getDvt(), bc.getLoaiBc(), bc.getDonGia(), bc.getTrangThai());
                }
                
                return null;
            }
        } catch (Exception e) {
            log.error("Lỗi khi tìm đơn giá cho maDvt: {}, loaiHang: {}", maDvt, loaiHang, e);
            return null;
        }
    }
}
