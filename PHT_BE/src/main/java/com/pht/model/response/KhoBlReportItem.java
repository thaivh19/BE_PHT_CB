package com.pht.model.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KhoBlReportItem {

    private String maKho;
    private BigDecimal tongTien;
    private Long soBienLai;
    private BigDecimal tyLePhanTram; // 0..100
}



