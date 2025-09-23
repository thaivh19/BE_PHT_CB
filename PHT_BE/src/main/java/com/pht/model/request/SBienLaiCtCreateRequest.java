package com.pht.model.request;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
public class SBienLaiCtCreateRequest {

    private String ndungTp;
    private String dvt;
    private BigDecimal soLuong;
    private BigDecimal donGia;
    private BigDecimal soTien;
}



