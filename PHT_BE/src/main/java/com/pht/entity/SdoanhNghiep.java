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
@Table(name = "SDOANH_NGHIEP")
@Data
@EqualsAndHashCode(callSuper = false)
public class SdoanhNghiep {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_dm_dn")
    @SequenceGenerator(name = "seq_dm_dn", sequenceName = "seq_dm_dn", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @Column(name = "MA_DN", length = 50)
    private String maDn;

    @Column(name = "TEN_DN", length = 255)
    private String tenDn;

    @Column(name = "DIEN_GIAI", length = 500)
    private String dienGiai;

    @Column(name = "TRANG_THAI", length = 50)
    private String trangThai;

    @Column(name = "NGAY_TAO")
    private LocalDateTime ngayTao;

    @Column(name = "NGAY_CAP_NHAT")
    private LocalDateTime ngayCapNhat;
}
