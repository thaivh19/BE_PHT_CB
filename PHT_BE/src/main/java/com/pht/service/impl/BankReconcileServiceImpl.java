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
import com.pht.dto.BankReconcileRequest;
import com.pht.entity.SDoiSoat;
import com.pht.entity.SDoiSoatCt;
import com.pht.entity.SDoiSoatThua;
import com.pht.entity.SlogNhKb;
import com.pht.exception.BusinessException;
import com.pht.repository.SDoiSoatCtRepository;
import com.pht.repository.SDoiSoatRepository;
import com.pht.repository.SDoiSoatThuaRepository;
import com.pht.repository.SlogNhKbRepository;
import com.pht.service.BankReconcileService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class BankReconcileServiceImpl implements BankReconcileService {
    
    @Autowired
    private SlogNhKbRepository slogNhKbRepository;
    
    @Autowired
    private SDoiSoatCtRepository sDoiSoatCtRepository;
    
    @Autowired
    private SDoiSoatRepository sDoiSoatRepository;
    
    @Autowired
    private SDoiSoatThuaRepository sDoiSoatThuaRepository;
    
    private final ObjectMapper objectMapper;
    
    public BankReconcileServiceImpl() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
    
    @Override
    public void processBankReconcile(BankReconcileRequest request) throws BusinessException {
        log.info("Bắt đầu xử lý đối soát ngân hàng: {} - {}", request.getBankCode(), request.getBankName());
        
        try {
            // Validate request
            validateRequest(request);
            
            // Chuyển đổi JSON thành string
            String jsonData = convertToJsonString(request);
            
            // Tạo entity SlogNhKb
            SlogNhKb slogNhKb = new SlogNhKb();
            slogNhKb.setNgayDs(request.getReconcileDate().atStartOfDay()); // Ngày đối soát
            slogNhKb.setNgayNhan(LocalDateTime.now()); // Ngày nhận = now
            slogNhKb.setLoai("NH"); // Loại = NH (từ ngân hàng)
            slogNhKb.setNganHang(request.getBankCode()); // Ngân hàng = bankCode
            slogNhKb.setJsonData(jsonData); // JSON data
            
            // Lưu vào database
            slogNhKbRepository.save(slogNhKb);
            
            // Thực hiện đối soát chi tiết
            performDetailedReconcile(request);
            
            log.info("Lưu thành công log đối soát ngân hàng: {} - {} giao dịch, tổng tiền: {}", 
                    request.getBankCode(), request.getTotalTransaction(), request.getTotalAmount());
            
        } catch (Exception e) {
            log.error("Lỗi khi xử lý đối soát ngân hàng: ", e);
            throw new BusinessException("Lỗi khi xử lý đối soát ngân hàng: " + e.getMessage());
        }
    }
    
    private void validateRequest(BankReconcileRequest request) throws BusinessException {
        if (request == null) {
            throw new BusinessException("Request không được null");
        }
        
        if (request.getReconcileDate() == null) {
            throw new BusinessException("Ngày đối soát không được null");
        }
        
        if (request.getBankCode() == null || request.getBankCode().trim().isEmpty()) {
            throw new BusinessException("Mã ngân hàng không được null hoặc rỗng");
        }
        
        if (request.getTotalTransaction() == null || request.getTotalTransaction() <= 0) {
            throw new BusinessException("Tổng số giao dịch phải lớn hơn 0");
        }
        
        if (request.getTransactions() == null || request.getTransactions().isEmpty()) {
            throw new BusinessException("Danh sách giao dịch không được null hoặc rỗng");
        }
        
        // Validate từng transaction
        for (BankReconcileRequest.BankTransaction transaction : request.getTransactions()) {
            if (transaction.getTransId() == null || transaction.getTransId().trim().isEmpty()) {
                throw new BusinessException("Mã giao dịch không được null hoặc rỗng");
            }
            
            if (transaction.getToKhaiId() == null || transaction.getToKhaiId().trim().isEmpty()) {
                throw new BusinessException("Mã tờ khai không được null hoặc rỗng");
            }
            
            if (transaction.getAmount() == null || transaction.getAmount().compareTo(java.math.BigDecimal.ZERO) <= 0) {
                throw new BusinessException("Số tiền phải lớn hơn 0");
            }
        }
    }
    
    private String convertToJsonString(BankReconcileRequest request) throws BusinessException {
        try {
            return objectMapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            log.error("Lỗi khi chuyển đổi JSON: ", e);
            throw new BusinessException("Lỗi khi chuyển đổi JSON: " + e.getMessage());
        }
    }
    
    /**
     * Thực hiện đối soát chi tiết giữa dữ liệu từ ngân hàng và SDOI_SOAT_CT
     */
    private void performDetailedReconcile(BankReconcileRequest request) throws BusinessException {
        log.info("Bắt đầu đối soát chi tiết: {} - {}", request.getBankCode(), request.getReconcileDate());
        
        try {
            // Lấy các bản ghi SDOI_SOAT_CT chưa đối soát với ngân hàng (NH_DS = "00" hoặc NULL)
            List<SDoiSoatCt> doiSoatCtList = sDoiSoatCtRepository.findByNgayDsAndNganHangMaxLanDsAndNhDs00(
                request.getReconcileDate(), request.getBankCode());
            
            log.info("Tìm thấy {} bản ghi trong SDOI_SOAT_CT", doiSoatCtList.size());
            
            // Nếu không có dữ liệu để đối soát thì dừng lại
            if (doiSoatCtList.isEmpty()) {
                log.warn("Không có dữ liệu SDOI_SOAT_CT để đối soát với ngân hàng {} cho ngày: {}", 
                        request.getBankCode(), request.getReconcileDate());
                return;
            }
            
            // Đối soát từng transaction từ ngân hàng
            for (BankReconcileRequest.BankTransaction bankTransaction : request.getTransactions()) {
                reconcileTransaction(bankTransaction, doiSoatCtList);
            }
            
            // Xử lý các transaction thừa từ ngân hàng (không tìm thấy trong hệ thống)
            handleExtraBankTransactions(request, doiSoatCtList);
            
            // Xử lý các transaction thừa từ hệ thống (không có trong JSON ngân hàng)
            handleExtraSystemTransactions(request, doiSoatCtList);
            
            // Cập nhật trạng thái tổng thể của SDoiSoat
            updateSDoiSoatStatus(request, doiSoatCtList);
            
            log.info("Hoàn thành đối soát chi tiết: {} - {}", request.getBankCode(), request.getReconcileDate());
            
        } catch (Exception e) {
            log.error("Lỗi khi thực hiện đối soát chi tiết: ", e);
            throw new BusinessException("Lỗi khi thực hiện đối soát chi tiết: " + e.getMessage());
        }
    }
    
    /**
     * Đối soát một transaction từ ngân hàng với dữ liệu SDOI_SOAT_CT
     */
    private void reconcileTransaction(BankReconcileRequest.BankTransaction bankTransaction, 
                                    List<SDoiSoatCt> doiSoatCtList) {
        String transId = bankTransaction.getTransId();
        BigDecimal bankAmount = bankTransaction.getAmount();
        
        log.debug("Đối soát transaction: {} - {}", transId, bankAmount);
        
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
            
            if (bankAmount.compareTo(systemAmount) == 0) {
                // Số tiền khớp
                foundTransaction.setNhDs("01");
                foundTransaction.setTongTienPhiNh(bankAmount);
                foundTransaction.setSoTienClNh(BigDecimal.ZERO);
                foundTransaction.setGhiChuNh("Khớp");
                log.debug("Transaction {}: Khớp số tiền", transId);
            } else {
                // Số tiền không khớp
                foundTransaction.setNhDs("02");
                foundTransaction.setTongTienPhiNh(bankAmount);
                foundTransaction.setSoTienClNh(systemAmount.subtract(bankAmount).abs());
                foundTransaction.setGhiChuNh("Lệch số tiền "+ systemAmount.subtract(bankAmount));
                log.debug("Transaction {}: Lệch số tiền - NH: {}, Hệ thống: {}", 
                        transId, bankAmount, systemAmount);
            }
            
            // Lưu thay đổi
            sDoiSoatCtRepository.save(foundTransaction);
            
        } else {
            // Không tìm thấy transaction trong ngày/ngân hàng này
            log.debug("Transaction {}: Không tìm thấy trong ngày/ngân hàng", transId);
        }
    }
    
    /**
     * Cập nhật trạng thái tổng thể của SDoiSoat sau khi đối soát
     */
    private void updateSDoiSoatStatus(BankReconcileRequest request, List<SDoiSoatCt> doiSoatCtList) {
        log.info("Bắt đầu cập nhật trạng thái SDoiSoat");
        
        try {
            
            // Lấy danh sách các SDoiSoat có LAN_DS lớn nhất theo ngày
            List<SDoiSoat> doiSoatMaxLanDs = sDoiSoatRepository.findByNgayDsAndMaxLanDs(request.getReconcileDate());
            
            // Cập nhật từng SDoiSoat có LAN_DS lớn nhất
            for (SDoiSoat doiSoat : doiSoatMaxLanDs) {
                    // Cập nhật TS_TK_DDS_NH cho từng SDoiSoat riêng biệt
                    long soTkDaDoiSoatChoDoiSoat = doiSoatCtList.stream()
                        .filter(ctItem -> ctItem.getDoiSoatId().equals(doiSoat.getId()))
                        .filter(ct -> ct.getNhDs() != null && !ct.getNhDs().trim().isEmpty())
                        .count();
                    doiSoat.setTongSoTkDdsNh((int) soTkDaDoiSoatChoDoiSoat);
                    
                    // Đếm số tờ khai thừa từ ngân hàng cho DOI_SOAT_ID cụ thể
                    Long soTkThuaNh = sDoiSoatThuaRepository.countByDoiSoatIdAndNganHang(
                        doiSoat.getId(), request.getBankCode());
                    doiSoat.setTongSoTkThuaNh(soTkThuaNh.intValue());
                    
                    // Đếm số tờ khai thừa từ hệ thống (NH_DS = "03")
                    long soTkThuaHeThong = doiSoatCtList.stream()
                        .filter(ctItem -> ctItem.getDoiSoatId().equals(doiSoat.getId()))
                        .filter(ctItem -> "03".equals(ctItem.getNhDs()))
                        .count();
                    
                    // Kiểm tra nếu có transaction thừa (từ ngân hàng hoặc hệ thống)
                    if (soTkThuaNh > 0 || soTkThuaHeThong > 0) {
                        // Có transaction thừa - lệch số lượng
                        doiSoat.setNhDs("02");
                        if (soTkThuaNh > 0 && soTkThuaHeThong > 0) {
                            doiSoat.setGhiChuNh("Không khớp số tờ khai");
                            log.info("SDoiSoat ID {}: Có {} tờ khai thừa từ ngân hàng và {} từ hệ thống - NH_DS = 02", 
                                    doiSoat.getId(), soTkThuaNh, soTkThuaHeThong);
                        } else if (soTkThuaNh > 0) {
                            doiSoat.setGhiChuNh("Không tìm thấy giao dịch");
                            log.info("SDoiSoat ID {}: Có {} tờ khai thừa từ ngân hàng - NH_DS = 02", 
                                    doiSoat.getId(), soTkThuaNh);
                        } else {
                            doiSoat.setGhiChuNh("Không tìm thấy giao dịch");
                            log.info("SDoiSoat ID {}: Có {} tờ khai thừa từ hệ thống - NH_DS = 02", 
                                    doiSoat.getId(), soTkThuaHeThong);
                        }
                    } else if (doiSoat.getTongSoTkDdsNh().equals(doiSoat.getTongSo())) {
                        // Tất cả tờ khai đã được đối soát và không có thừa
                        log.info("Tất cả tờ khai đã được đối soát cho SDoiSoat ID: {}", doiSoat.getId());
                        
                        // Kiểm tra tất cả NH_DS có phải "01" không
                        boolean tatCaKhop = doiSoatCtList.stream()
                            .filter(ctItem -> ctItem.getDoiSoatId().equals(doiSoat.getId()))
                            .allMatch(ctItem -> "01".equals(ctItem.getNhDs()));
                        
                        if (tatCaKhop) {
                            // Tất cả đều khớp
                            doiSoat.setNhDs("01");
                            doiSoat.setGhiChuNh("Đối soát thành công");
                            log.info("SDoiSoat ID {}: Tất cả đều khớp - NH_DS = 01", doiSoat.getId());
                        } else {
                            // Có ít nhất một không khớp
                            doiSoat.setNhDs("02");
                            doiSoat.setGhiChuNh("Có lệch số tiền");
                            log.info("SDoiSoat ID {}: Có lệch - NH_DS = 02", doiSoat.getId());
                        }
                    } else {
                        // Chưa đối soát hết
                        doiSoat.setNhDs("99");
                        doiSoat.setGhiChuNh("Chưa đối soát hết");
                        log.info("SDoiSoat ID {}: Chưa đối soát hết - TS_TK_DDS_NH: {}, TONG_SO: {}, NH_DS = 99", 
                                doiSoat.getId(), doiSoat.getTongSoTkDdsNh(), doiSoat.getTongSo());
                    }
                    
                // Lưu thay đổi SDoiSoat
                sDoiSoatRepository.save(doiSoat);
            }
            
            log.info("Hoàn thành cập nhật trạng thái SDoiSoat");
            
        } catch (Exception e) {
            log.error("Lỗi khi cập nhật trạng thái SDoiSoat: ", e);
            throw new RuntimeException("Lỗi khi cập nhật trạng thái SDoiSoat: " + e.getMessage(), e);
        }
    }
    
    /**
     * Xử lý các transaction thừa từ ngân hàng (không tìm thấy trong hệ thống)
     */
    private void handleExtraBankTransactions(BankReconcileRequest request, List<SDoiSoatCt> doiSoatCtList) {
        log.info("Xử lý các transaction thừa từ ngân hàng");
        
        try {
            // Lấy danh sách transId từ hệ thống
            Set<String> systemTransIds = doiSoatCtList.stream()
                .map(SDoiSoatCt::getTransId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
            
            // Tìm các transaction từ ngân hàng không có trong hệ thống
            List<BankReconcileRequest.BankTransaction> extraTransactions = request.getTransactions().stream()
                .filter(bankTrans -> !systemTransIds.contains(bankTrans.getTransId()))
                .collect(Collectors.toList());
            
            if (!extraTransactions.isEmpty()) {
                log.warn("Tìm thấy {} transaction thừa từ ngân hàng {} không có trong hệ thống", 
                        extraTransactions.size(), request.getBankCode());
                
                
                // Lưu vào bảng SDOI_SOAT_THUA
                saveExtraTransactions(request, extraTransactions, doiSoatCtList);
            } else {
                log.info("Không có transaction thừa từ ngân hàng");
            }
            
        } catch (Exception e) {
            log.error("Lỗi khi xử lý transaction thừa từ ngân hàng: ", e);
            throw new RuntimeException("Lỗi khi xử lý transaction thừa từ ngân hàng: " + e.getMessage(), e);
        }
    }
    
    /**
     * Lưu các transaction thừa từ ngân hàng vào bảng SDOI_SOAT_THUA
     */
    private void saveExtraTransactions(BankReconcileRequest request, 
                                     List<BankReconcileRequest.BankTransaction> extraTransactions,
                                     List<SDoiSoatCt> doiSoatCtList) {
        try {
            // Lấy DOI_SOAT_ID từ doiSoatCtList (tất cả cùng một DOI_SOAT_ID với LAN_DS lớn nhất)
            Long doiSoatId = null;
            if (!doiSoatCtList.isEmpty()) {
                doiSoatId = doiSoatCtList.get(0).getDoiSoatId();
            }
            
            // Lưu từng transaction thừa
            for (BankReconcileRequest.BankTransaction extraTrans : extraTransactions) {
                SDoiSoatThua thuaRecord = new SDoiSoatThua();
                thuaRecord.setDoiSoatId(doiSoatId); // Sử dụng DOI_SOAT_ID có LAN_DS lớn nhất
                thuaRecord.setNgayDs(request.getReconcileDate());
                thuaRecord.setNganHang(request.getBankCode());
                thuaRecord.setTransId(extraTrans.getTransId());
                thuaRecord.setToKhaiId(extraTrans.getToKhaiId());
                thuaRecord.setSoTien(extraTrans.getAmount());
                thuaRecord.setTrangThai(extraTrans.getStatus());
                thuaRecord.setThoiGianThanhToan(extraTrans.getPayTime());
                
                sDoiSoatThuaRepository.save(thuaRecord);
            }
            
            log.info("Đã lưu {} transaction thừa từ ngân hàng vào bảng SDOI_SOAT_THUA", extraTransactions.size());
            
        } catch (Exception e) {
            log.error("Lỗi khi lưu transaction thừa: ", e);
            // Không throw exception để không làm gián đoạn quá trình đối soát chính
        }
    }
    
    /**
     * Xử lý các transaction thừa từ hệ thống (không có trong JSON ngân hàng)
     */
    private void handleExtraSystemTransactions(BankReconcileRequest request, List<SDoiSoatCt> doiSoatCtList) {
        log.info("Xử lý các transaction thừa từ hệ thống");
        
        try {
            // Lấy danh sách transId từ ngân hàng
            Set<String> bankTransIds = request.getTransactions().stream()
                .map(BankReconcileRequest.BankTransaction::getTransId)
                .collect(Collectors.toSet());
            
            // Tìm các transaction từ hệ thống không có trong JSON ngân hàng
            List<SDoiSoatCt> extraSystemTransactions = doiSoatCtList.stream()
                .filter(ct -> ct.getTransId() != null && !bankTransIds.contains(ct.getTransId()))
                .collect(Collectors.toList());
            
            if (!extraSystemTransactions.isEmpty()) {
                log.warn("Tìm thấy {} transaction thừa từ hệ thống không có trong JSON ngân hàng {}", 
                        extraSystemTransactions.size(), request.getBankCode());
                
                // Cập nhật trạng thái cho các transaction thừa từ hệ thống
                for (SDoiSoatCt extraCt : extraSystemTransactions) {
                    extraCt.setNhDs("03");
                    extraCt.setGhiChuNh("Không tìm thấy giao dịch");
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
