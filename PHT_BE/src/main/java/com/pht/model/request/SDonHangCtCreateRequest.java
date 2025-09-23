package com.pht.model.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SDonHangCtCreateRequest {

    private Long idTokhai;
    private String soThongBao;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate ngayThongBao;
    
    private BigDecimal thanhTien;
}
