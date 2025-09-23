package com.pht.entity;

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
@Table(name = "S_DOI_SOAT_CT_LECH")
@Data
@EqualsAndHashCode(callSuper = false)
public class SDoiSoatCtLech {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "S_DOI_SOAT_CT_LECH_SEQ")
    @SequenceGenerator(name = "S_DOI_SOAT_CT_LECH_SEQ", sequenceName = "S_DOI_SOAT_CT_LECH_SEQ", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @Column(name = "DOI_SOAT_ID")
    private Long doiSoatId; // ID của bảng S_DOI_SOAT

    @Column(name = "DOI_SOAT_CT_ID")
    private Long doiSoatCtId; // ID của bảng S_DOI_SOAT_CT

    @Column(name = "SO_TO_KHAI", length = 50)
    private String soToKhai; // Số tờ khai

    @Column(name = "LOAI_LECH", length = 20)
    private String loaiLech; // Loại lệch (TIEN, SO_LUONG, THONG_TIN)

    @Column(name = "MO_TA_LECH", length = 500)
    private String moTaLech; // Mô tả chi tiết lệch

    @Column(name = "GIA_TRI_HE_THONG")
    private java.math.BigDecimal giaTriHeThong; // Giá trị trong hệ thống

    @Column(name = "GIA_TRI_KHO_BAC")
    private java.math.BigDecimal giaTriKhoBac; // Giá trị từ kho bạc

    @Column(name = "CHE_DO_LECH", length = 20)
    private String cheDoLech; // Chế độ lệch (THIEU, THUA, KHAC)

    @Column(name = "NGAY_TAO")
    private java.time.LocalDateTime ngayTao; // Ngày tạo

    // Relationship với SDoiSoat
    @ManyToOne
    @JoinColumn(name = "DOI_SOAT_ID", insertable = false, updatable = false)
    private SDoiSoat doiSoat;

    // Relationship với SDoiSoatCt
    @ManyToOne
    @JoinColumn(name = "DOI_SOAT_CT_ID", insertable = false, updatable = false)
    private SDoiSoatCt doiSoatCt;
}









