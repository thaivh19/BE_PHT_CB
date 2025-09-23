package com.pht.entity;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonBackReference;

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
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name = "SBIEN_LAI_CT")
@Data
@EqualsAndHashCode(callSuper = false)
@ToString(exclude = "bienLai")
public class SBienLaiCt {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SBIEN_LAI_CT_SEQ")
    @SequenceGenerator(name = "SBIEN_LAI_CT_SEQ", sequenceName = "SBIEN_LAI_CT_SEQ", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @Column(name = "BL_ID", nullable = false)
    private Long blId;

    @Column(name = "NDUNG_TP", length = 500)
    private String ndungTp;

    @Column(name = "DVT", length = 50)
    private String dvt;

    @Column(name = "SO_LUONG", precision = 18, scale = 2)
    private BigDecimal soLuong;

    @Column(name = "DON_GIA", precision = 18, scale = 2)
    private BigDecimal donGia;

    @Column(name = "SO_TIEN", precision = 18, scale = 2)
    private BigDecimal soTien;

    // Relationship
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BL_ID", insertable = false, updatable = false)
    @JsonBackReference
    private SBienLai bienLai;
}





