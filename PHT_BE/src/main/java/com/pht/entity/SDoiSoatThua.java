package com.pht.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "SDOI_SOAT_THUA")
@Data
public class SDoiSoatThua {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SDOI_SOAT_THUA_SEQ")
    @SequenceGenerator(name = "SDOI_SOAT_THUA_SEQ", sequenceName = "SDOI_SOAT_THUA_SEQ", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @Column(name = "DOI_SOAT_ID")
    private Long doiSoatId; // ID của bảng SDOI_SOAT

    @Column(name = "NGAY_DS")
    private java.time.LocalDate ngayDs; // Ngày đối soát

    @Column(name = "NGAN_HANG", length = 200)
    private String nganHang; // Ngân hàng

    @Column(name = "TRANS_ID", length = 100)
    private String transId; // Transaction ID từ ngân hàng

    @Column(name = "TO_KHAI_ID", length = 100)
    private String toKhaiId; // Mã tờ khai từ ngân hàng

    @Column(name = "SO_TIEN")
    private BigDecimal soTien; // Số tiền từ ngân hàng

    @Column(name = "TRANG_THAI", length = 50)
    private String trangThai; // Trạng thái từ ngân hàng (SUCCESS/FAILED)

    @Column(name = "THOI_GIAN_THANH_TOAN")
    private LocalDateTime thoiGianThanhToan; // Thời gian thanh toán


}




