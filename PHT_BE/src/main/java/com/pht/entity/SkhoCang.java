package com.pht.entity;

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
@Table(name = "SKHO_CANG")
@Data
@EqualsAndHashCode(callSuper = false)
public class SkhoCang {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_dm_kc")
    @SequenceGenerator(name = "seq_dm_kc", sequenceName = "seq_dm_kc", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @Column(name = "MA", length = 50)
    private String ma;

    @Column(name = "TEN", length = 255)
    private String ten;

    @Column(name = "MA_CK", length = 50)
    private String maCk;

    @Column(name = "MA_HQ", length = 50)
    private String maHq;

    @Column(name = "DIA_CHI", length = 500)
    private String diaChi;

    @Column(name = "GHI_CHU", length = 500)
    private String ghiChu;

    @Column(name = "TRANG_THAI", length = 50)
    private String trangThai;

    @Column(name = "NGAY_TAO")
    private LocalDateTime ngayTao;

    @Column(name = "NGAY_CAP_NHAT")
    private LocalDateTime ngayCapNhat;
}
