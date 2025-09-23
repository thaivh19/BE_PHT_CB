package com.pht.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class KbReconcileRequest {
    
    @NotNull
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate reconcileDate; // Ngày đối soát
    
    @NotNull
    private Integer totalTransaction; // Tổng số giao dịch
    
    @NotNull
    private BigDecimal totalAmount; // Tổng số tiền
    
    @NotNull
    private List<KbTransaction> transactions; // Danh sách giao dịch
    
    @Data
    public static class KbTransaction {
        @NotNull
        private String transId; // Mã giao dịch
        
        @NotNull
        private BigDecimal amount; // Số tiền
    }
}
