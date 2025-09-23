package com.pht.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
public class SDonHangSearchRequest {

    private String mst;
    private String tenDn;
    private String soDonHang;
    private String loaiThanhToan;
    private String trangThai;
    private String nganHang;
}
