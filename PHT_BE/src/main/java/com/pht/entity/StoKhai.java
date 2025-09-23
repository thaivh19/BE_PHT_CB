package com.pht.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
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
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name = "STO_KHAI")
@Data
@EqualsAndHashCode(callSuper = false)
@ToString(exclude = "chiTietList")
public class StoKhai {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    // NGUỒN THÔNG TIN TỜ KHAI
    @Column(name = "NGUON_TK")
    private Integer nguonTK;

    // DOANH NGHIỆP KHAI PHÍ
    @Column(name = "MA_DN_KP", nullable = false, length = 20)
    private String maDoanhNghiepKhaiPhi;

    @Column(name = "TEN_DN_KP", length = 255)
    private String tenDoanhNghiepKhaiPhi;

    @Column(name = "DCHI_KP", length = 500)
    private String diaChiKhaiPhi;

    // DOANH NGHIỆP XNK
    @Column(name = "MA_DN_XNK", nullable = false, length = 20)
    private String maDoanhNghiepXNK;

    @Column(name = "TEN_DN_XNK", length = 255)
    private String tenDoanhNghiepXNK;

    @Column(name = "DCHI_XNK", length = 500)
    private String diaChiXNK;

    // TỜ KHAI HẢI QUAN
    @Column(name = "SO_TK", length = 50)
    private String soToKhai;

    @Column(name = "NGAY_TK")
    private LocalDate ngayToKhai;

    @Column(name = "MA_HQ", length = 50)
    private String maHaiQuan;

    @Column(name = "MA_LOAIHINH", length = 50)
    private String maLoaiHinh;

    @Column(name = "MA_KHO", length = 100)
    private String maLuuKho;

    @Column(name = "NUOC_XK", length = 100)
    private String nuocXuatKhau;

    // THÔNG TIN HÀNG HÓA
    @Column(name = "MA_PT_VC", length = 50)
    private String maPhuongThucVC;

    @Column(name = "PT_VC", length = 100)
    private String phuongTienVC;

    @Column(name = "MA_DD_XH", length = 100)
    private String maDiaDiemXepHang;

    @Column(name = "MA_DD_DH", length = 100)
    private String maDiaDiemDoHang;

    @Column(name = "MA_PL_HH", length = 100)
    private String maPhanLoaiHangHoa;

    @Column(name = "MUC_DICH_VC", length = 200)
    private String mucDichVC;

    // TỜ KHAI PHÍ
    @Column(name = "SO_TN_KP", length = 50)
    private String soTiepNhanKhaiPhi;

    @Column(name = "NGAY_KP")
    private LocalDate ngayKhaiPhi;

    @Column(name = "NHOM_PHI", length = 100)
    private String nhomLoaiPhi;

    @Column(name = "LOAI_TT", length = 100)
    private String loaiThanhToan;

    @Column(name = "GHICHU_KP", length = 500)
    private String ghiChuKhaiPhi;

    // THÔNG TIN THU PHÍ
    @Column(name = "SO_TB_NOP", length = 50)
    private String soThongBaoNopPhi;

    @Column(name = "SO_TB", length = 50)
    private String soThongBao;

    @Column(name = "MSG_ID", length = 50)
    private String msgId;

    @Column(name = "ID_PH", length = 100)
    private String idPhatHanh;

    @Column(name = "TONG_TIEN_PHI", precision = 18, scale = 2)
    private BigDecimal tongTienPhi;

    @Column(name = "TT_NH", length = 50)
    private String trangThaiNganHang;

    @Column(name = "SO_BL", length = 50)
    private String soBienLai;

    @Column(name = "NGAY_BL")
    private LocalDate ngayBienLai;

    @Column(name = "KYHIEU_BL", length = 50)
    private String kyHieuBienLai;

    @Column(name = "MAU_BL", length = 50)
    private String mauBienLai;

    @Column(name = "MA_TRACUU_BL", length = 50)
    private String maTraCuuBienLai;

    @Column(name = "XEM_BL", length = 200)
    private String xemBienLai;

    @Column(name = "NGAY_TT", columnDefinition = "TIMESTAMP")
    private LocalDateTime ngayTt; // Ngày và giờ thanh toán

    // ID biên lai liên kết
    @Column(name = "ID_BIEN_LAI")
    private Long idBienLai;

    // DANH MỤC LOẠI HÀNG MIỄN PHÍ
    @Column(name = "LOAI_HH_MP", length = 500)
    private String loaiHangMienPhi;

    @Column(name = "LOAI_HH", length = 50)
    private String loaiHang;

    @Column(name = "TRANGTHAI", length = 50)
    private String trangThai;

    @Column(name = "TT_PH", length = 50)
    private String trangThaiPhatHanh = "00";

    // XML DATA FIELDS
    @Column(name = "XML_K1", columnDefinition = "TEXT")
    private String kylan1Xml;

    @Column(name = "XML_K2", columnDefinition = "TEXT")
    private String kylan2Xml;

    // IMAGE DATA FIELD
    @Column(name = "IMG_BL", columnDefinition = "TEXT")
    private String imageBl;

    // BANK WEBHOOK DATA FIELDS
    @Column(name = "TVSD_JSON", columnDefinition = "TEXT")
    private String tvsdJson; // JSON data từ ngân hàng

    @Column(name = "TRANS_ID", length = 100)
    private String transId; // Transaction ID từ ngân hàng

    // Relationship
    @OneToMany(mappedBy = "stoKhai", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<StoKhaiCt> chiTietList;
}