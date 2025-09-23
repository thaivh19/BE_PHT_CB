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
@Table(name = "SHAIQUAN")
@Data
@EqualsAndHashCode(callSuper = false)
public class ShaiQuan {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_dm_hq")
    @SequenceGenerator(name = "seq_dm_hq", sequenceName = "seq_dm_hq", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @Column(name = "Ma_HQ", length = 50)
    private String maHq;

    @Column(name = "Ten_HQ", length = 255)
    private String tenHq;

    @Column(name = "DIEN_GIAI", length = 255)
    private String dienGiai;

    @Column(name = "TRANG_THAI", length = 50)
    private String trangThai;

    @Column(name = "NGAY_TAO")
    private LocalDateTime ngayTao;

    @Column(name = "NGAY_CAP_NHAT")
    private LocalDateTime ngayCapNhat;
}
