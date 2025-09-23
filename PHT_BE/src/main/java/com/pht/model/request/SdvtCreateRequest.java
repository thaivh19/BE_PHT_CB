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
public class SdvtCreateRequest {

    private String maDvt;
    private String tenDvt;
    private String loaiDvt;
    private String dienGiai;
    private String trangThai;
    private BigDecimal cvTon;
}
