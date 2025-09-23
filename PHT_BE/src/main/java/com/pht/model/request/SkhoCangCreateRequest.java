package com.pht.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
public class SkhoCangCreateRequest {

    private String ma;
    private String ten;
    private String maCk;
    private String maHq;
    private String diaChi;
    private String ghiChu;
    private String trangThai;
}
