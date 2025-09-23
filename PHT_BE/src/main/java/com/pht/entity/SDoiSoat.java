package com.pht.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "SDOI_SOAT")
@Data
@EqualsAndHashCode(callSuper = false)
public class SDoiSoat {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SDOI_SOAT_SEQ")
    @SequenceGenerator(name = "SDOI_SOAT_SEQ", sequenceName = "SDOI_SOAT_SEQ", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @Column(name = "PKG_ID", length = 50)
    private String pkgId; // Package ID

    @Column(name = "LAN_DS")
    private Integer lanDs; // Lần đối soát

    @Column(name = "SO_BK", length = 50)
    private String soBk; // Số bảng kê

    @Column(name = "NGAY_BK")
    private java.time.LocalDate ngayBk; // Ngày bảng kê

    @Column(name = "NGAY_DS")
    private java.time.LocalDate ngayDs; // Ngày đối soát

    @Column(name = "TONG_SO")
    private Integer tongSo; // Tổng số tờ khai

    @Column(name = "TONG_TIEN")
    private java.math.BigDecimal tongTien; // Tổng tiền

    @Column(name = "TRANG_THAI", length = 2)
    private String trangThai; // Trạng thái


    @Column(name = "NH_DS", length = 200)
    private String nhDs; // Ngân hàng đối soát

    @Column(name = "KB_DS", length = 200)
    private String kbDs; // Kho bạc đối soát

    @Column(name = "TS_TK_DDS_NH")
    private Integer tongSoTkDdsNh; // Tổng số tờ khai đã đối soát với ngân hàng

    @Column(name = "TS_TK_DDS_KB")
    private Integer tongSoTkDdsKb; // Tổng số tờ khai đã đối soát với kho bạc

    @Column(name = "TS_TK_THUA_NH")
    private Integer tongSoTkThuaNh; // Tổng số tờ khai thừa từ ngân hàng

    @Column(name = "TS_TK_THUA_KB")
    private Integer tongSoTkThuaKb; // Tổng số tờ khai thừa từ kho bạc

    @Column(name = "GHI_CHU_NH", length = 500)
    private String ghiChuNh; // Ghi chú mô tả từ ngân hàng

    @Column(name = "GHI_CHU_KB", length = 500)
    private String ghiChuKb; // Ghi chú mô tả từ kho bạc

    // Relationship với chi tiết đối soát
    @OneToMany(mappedBy = "doiSoat", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<SDoiSoatCt> chiTietList;
}
