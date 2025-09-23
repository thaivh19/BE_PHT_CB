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
public class SbieuCuocUpdateRequest {

    private Long id;
    private String maBieuCuoc;
    private String tenBieuCuoc;
    private String nhomLoaiHinh;
    private String loaiCont;
    private String tinhChatCont;
    private String dvt;
    private String hang;
    private BigDecimal donGia;
    private String loaiBc;
    private String maLoaiCont;
    private String maTcCont;
    private String trangThai;
}
