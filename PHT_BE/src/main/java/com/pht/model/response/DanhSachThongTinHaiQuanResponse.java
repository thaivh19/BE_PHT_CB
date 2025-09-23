package com.pht.model.response;

import java.util.List;

import lombok.Data;

@Data
public class DanhSachThongTinHaiQuanResponse {
    
    private List<ThongTinHaiQuanResponse> danhSachToKhai;
    private Integer tongSoToKhai;
    private String trangThai;
    private String thongBao;
    
    public DanhSachThongTinHaiQuanResponse() {
        this.trangThai = "00";
        this.thongBao = "Thành công";
    }
    
    public DanhSachThongTinHaiQuanResponse(List<ThongTinHaiQuanResponse> danhSachToKhai) {
        this.danhSachToKhai = danhSachToKhai;
        this.tongSoToKhai = danhSachToKhai != null ? danhSachToKhai.size() : 0;
        this.trangThai = "00";
        this.thongBao = "Thành công";
    }
}









