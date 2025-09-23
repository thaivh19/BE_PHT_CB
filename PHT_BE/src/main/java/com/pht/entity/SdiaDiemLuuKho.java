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
@Table(name = "SDIA_DIEM_LUU_KHO")
@Data
@EqualsAndHashCode(callSuper = false)
public class SdiaDiemLuuKho {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_dm_ddlk")
    @SequenceGenerator(name = "seq_dm_ddlk", sequenceName = "seq_dm_ddlk", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @Column(name = "MA_DIA_DIEM_LUU_KHO", length = 50)
    private String maDiaDiemLuuKho;

    @Column(name = "TEN_DIA_DIEM", length = 255)
    private String tenDiaDiem;

    @Column(name = "TEN_DIA_DIEM_TCVN", length = 255)
    private String tenDiaDiemTcVN;

    @Column(name = "DIA_DIEM", length = 500)
    private String diaDiem;

    @Column(name = "DIEN_GIAI", length = 500)
    private String dienGiai;

    @Column(name = "TRANG_THAI", length = 50)
    private String trangThai;

    @Column(name = "LOAI", length = 50)
    private String loai;

    @Column(name = "NGAY_TAO")
    private LocalDateTime ngayTao;

    @Column(name = "NGAY_CAP_NHAT")
    private LocalDateTime ngayCapNhat;
}
