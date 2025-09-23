package com.pht.entity;

import java.time.LocalDate;
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
@Table(name = "SMAU_PHI_BIEN_LAI")
@Data
@EqualsAndHashCode(callSuper = false)
public class SmauPhiBienLai {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_dm_mpbl")
    @SequenceGenerator(name = "seq_dm_mpbl", sequenceName = "seq_dm_mpbl", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @Column(name = "MAU_BIEN_LAI", length = 100)
    private String mauBienLai;

    @Column(name = "KY_HIEU", length = 50)
    private String kyHieu;

    @Column(name = "TU_SO", length = 50)
    private String tuSo;

    @Column(name = "DEN_SO", length = 50)
    private String denSo;

    @Column(name = "NGAY_HIEU_LUC")
    private LocalDate ngayHieuLuc;

    @Column(name = "DIEM_THU_PHI", length = 255)
    private String diemThuPhi;

    @Column(name = "TRANG_THAI", length = 50)
    private String trangThai;

    @Column(name = "PHAT_HANH", length = 50)
    private String phatHanh;

    @Column(name = "NGUOI_TAO", length = 100)
    private String nguoiTao;

    @Column(name = "NGAY_TAO")
    private LocalDateTime ngayTao;

    @Column(name = "NGAY_CAP_NHAT")
    private LocalDateTime ngayCapNhat;
}
