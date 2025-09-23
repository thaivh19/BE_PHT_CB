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
@Table(name = "SDVT")
@Data
@EqualsAndHashCode(callSuper = false)
public class Sdvt {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_dm_dvt")
    @SequenceGenerator(name = "seq_dm_dvt", sequenceName = "seq_dm_dvt", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @Column(name = "MA_DVT", length = 50)
    private String maDvt;

    @Column(name = "TEN_DVT", length = 255)
    private String tenDvt;

    @Column(name = "LOAI_DVT", length = 100)
    private String loaiDvt;

    @Column(name = "DIEN_GIAI", length = 500)
    private String dienGiai;

    @Column(name = "TRANG_THAI", length = 50)
    private String trangThai;

    @Column(name = "NGAY_TAO")
    private LocalDateTime ngayTao;

    @Column(name = "NGAY_CAP_NHAT")
    private LocalDateTime ngayCapNhat;

    @Column(name = "CV_TON", precision = 10, scale = 6)
    private java.math.BigDecimal cvTon;
}