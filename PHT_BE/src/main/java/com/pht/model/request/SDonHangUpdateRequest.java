package com.pht.model.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SDonHangUpdateRequest {

    private Long id;

    // Thông tin doanh nghiệp
    private String mst;
    private String tenDn;
    private String diaChi;
    private String email;
    private String sdt;

    // Thông tin đơn hàng
    private String soDonHang;
    private LocalDateTime ngayDonHang;
    private String loaiThanhToan;
    private BigDecimal tongTien;
    private String nganHang;
    private String trangThai;
    private String moTa;

    // Audit
    private String nguoiSua;

    // XML ký
    private String xmlKy;

    // Chi tiết đơn hàng
    private List<SDonHangCtUpdateRequest> chiTietList;
}
