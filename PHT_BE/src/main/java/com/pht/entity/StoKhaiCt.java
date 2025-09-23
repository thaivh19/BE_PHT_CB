package com.pht.entity;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name = "STO_KHAI_CT")
@Data
@EqualsAndHashCode(callSuper = false)
@ToString(exclude = "stoKhai")
public class StoKhaiCt {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "STO_KHAI_CT_SEQ")
    @SequenceGenerator(name = "STO_KHAI_CT_SEQ", sequenceName = "STO_KHAI_CT_SEQ", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @Column(name = "TK_ID", nullable = false)
    private Long toKhaiThongTinID;

    @Column(name = "SO_VANDON", nullable = false, length = 100)
    private String soVanDon;

    // Thông tin chung
    @Column(name = "SO_HIEU", length = 100)
    private String soHieu; // số vận đơn / số container / mã hàng

    @Column(name = "SO_SEAL", length = 100)
    private String soSeal;

    @Column(name = "LOAI_CONT", length = 255)
    private String loaiCont;

    @Column(name = "TINHCHAT_CONT", length = 255)
    private String tinhChatCont;

    @Column(name = "MA_LOAI_CONT", length = 100)
    private String maLoaiCont;

    @Column(name = "MA_TC_CONT", length = 100)
    private String maTcCont;

    @Column(name = "TONG_TL", precision = 18, scale = 2)
    private BigDecimal tongTrongLuong;

    @Column(name = "DVI_TINH", length = 20)
    private String donViTinh;

    @Column(name = "GHI_CHU", length = 500)
    private String ghiChu;

    @Column(name = "DON_GIA", precision = 18, scale = 2)
    private BigDecimal donGia;

    @Column(name = "SO_TIEN", precision = 18, scale = 2)
    private BigDecimal soTien;

    // Relationship
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TK_ID", insertable = false, updatable = false)
    @JsonBackReference
    private StoKhai stoKhai;
}
