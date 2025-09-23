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
@Table(name = "STRAM_TP")
@Data
@EqualsAndHashCode(callSuper = false)
public class StramTp {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_dm_tp")
    @SequenceGenerator(name = "seq_dm_tp", sequenceName = "seq_dm_tp", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @Column(name = "MA_TRAM_TP", length = 50)
    private String maTramTp;

    @Column(name = "TEN_TRAM_TP", length = 255)
    private String tenTramTp;

    @Column(name = "MASOTHUE", length = 50)
    private String masothue;

    @Column(name = "DIA_CHI", length = 500)
    private String diaChi;

    @Column(name = "TEN_GIAO_DICH", length = 100)
    private String tenGiaoDich;

    @Column(name = "TRANG_THAI", length = 50)
    private String trangThai;

    @Column(name = "NGAY_TAO")
    private LocalDateTime ngayTao;

    @Column(name = "NGAY_CAP_NHAT")
    private LocalDateTime ngayCapNhat;
}
