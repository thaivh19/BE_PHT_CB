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

@Entity
@Table(name = "SLOG_NH_KB")
@Data
public class SlogNhKb {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SLOG_NH_KB_SEQ")
    @SequenceGenerator(name = "SLOG_NH_KB_SEQ", sequenceName = "SLOG_NH_KB_SEQ", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NGAY_DS", nullable = false)
    private LocalDateTime ngayDs; // Ngày đối soát

    @Column(name = "NGAY_NHAN", nullable = false)
    private LocalDateTime ngayNhan; // Ngày nhận

    @Column(name = "LOAI", nullable = false, length = 10)
    private String loai; // Từ NH hay từ KB (NH/KB)

    @Column(name = "NGAN_HANG", length = 100)
    private String nganHang; // Tên ngân hàng

    @Column(name = "JSON_DATA", columnDefinition = "TEXT")
    private String jsonData; // JSON data từ NH hoặc KB
}




