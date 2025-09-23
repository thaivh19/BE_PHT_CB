package com.pht.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "SBIEU_CUOC")
@Data
@EqualsAndHashCode(callSuper = false)
public class SbieuCuoc {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_dm_bc")
    @SequenceGenerator(name = "seq_dm_bc", sequenceName = "seq_dm_bc", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @Column(name = "MA_BIEU_CUOC", length = 50)
    private String maBieuCuoc;

    @Column(name = "TEN_BIEU_CUOC", length = 255)
    private String tenBieuCuoc;

    @Column(name = "MA_NHOM_LOAI_HINH", length = 100)
    private String maNhomLoaiHinh;

    @Column(name = "NHOM_LOAI_HINH", length = 100)
    private String nhomLoaiHinh;

    @Column(name = "LOAI_CONT", length = 100)
    private String loaiCont;

    @Column(name = "TINH_CHAT_CONT", length = 100)
    private String tinhChatCont;

    @Column(name = "DVT", length = 50)
    private String dvt;

    @Column(name = "HANG", length = 255)
    private String hang;

    @Column(name = "DON_GIA", precision = 18, scale = 2)
    private BigDecimal donGia;

    @Column(name = "LOAI_BC", length = 100)
    private String loaiBc;

    @Column(name = "MA_LOAI_CONT", length = 100)
    private String maLoaiCont;

    @Column(name = "MA_TC_CONT", length = 100)
    private String maTcCont;

    @Column(name = "TRANG_THAI", length = 50)
    private String trangThai;

    @Column(name = "NGAY_TAO")
    private LocalDateTime ngayTao;

    @Column(name = "NGAY_CAP_NHAT")
    private LocalDateTime ngayCapNhat;
}