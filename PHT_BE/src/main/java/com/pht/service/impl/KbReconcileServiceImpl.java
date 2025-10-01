package com.pht.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pht.dto.KbReconcileRequest;
import com.pht.entity.SDoiSoat;
import com.pht.entity.SDoiSoatCt;
import com.pht.entity.SDoiSoatThua;
import com.pht.entity.SlogNhKb;
import com.pht.exception.BusinessException;
import com.pht.repository.SDoiSoatCtRepository;
import com.pht.repository.SDoiSoatRepository;
import com.pht.repository.SDoiSoatThuaRepository;
import com.pht.repository.SlogNhKbRepository;
import com.pht.service.KbReconcileService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class KbReconcileServiceImpl implements KbReconcileService {
    
    @Autowired
    private SlogNhKbRepository slogNhKbRepository;
    
    @Autowired
    private SDoiSoatCtRepository sDoiSoatCtRepository;
    
    @Autowired
    private SDoiSoatRepository sDoiSoatRepository;
    
    @Autowired
    private SDoiSoatThuaRepository sDoiSoatThuaRepository;
    
    private final ObjectMapper objectMapper;
    
    public KbReconcileServiceImpl() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
    
    @Override
    public void processKbReconcile(KbReconcileRequest request) throws BusinessException {
        log.info("Bắt đầu xử lý đối soát kho bạc: {} giao dịch, tổng tiền: {}", 
                request.getTotalTransaction(), request.getTotalAmount());
        
        try {
            // Validate request
            validateRequest(request);
            
            // Chuyển đổi JSON thành string
            String jsonData = convertToJsonString(request);
            
            // Tạo entity SlogNhKb
            SlogNhKb slogNhKb = new SlogNhKb();
            slogNhKb.setNgayDs(request.getReconcileDate().atStartOfDay()); // Ngày đối soát
            slogNhKb.setNgayNhan(LocalDateTime.now()); // Ngày nhận = now
            slogNhKb.setLoai("KB"); // Loại = KB (từ kho bạc)
            slogNhKb.setNganHang("KB"); // Ngân hàng = KB (kho bạc)
            slogNhKb.setJsonData(jsonData); // JSON data
            
            // Lưu vào database
            slogNhKbRepository.save(slogNhKb);
            
            // Thực hiện đối soát chi tiết
            performDetailedReconcile(request);
            
            log.info("Lưu thành công log đối soát kho bạc: {} giao dịch, tổng tiền: {}", 
                    request.getTotalTransaction(), request.getTotalAmount());
            
        } catch (Exception e) {
            log.error("Lỗi khi xử lý đối soát kho bạc: ", e);
            throw new BusinessException("Lỗi khi xử lý đối soát kho bạc: " + e.getMessage());
        }
    }
    
    private void validateRequest(KbReconcileRequest request) throws BusinessException {
        if (request == null) {
            throw new BusinessException("Request không được null");
        }
        
        if (request.getReconcileDate() == null) {
            throw new BusinessException("Ngày đối soát không được null");
        }
        
        if (request.getTotalTransaction() == null || request.getTotalTransaction() <= 0) {
            throw new BusinessException("Tổng số giao dịch phải lớn hơn 0");
        }
        
        if (request.getTotalAmount() == null || request.getTotalAmount().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Tổng số tiền phải lớn hơn 0");
        }
        
        if (request.getTransactions() == null || request.getTransactions().isEmpty()) {
            throw new BusinessException("Danh sách giao dịch không được null hoặc rỗng");
        }
        
        // Validate từng transaction
        for (KbReconcileRequest.KbTransaction transaction : request.getTransactions()) {
            if (transaction.getTransId() == null || transaction.getTransId().trim().isEmpty()) {
                throw new BusinessException("Mã giao dịch không được null hoặc rỗng");
            }
            
            if (transaction.getAmount() == null || transaction.getAmount().compareTo(java.math.BigDecimal.ZERO) <= 0) {
                throw new BusinessException("Số tiền phải lớn hơn 0");
            }
        }
    }
    
    private String convertToJsonString(KbReconcileRequest request) throws BusinessException {
        try {
            return objectMapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            log.error("Lỗi khi chuyển đổi JSON: ", e);
            throw new BusinessException("Lỗi khi chuyển đổi JSON: " + e.getMessage());
        }
    }
    
    /**
     * Thực hiện đối soát chi tiết giữa dữ liệu từ kho bạc và SDOI_SOAT_CT
     */
    private void performDetailedReconcile(KbReconcileRequest request) throws BusinessException {
        log.info("Bắt đầu đối soát chi tiết kho bạc: {}", request.getReconcileDate());
        
        try {
            // Lấy các bản ghi SDOI_SOAT_CT chưa đối soát với kho bạc (KB_DS = "00" hoặc NULL)
            List<SDoiSoatCt> doiSoatCtList = sDoiSoatCtRepository.findByNgayDsMaxLanDsAndKbDs00(request.getReconcileDate());
            
            log.info("Tìm thấy {} bản ghi trong SDOI_SOAT_CT", doiSoatCtList.size());
            
            // Nếu không có dữ liệu để đối soát thì dừng lại
            if (doiSoatCtList.isEmpty()) {
                log.warn("Không có dữ liệu SDOI_SOAT_CT để đối soát với kho bạc cho ngày: {}", request.getReconcileDate());
                return;
            }
            
            // Đối soát từng transaction từ kho bạc
            for (KbReconcileRequest.KbTransaction kbTransaction : request.getTransactions()) {
                reconcileTransaction(kbTransaction, doiSoatCtList);
            }
            
            // Xử lý các transaction thừa từ kho bạc (không tìm thấy trong hệ thống)
            handleExtraKbTransactions(request, doiSoatCtList);
            
            // Xử lý các transaction thừa từ hệ thống (không có trong JSON kho bạc)
            handleExtraSystemTransactions(request, doiSoatCtList);
            
            // Cập nhật trạng thái tổng thể của SDoiSoat
            updateSDoiSoatStatus(request, doiSoatCtList);
            
            log.info("Hoàn thành đối soát chi tiết kho bạc: {}", request.getReconcileDate());
            
        } catch (Exception e) {
            log.error("Lỗi khi thực hiện đối soát chi tiết kho bạc: ", e);
            throw new BusinessException("Lỗi khi thực hiện đối soát chi tiết kho bạc: " + e.getMessage());
        }
    }
    
    /**
     * Đối soát một transaction từ kho bạc với dữ liệu SDOI_SOAT_CT
     */
    private void reconcileTransaction(KbReconcileRequest.KbTransaction kbTransaction, 
                                    List<SDoiSoatCt> doiSoatCtList) {
        String transId = kbTransaction.getTransId();
        BigDecimal kbAmount = kbTransaction.getAmount();
        
        log.debug("Đối soát transaction kho bạc: {} - {}", transId, kbAmount);
        
        // Tìm transaction trong SDOI_SOAT_CT
        SDoiSoatCt foundTransaction = null;
        for (SDoiSoatCt ct : doiSoatCtList) {
            if (transId.equals(ct.getTransId())) {
                foundTransaction = ct;
                break;
            }
        }
        
        if (foundTransaction != null) {
            // Tìm thấy transaction - so sánh số tiền
            BigDecimal systemAmount = foundTransaction.getTongTienPhi();
            
            if (kbAmount.compareTo(systemAmount) == 0) {
                // Số tiền khớp
                foundTransaction.setKbDs("01");
                foundTransaction.setTongTienPhiKb(kbAmount);
                foundTransaction.setSoTienClKb(BigDecimal.ZERO);
                foundTransaction.setGhiChuKb("Khớp");
                log.debug("Transaction {}: Khớp số tiền", transId);
            } else {
                // Số tiền không khớp
                foundTransaction.setKbDs("02");
                foundTransaction.setTongTienPhiKb(kbAmount);    
                foundTransaction.setSoTienClKb(systemAmount.subtract(kbAmount).abs());
                foundTransaction.setGhiChuKb("Lệch số tiền "+ systemAmount.subtract(kbAmount));
                log.debug("Transaction {}: Lệch số tiền - KB: {}, Hệ thống: {}", 
                        transId, kbAmount, systemAmount);
            }
            
            // Lưu thay đổi
            sDoiSoatCtRepository.save(foundTransaction);
            
        } else {
            // Không tìm thấy transaction trong ngày này
            log.debug("Transaction {}: Không tìm thấy trong ngày", transId);
        }
    }
    
    /**
     * Cập nhật trạng thái tổng thể của SDoiSoat sau khi đối soát
     */
    private void updateSDoiSoatStatus(KbReconcileRequest request, List<SDoiSoatCt> doiSoatCtList) {
        log.info("Bắt đầu cập nhật trạng thái SDoiSoat cho kho bạc");
        
        try {
            // Lấy danh sách các SDoiSoat có LAN_DS lớn nhất theo ngày
            List<SDoiSoat> doiSoatMaxLanDs = sDoiSoatRepository.findByNgayDsAndMaxLanDs(request.getReconcileDate());
            
            // Cập nhật từng SDoiSoat có LAN_DS lớn nhất
            for (SDoiSoat doiSoat : doiSoatMaxLanDs) {
                    // Cập nhật TS_TK_DDS_KB cho từng SDoiSoat riêng biệt
                    long soTkDaDoiSoatChoDoiSoat = doiSoatCtList.stream()
                        .filter(ctItem -> ctItem.getDoiSoatId().equals(doiSoat.getId()))
                        .filter(ct -> ct.getKbDs() != null && !ct.getKbDs().trim().isEmpty())
                        .count();
                    doiSoat.setTongSoTkDdsKb((int) soTkDaDoiSoatChoDoiSoat);
                    
                    // Đếm số tờ khai thừa từ kho bạc cho DOI_SOAT_ID cụ thể
                    Long soTkThuaKb = sDoiSoatThuaRepository.countByDoiSoatIdAndNganHang(
                        doiSoat.getId(), "KB");
                    doiSoat.setTongSoTkThuaKb(soTkThuaKb.intValue());
                    
                    // Đếm số tờ khai thừa từ hệ thống (KB_DS = "03")
                    long soTkThuaHeThong = doiSoatCtList.stream()
                        .filter(ctItem -> ctItem.getDoiSoatId().equals(doiSoat.getId()))
                        .filter(ctItem -> "03".equals(ctItem.getKbDs()))
                        .count();
                    
                    // Kiểm tra nếu có transaction thừa (từ kho bạc hoặc hệ thống)
                    if (soTkThuaKb > 0 || soTkThuaHeThong > 0) {
                        // Có transaction thừa - lệch số lượng
                        doiSoat.setKbDs("02");
                        if (soTkThuaKb > 0 && soTkThuaHeThong > 0) {
                            doiSoat.setGhiChuKb("Không khớp số tờ khai");
                            log.info("SDoiSoat ID {}: Có {} tờ khai thừa từ kho bạc và {} từ hệ thống - KB_DS = 02", 
                                    doiSoat.getId(), soTkThuaKb, soTkThuaHeThong);
                        } else if (soTkThuaKb > 0) {
                            doiSoat.setGhiChuKb("Không tìm thấy giao dịch");
                            log.info("SDoiSoat ID {}: Có {} tờ khai thừa từ kho bạc - KB_DS = 02", 
                                    doiSoat.getId(), soTkThuaKb);
                        } else {
                            doiSoat.setGhiChuKb("Không tìm thấy giao dịch");
                            log.info("SDoiSoat ID {}: Có {} tờ khai thừa từ hệ thống - KB_DS = 02", 
                                    doiSoat.getId(), soTkThuaHeThong);
                        }
                    } else if (doiSoat.getTongSoTkDdsKb().equals(doiSoat.getTongSo())) {
                        // Tất cả tờ khai đã được đối soát và không có thừa
                        log.info("Tất cả tờ khai đã được đối soát cho SDoiSoat ID: {}", doiSoat.getId());
                        
                        // Kiểm tra tất cả KB_DS có phải "01" không
                        boolean tatCaKhop = doiSoatCtList.stream()
                            .filter(ctItem -> ctItem.getDoiSoatId().equals(doiSoat.getId()))
                            .allMatch(ctItem -> "01".equals(ctItem.getKbDs()));
                        
                        if (tatCaKhop) {
                            // Tất cả đều khớp
                            doiSoat.setKbDs("01");
                            doiSoat.setGhiChuKb("Đối soát thành công");
                            log.info("SDoiSoat ID {}: Tất cả đều khớp - KB_DS = 01", doiSoat.getId());
                        } else {
                            // Có ít nhất một không khớp
                            doiSoat.setKbDs("02");
                            doiSoat.setGhiChuKb("Có lệch số tiền");
                            log.info("SDoiSoat ID {}: Có lệch - KB_DS = 02", doiSoat.getId());
                        }
                    }
                    
                // Lưu thay đổi SDoiSoat
                sDoiSoatRepository.save(doiSoat);
            }
            
            log.info("Hoàn thành cập nhật trạng thái SDoiSoat cho kho bạc");
            
        } catch (Exception e) {
            log.error("Lỗi khi cập nhật trạng thái SDoiSoat cho kho bạc: ", e);
            throw new RuntimeException("Lỗi khi cập nhật trạng thái SDoiSoat cho kho bạc: " + e.getMessage(), e);
        }
    }
    
    /**
     * Xử lý các transaction thừa từ kho bạc (không tìm thấy trong hệ thống)
     */
    private void handleExtraKbTransactions(KbReconcileRequest request, List<SDoiSoatCt> doiSoatCtList) {
        log.info("Xử lý các transaction thừa từ kho bạc");
        
        try {
            // Lấy danh sách transId từ hệ thống
            Set<String> systemTransIds = doiSoatCtList.stream()
                .map(SDoiSoatCt::getTransId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
            
            // Tìm các transaction từ kho bạc không có trong hệ thống
            List<KbReconcileRequest.KbTransaction> extraTransactions = request.getTransactions().stream()
                .filter(kbTrans -> !systemTransIds.contains(kbTrans.getTransId()))
                .collect(Collectors.toList());
            
            if (!extraTransactions.isEmpty()) {
                log.warn("Tìm thấy {} transaction thừa từ kho bạc không có trong hệ thống", 
                        extraTransactions.size());
                
                // Lưu vào bảng SDOI_SOAT_THUA
                saveExtraTransactions(request, extraTransactions, doiSoatCtList);
            } else {
                log.info("Không có transaction thừa từ kho bạc");
            }
            
        } catch (Exception e) {
            log.error("Lỗi khi xử lý transaction thừa từ kho bạc: ", e);
            throw new RuntimeException("Lỗi khi xử lý transaction thừa từ kho bạc: " + e.getMessage(), e);
        }
    }
    
    /**
     * Lưu các transaction thừa từ kho bạc vào bảng SDOI_SOAT_THUA
     */
    private void saveExtraTransactions(KbReconcileRequest request, 
                                     List<KbReconcileRequest.KbTransaction> extraTransactions,
                                     List<SDoiSoatCt> doiSoatCtList) {
        try {
            // Lấy DOI_SOAT_ID từ doiSoatCtList (tất cả cùng một DOI_SOAT_ID với LAN_DS lớn nhất)
            Long doiSoatId = null;
            if (!doiSoatCtList.isEmpty()) {
                doiSoatId = doiSoatCtList.get(0).getDoiSoatId();
            }
            
            // Lưu từng transaction thừa
            for (KbReconcileRequest.KbTransaction extraTrans : extraTransactions) {
                SDoiSoatThua thuaRecord = new SDoiSoatThua();
                thuaRecord.setDoiSoatId(doiSoatId); // Sử dụng DOI_SOAT_ID có LAN_DS lớn nhất
                thuaRecord.setNgayDs(request.getReconcileDate());
                thuaRecord.setNganHang("KB"); // Kho bạc
                thuaRecord.setTransId(extraTrans.getTransId());
                thuaRecord.setToKhaiId(null); // Kho bạc không có toKhaiId
                thuaRecord.setSoTien(extraTrans.getAmount());
                thuaRecord.setTrangThai("SUCCESS"); // Mặc định thành công
                thuaRecord.setThoiGianThanhToan(null); // Kho bạc không có thời gian thanh toán
                
                sDoiSoatThuaRepository.save(thuaRecord);
            }
            
            log.info("Đã lưu {} transaction thừa từ kho bạc vào bảng SDOI_SOAT_THUA", extraTransactions.size());
            
        } catch (Exception e) {
            log.error("Lỗi khi lưu transaction thừa từ kho bạc: ", e);
            // Không throw exception để không làm gián đoạn quá trình đối soát chính
        }
    }
    
    /**
     * Xử lý các transaction thừa từ hệ thống (không có trong JSON kho bạc)
     */
    private void handleExtraSystemTransactions(KbReconcileRequest request, List<SDoiSoatCt> doiSoatCtList) {
        log.info("Xử lý các transaction thừa từ hệ thống");
        
        try {
            // Lấy danh sách transId từ kho bạc
            Set<String> kbTransIds = request.getTransactions().stream()
                .map(KbReconcileRequest.KbTransaction::getTransId)
                .collect(Collectors.toSet());
            
            // Tìm các transaction từ hệ thống không có trong JSON kho bạc
            List<SDoiSoatCt> extraSystemTransactions = doiSoatCtList.stream()
                .filter(ct -> ct.getTransId() != null && !kbTransIds.contains(ct.getTransId()))
                .collect(Collectors.toList());
            
            if (!extraSystemTransactions.isEmpty()) {
                log.warn("Tìm thấy {} transaction thừa từ hệ thống không có trong JSON kho bạc", 
                        extraSystemTransactions.size());
                
                // Cập nhật trạng thái cho các transaction thừa từ hệ thống
                for (SDoiSoatCt extraCt : extraSystemTransactions) {
                    extraCt.setKbDs("03");
                    extraCt.setGhiChuKb("Không tìm thấy giao dịch");
                    sDoiSoatCtRepository.save(extraCt);
                }
            } else {
                log.info("Không có transaction thừa từ hệ thống");
            }
            
        } catch (Exception e) {
            log.error("Lỗi khi xử lý transaction thừa từ hệ thống: ", e);
            throw new RuntimeException("Lỗi khi xử lý transaction thừa từ hệ thống: " + e.getMessage(), e);
        }
    }
}
