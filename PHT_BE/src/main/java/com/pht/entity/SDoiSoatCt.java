package com.pht.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "SDOI_SOAT_CT")
@Data
@EqualsAndHashCode(callSuper = false)
public class SDoiSoatCt {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "S_DOI_SOAT_CT_SEQ")
    @SequenceGenerator(name = "S_DOI_SOAT_CT_SEQ", sequenceName = "S_DOI_SOAT_CT_SEQ", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @Column(name = "DOI_SOAT_ID")
    private Long doiSoatId; // ID của bảng S_DOI_SOAT

    @Column(name = "STO_KHAI_ID")
    private Long stoKhaiId; // ID của bảng STO_KHAI

    @Column(name = "SO_TK", length = 50)
    private String soToKhai; // Số tờ khai

    @Column(name = "NGAY_TK")
    private java.time.LocalDate ngayToKhai; // Ngày tờ khai

    @Column(name = "SO_TN_KP", length = 50)
    private String soTnKp; // Số tiếp nhận khai phí

    @Column(name = "NGAY_TN_KP")
    private java.time.LocalDate ngayTnKp; // Ngày tiếp nhận khai phí

    @Column(name = "MA_DOANH_NGHIEP", length = 20)
    private String maDoanhNghiep; // Mã doanh nghiệp

    @Column(name = "TEN_DOANH_NGHIEP", length = 255)
    private String tenDoanhNghiep; // Tên doanh nghiệp

    @Column(name = "TONG_TIEN_PHI")
    private java.math.BigDecimal tongTienPhi; // Tổng tiền phí

    @Column(name = "TRANS_ID", length = 100)
    private String transId; // Transaction ID từ ngân hàng

    @Column(name = "NGAN_HANG", length = 200)
    private String nganHang; // Thông tin ngân hàng

    @Column(name = "NH_DS", length = 200)
    private String nhDs; // Ngân hàng đối soát

    @Column(name = "KB_DS", length = 200)
    private String kbDs; // Kho bạc đối soát

    @Column(name = "GHI_CHU", length = 500)
    private String ghiChu; // Ghi chú

    // Relationship với SDoiSoat
    @ManyToOne
    @JoinColumn(name = "DOI_SOAT_ID", insertable = false, updatable = false)
    @JsonBackReference
    private SDoiSoat doiSoat;
}
