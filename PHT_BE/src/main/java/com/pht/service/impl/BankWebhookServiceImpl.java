package com.pht.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pht.entity.SDonHang;
import com.pht.entity.SDonHangCt;
import com.pht.model.request.BankWebhookRequest;
import com.pht.model.response.BankWebhookResponse;
import com.pht.repository.SDonHangRepository;
import com.pht.repository.ToKhaiThongTinRepository;
import com.pht.service.BankWebhookService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class BankWebhookServiceImpl implements BankWebhookService {

    @Autowired
    private ToKhaiThongTinRepository toKhaiThongTinRepository;
    
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SDonHangRepository sDonHangRepository;

    @Override
    public BankWebhookResponse processPaymentNotification(BankWebhookRequest request) {
        log.info("Nhận webhook từ ngân hàng: transId={}, amount={}, remark={}", 
                request.getTransId(), request.getAmount(), request.getRemark());
        
        try {
            // Chuyển đổi request thành JSON string để lưu vào tvsd_json
            String tvsdJson = objectMapper.writeValueAsString(request);
            log.info("Đã chuyển đổi request thành JSON: {}", tvsdJson);
            
            // Lấy số đơn hàng từ remark
            String soDonHang = extractSoDonHangFromRemark(request.getRemark());
            log.info("Bóc tách remark: soDonHang={}", soDonHang);

            // Tìm đơn hàng theo số đơn hàng
            SDonHang donHang = sDonHangRepository.findBySoDonHang(soDonHang);
            if (donHang == null) {
                log.warn("Không tìm thấy đơn hàng với soDonHang={}", soDonHang);
                return createErrorResponse(request.getTransId(), request.getProviderId(), "01", "Không tìm thấy đơn hàng");
            }

            // Parse transTime và lưu vào ngay_tt
            LocalDateTime ngayThanhToan = parseTransTime(request.getTransTime());

            // Cập nhật tất cả tờ khai liên quan (từ chi tiết đơn hàng) TT_NH = "02"
            if (donHang.getChiTietList() != null && !donHang.getChiTietList().isEmpty()) {
                List<Long> toKhaiIds = donHang.getChiTietList().stream()
                        .map(SDonHangCt::getIdTokhai)
                        .filter(Objects::nonNull)
                        .distinct()
                        .toList();

                if (!toKhaiIds.isEmpty()) {
                    toKhaiThongTinRepository.findAllById(toKhaiIds).forEach(tk -> {
                        tk.setTvsdJson(tvsdJson);
                        tk.setTransId(request.getTransId());
                        tk.setTrangThai("04"); // Đã thanh toán
                        tk.setTrangThaiNganHang("02"); // TTNH = 02
                        tk.setNgayTt(ngayThanhToan);
                    });
                    toKhaiThongTinRepository.flush();
                }
            }

            // Cập nhật trạng thái đơn hàng thành "01"
            donHang.setTrangThai("01");
            sDonHangRepository.save(donHang);

            log.info("✅ Đã cập nhật trạng thái TT_NH=02 cho các tờ khai liên quan và trạng thái đơn hàng=01 cho soDonHang={}", soDonHang);

            return createSuccessResponse(request.getTransId(), request.getProviderId());
            
        } catch (Exception e) {
            log.error("Lỗi khi xử lý webhook từ ngân hàng: ", e);
            return createErrorResponse(request.getTransId(), request.getProviderId(), "99", "Lỗi hệ thống: " + e.getMessage());
        }
    }

    @Override
    public java.util.List<BankWebhookResponse> simulatePaymentForOrders(java.util.List<String> soDonHangList) {
        java.util.List<BankWebhookResponse> responses = new java.util.ArrayList<>();

        // Nếu không truyền danh sách, tự tìm các đơn có tờ khai TT_NH = "00"
        if (soDonHangList == null || soDonHangList.isEmpty()) {
            List<SDonHang> orders = sDonHangRepository.findOrdersHavingTokhaiTrangThaiNganHang("00");
            soDonHangList = orders.stream().map(SDonHang::getSoDonHang).distinct().toList();
        }

        for (String soDonHang : soDonHangList) {
            try {
                // Tạo request giả lập
                BankWebhookRequest req = new BankWebhookRequest();
                req.setMsgId("SIM-" + System.currentTimeMillis());
                req.setProviderId("SIM");
                req.setTransId("SIMTX-" + soDonHang + "-" + System.nanoTime());
                req.setTransTime(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(java.time.LocalDateTime.now()));
                req.setTransType("PAYMENT");
                req.setAmount("100000");
                req.setRemark(soDonHang); // remark là số đơn hàng
                req.setCurrencyCode("VND");
                req.setSignature("");

                // Gọi xử lý như webhook thật
                BankWebhookResponse resp = processPaymentNotification(req);
                responses.add(resp);
            } catch (Exception e) {
                BankWebhookResponse error = new BankWebhookResponse();
                error.setTransId(soDonHang);
                error.setProviderId("SIM");
                error.setErrorCode("99");
                error.setErrorDesc("Simulate error: " + e.getMessage());
                error.setSignature("");
                responses.add(error);
            }
        }
        return responses;
    }
    
    /**
     * Bóc tách remark để lấy mã doanh nghiệp và số tờ khai
     * Format: "maDoanhNghiep_soToKhai"
     */
    private String extractSoDonHangFromRemark(String remark) {
        if (remark == null || remark.trim().isEmpty()) {
            throw new IllegalArgumentException("Remark không được để trống");
        }
        return remark.trim();
    }
    
    /**
     * Tạo response thành công
     */
    private BankWebhookResponse createSuccessResponse(String transId, String providerId) {
        BankWebhookResponse response = new BankWebhookResponse();
        response.setTransId(transId);
        response.setProviderId(providerId);
        response.setErrorCode("00");
        response.setErrorDesc("Thanh cong");
        response.setSignature(""); // TODO: Implement signature generation
        return response;
    }
    
    /**
     * Parse transTime từ định dạng yyyyMMddhhmmss sang LocalDateTime
     * Ví dụ: "20241216140713" -> LocalDateTime(2024-12-16T14:07:13)
     */
    private LocalDateTime parseTransTime(String transTime) {
        log.info("Bắt đầu parse transTime: '{}', độ dài: {}", transTime, transTime != null ? transTime.length() : "null");
        
        if (transTime == null) {
            log.warn("TransTime là null, sử dụng thời gian hiện tại");
            return LocalDateTime.now();
        }
        
        if (transTime.trim().isEmpty()) {
            log.warn("TransTime là chuỗi rỗng, sử dụng thời gian hiện tại");
            return LocalDateTime.now();
        }
        
        if (transTime.length() != 14) {
            log.warn("TransTime không đúng độ dài (cần 14 ký tự): '{}', độ dài: {}, sử dụng thời gian hiện tại", 
                    transTime, transTime.length());
            return LocalDateTime.now();
        }
        
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            LocalDateTime parsedDateTime = LocalDateTime.parse(transTime, formatter);
            
            log.info("✅ Đã parse transTime thành công: '{}' -> {}", transTime, parsedDateTime);
            return parsedDateTime;
            
        } catch (Exception e) {
            log.error("❌ Lỗi khi parse transTime: '{}', sử dụng thời gian hiện tại. Lỗi: {}", transTime, e.getMessage());
            return LocalDateTime.now();
        }
    }
    
    /**
     * Tạo response lỗi
     */
    private BankWebhookResponse createErrorResponse(String transId, String providerId, String errorCode, String errorDesc) {
        BankWebhookResponse response = new BankWebhookResponse();
        response.setTransId(transId);
        response.setProviderId(providerId);
        response.setErrorCode(errorCode);
        response.setErrorDesc(errorDesc);
        response.setSignature(""); // TODO: Implement signature generation
        return response;
    }
}
