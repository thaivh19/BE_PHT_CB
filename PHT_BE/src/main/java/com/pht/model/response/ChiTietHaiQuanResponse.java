package com.pht.model.response;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ChiTietHaiQuanResponse {
    
    private Long id;
    private Long toKhaiThongTinID;
    private String soVanDon;
    
    // Thông tin chung
    private String soHieu; // số vận đơn / số container / mã hàng
    private String soSeal;
    private String maLoaiCont; // MA_LOAI_CONT từ SBIEU_CUOC
    private String maTcCont;   // MA_TC_CONT từ SBIEU_CUOC
    private BigDecimal tongTrongLuong;
    private String donViTinh;
    private String ghiChu;
}
