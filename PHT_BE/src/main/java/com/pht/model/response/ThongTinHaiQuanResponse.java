package com.pht.model.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import lombok.Data;

@Data
public class ThongTinHaiQuanResponse {
    
    // Thông tin chính (StoKhai)
    private Long id;
    private Integer nguonTK;
    
    // DOANH NGHIỆP KHAI PHÍ
    private String maDoanhNghiepKhaiPhi;
    private String tenDoanhNghiepKhaiPhi;
    private String diaChiKhaiPhi;
    
    // DOANH NGHIỆP XNK
    private String maDoanhNghiepXNK;
    private String tenDoanhNghiepXNK;
    private String diaChiXNK;
    
    // TỜ KHAI HẢI QUAN
    private String soToKhai;
    private LocalDate ngayToKhai;
    private String maHaiQuan;
    private String maLoaiHinh;
    private String maLuuKho;
    private String nuocXuatKhau;
    
    // THÔNG TIN HÀNG HÓA
    private String maPhuongThucVC;
    private String phuongTienVC;
    private String maDiaDiemXepHang;
    private String maDiaDiemDoHang;
    private String maPhanLoaiHangHoa;
    private String mucDichVC;
    
    // TỜ KHAI PHÍ
    private String soTiepNhanKhaiPhi;
    private LocalDate ngayKhaiPhi;
    private String nhomLoaiPhi;
    private String loaiThanhToan;
    private String ghiChuKhaiPhi;
    
    // THÔNG TIN THU PHÍ
    private String soThongBaoNopPhi;
    private BigDecimal tongTienPhi;
    private String trangThaiNganHang;
    private String soBienLai;
    private LocalDate ngayBienLai;
    private String kyHieuBienLai;
    private String mauBienLai;
    private String maTraCuuBienLai;
    private String xemBienLai;
    private LocalDate ngayTt; // Ngày thanh toán
    
    // DANH MỤC LOẠI HÀNG MIỄN PHÍ
    private String loaiHangMienPhi;
    private String loaiHang;
    private String trangThai;
    
    // XML DATA FIELDS
    private String kylan1Xml;
    private String kylan2Xml;
    
    // IMAGE DATA FIELD
    private String imageBl;
    
    // Danh sách chi tiết (StoKhaiChiTiet)
    private List<ChiTietHaiQuanResponse> chiTietList;
}
