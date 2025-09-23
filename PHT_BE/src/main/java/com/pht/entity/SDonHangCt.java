package com.pht.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "SDON_HANG_CT")
public class SDonHangCt {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sdon_hang_ct_seq")
    @SequenceGenerator(name = "sdon_hang_ct_seq", sequenceName = "SDON_HANG_CT_SEQ", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @Column(name = "DON_HANG_ID")
    private Long donHangId;

    @Column(name = "ID_TOKHAI")
    private Long idTokhai;

    @Column(name = "SO_THONG_BAO", length = 100)
    private String soThongBao;

    @Column(name = "NGAY_THONG_BAO")
    private LocalDateTime ngayThongBao;

    @Column(name = "THANH_TIEN", precision = 18, scale = 2)
    private java.math.BigDecimal thanhTien;

    // Relationship
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DON_HANG_ID", insertable = false, updatable = false)
    @JsonBackReference
    private SDonHang donHang;
}



