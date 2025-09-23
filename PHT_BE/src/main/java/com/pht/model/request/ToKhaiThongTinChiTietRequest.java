package com.pht.model.request;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ToKhaiThongTinChiTietRequest {

    private String soVanDon;

    // Thông tin chung
    private String soHieu; // số vận đơn / số container / mã hàng
    private String soSeal;
    private String loaiCont;
    private String tinhChatCont;
    private String maLoaiCont;
    private String maTcCont;
    private BigDecimal tongTrongLuong;
    private String donViTinh;
    private String ghiChu;
    private BigDecimal donGia;
    private BigDecimal soTien;
}
