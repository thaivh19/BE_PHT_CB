package com.pht.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "SDON_HANG")
public class SDonHang {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sdon_hang_seq")
    @SequenceGenerator(name = "sdon_hang_seq", sequenceName = "SDON_HANG_SEQ", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    // Thông tin doanh nghiệp
    @Column(name = "MST", length = 20)
    private String mst;

    @Column(name = "TEN_DN", length = 500)
    private String tenDn;

    @Column(name = "DIA_CHI", length = 1000)
    private String diaChi;

    @Column(name = "EMAIL", length = 200)
    private String email;

    @Column(name = "SDT", length = 20)
    private String sdt;

    // Thông tin đơn hàng
    @Column(name = "SO_DON_HANG", length = 100)
    private String soDonHang;

    @Column(name = "NGAY_DON_HANG")
    private LocalDateTime ngayDonHang;

    @Column(name = "LOAI_THANH_TOAN", length = 100)
    private String loaiThanhToan;

    @Column(name = "TONG_TIEN", precision = 18, scale = 2)
    private java.math.BigDecimal tongTien;

    @Column(name = "NGAN_HANG", length = 200)
    private String nganHang;

    @Column(name = "TRANG_THAI", length = 50)
    private String trangThai;

    @Column(name = "MO_TA", columnDefinition = "TEXT")
    private String moTa;

    // Audit fields
    @Column(name = "NGAY_TAO")
    private LocalDateTime ngayTao;

    @Column(name = "NGUOI_TAO", length = 100)
    private String nguoiTao;

    @Column(name = "NGAY_SUA")
    private LocalDateTime ngaySua;

    @Column(name = "NGUOI_SUA", length = 100)
    private String nguoiSua;

    @Column(name = "XML_KY", columnDefinition = "TEXT")
    private String xmlKy;

    // Relationship - Chi tiết đơn hàng (nếu cần)
    @OneToMany(mappedBy = "donHang", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<SDonHangCt> chiTietList;
}
