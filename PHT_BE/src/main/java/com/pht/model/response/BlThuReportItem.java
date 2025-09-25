package com.pht.model.response;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlThuReportItem {

    private String mst;
    private String tenDvi;
    private LocalDate ngay;
    private BigDecimal tongTien;
    private Long soBienLai;
}


