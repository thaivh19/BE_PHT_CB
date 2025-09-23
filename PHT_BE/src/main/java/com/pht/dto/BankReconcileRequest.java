package com.pht.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class BankReconcileRequest {
    
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate reconcileDate; // Ngày đối soát
    private String bankCode; // Mã ngân hàng
    private String bankName; // Tên ngân hàng
    private String unitCode; // Mã đơn vị
    private String unitName; // Tên đơn vị
    private Integer totalTransaction; // Tổng số giao dịch
    private BigDecimal totalAmount; // Tổng số tiền
    private List<BankTransaction> transactions; // Danh sách giao dịch
    
    @Data
    public static class BankTransaction {
        private String transId; // Mã giao dịch
        private String toKhaiId; // Mã tờ khai
        private BigDecimal amount; // Số tiền
        private String status; // Trạng thái (SUCCESS/FAILED)
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime payTime; // Thời gian thanh toán
        private String sendToKBNNStatus; // Trạng thái gửi KB (SENT_SUCCESS/SENT_FAIL/NOT_SENT)
        private String remark; // Ghi chú
    }
}
