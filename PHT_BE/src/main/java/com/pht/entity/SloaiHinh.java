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
@Table(name = "SLOAI_HINH")
@Data
@EqualsAndHashCode(callSuper = false)
public class SloaiHinh {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_dm_lh")
    @SequenceGenerator(name = "seq_dm_lh", sequenceName = "seq_dm_lh", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NHOM_LOAI_HINH", length = 100)
    private String nhomLoaiHinh;

    @Column(name = "MA_LOAI_HINH", length = 50)
    private String maLoaiHinh;

    @Column(name = "TEN_LOAI_HINH", length = 255)
    private String tenLoaiHinh;

    @Column(name = "DIEN_GIAI", length = 500)
    private String dienGiai;

    @Column(name = "TRANG_THAI", length = 50)
    private String trangThai;

    @Column(name = "NGAY_TAO")
    private LocalDateTime ngayTao;

    @Column(name = "NGAY_CAP_NHAT")
    private LocalDateTime ngayCapNhat;
}