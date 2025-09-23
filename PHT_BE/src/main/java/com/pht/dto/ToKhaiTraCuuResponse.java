package com.pht.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.pht.entity.StoKhai;
import com.pht.entity.StoKhaiCt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ToKhaiTraCuuResponse {
    
    // Thông tin từ StoKhai (Tờ khai chính)
    private Long toKhaiId;
    private String soToKhai;
    private LocalDate ngayToKhai;
    private String maDoanhNghiepKhaiPhi;
    private String tenDoanhNghiepKhaiPhi;
    private String maDoanhNghiepXNK;
    private String tenDoanhNghiepXNK;
    private String soTiepNhanKhaiPhi;
    private LocalDate ngayKhaiPhi;
    private BigDecimal tongTienPhi;
    private String trangThaiNganHang;
    private String soBienLai;
    private LocalDate ngayBienLai;
    private LocalDateTime ngayTt;
    private String trangThai;
    private String transId;
    
    // Thông tin từ StoKhaiCt (Chi tiết tờ khai)
    private Long chiTietId;
    private String soVanDon;
    private String soHieu;
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
    
    /**
     * Constructor để tạo response từ StoKhai và StoKhaiCt
     */
    public ToKhaiTraCuuResponse(StoKhai stoKhai, StoKhaiCt stoKhaiCt) {
        // Thông tin từ StoKhai
        this.toKhaiId = stoKhai.getId();
        this.soToKhai = stoKhai.getSoToKhai();
        this.ngayToKhai = stoKhai.getNgayToKhai();
        this.maDoanhNghiepKhaiPhi = stoKhai.getMaDoanhNghiepKhaiPhi();
        this.tenDoanhNghiepKhaiPhi = stoKhai.getTenDoanhNghiepKhaiPhi();
        this.maDoanhNghiepXNK = stoKhai.getMaDoanhNghiepXNK();
        this.tenDoanhNghiepXNK = stoKhai.getTenDoanhNghiepXNK();
        this.soTiepNhanKhaiPhi = stoKhai.getSoTiepNhanKhaiPhi();
        this.ngayKhaiPhi = stoKhai.getNgayKhaiPhi();
        this.tongTienPhi = stoKhai.getTongTienPhi();
        this.trangThaiNganHang = stoKhai.getTrangThaiNganHang();
        this.soBienLai = stoKhai.getSoBienLai();
        this.ngayBienLai = stoKhai.getNgayBienLai();
        this.ngayTt = stoKhai.getNgayTt();
        this.trangThai = stoKhai.getTrangThai();
        this.transId = stoKhai.getTransId();
        
        // Thông tin từ StoKhaiCt
        this.chiTietId = stoKhaiCt.getId();
        this.soVanDon = stoKhaiCt.getSoVanDon();
        this.soHieu = stoKhaiCt.getSoHieu();
        this.soSeal = stoKhaiCt.getSoSeal();
        this.loaiCont = stoKhaiCt.getLoaiCont();
        this.tinhChatCont = stoKhaiCt.getTinhChatCont();
        this.maLoaiCont = stoKhaiCt.getMaLoaiCont();
        this.maTcCont = stoKhaiCt.getMaTcCont();
        this.tongTrongLuong = stoKhaiCt.getTongTrongLuong();
        this.donViTinh = stoKhaiCt.getDonViTinh();
        this.ghiChu = stoKhaiCt.getGhiChu();
        this.donGia = stoKhaiCt.getDonGia();
        this.soTien = stoKhaiCt.getSoTien();
    }
}


