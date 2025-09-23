package com.pht.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "SBIEN_LAI")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SBienLai {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SBIEN_LAI_SEQ")
    @SequenceGenerator(name = "SBIEN_LAI_SEQ", sequenceName = "SBIEN_LAI_SEQ", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @Column(name = "MST", length = 20)
    private String mst;

    @Column(name = "TEN_DVI", length = 255)
    private String tenDvi;

    @Column(name = "DIA_CHI", length = 500)
    private String diaChi;

    @Column(name = "EMAIL", length = 100)
    private String email;

    @Column(name = "SDT", length = 20)
    private String sdt;

    @Column(name = "MA_BL", length = 50)
    private String maBl;

    @Column(name = "SO_BL", length = 50)
    private String soBl;

    @Column(name = "HTHUC_TTOAN", length = 100)
    private String hthucTtoan;

    @Column(name = "NGAY_BL")
    private LocalDateTime ngayBl;

    @Column(name = "LOAI_CTIET", length = 100)
    private String loaiCtiet;

    @Column(name = "GHI_CHU", length = 1000)
    private String ghiChu;

    @Column(name = "STB", length = 50)
    private String stb;

    @Column(name = "NGAY_NOP")
    private LocalDateTime ngayNop;

    @Column(name = "SO_TK", length = 50)
    private String soTk;

    @Column(name = "NGAY_TK")
    private LocalDateTime ngayTk;

    @Column(name = "MA_KHO", length = 50)
    private String maKho;

    @Column(name = "NGAY_TAO")
    private LocalDateTime ngayTao;

    @Column(name = "NGUOI_TAO", length = 100)
    private String nguoiTao;

    @Column(name = "NGAY_SUA")
    private LocalDateTime ngaySua;

    @Column(name = "NGUOI_SUA", length = 100)
    private String nguoiSua;

    @Column(name = "ID_PH", length = 100)
    private String idPhatHanh;

    @Column(name = "IMG_BL", columnDefinition = "TEXT")
    private String imageBl;

    // Relationship
    @OneToMany(mappedBy = "bienLai", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<SBienLaiCt> chiTietList;
}
