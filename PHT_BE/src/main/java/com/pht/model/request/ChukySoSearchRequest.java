package com.pht.model.request;

import lombok.Data;

@Data
public class ChukySoSearchRequest {

    private String serialNumber;

    private String maDoanhNghiep;

    private String tenDoanhNghiep;

    private String maSoThue;

    private String trangThai;

    private String loaiChuKy;

    private Boolean isActive;

    private Boolean isDefault;

    private String subject;

    private String issuer;

    // Pagination
    private Integer page = 0;
    private Integer size = 10;
    private String sortBy = "id";
    private String sortDir = "desc";
}
