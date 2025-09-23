package com.pht.entity;

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
@Table(name = "SNGAY_LV")
@Data
@EqualsAndHashCode(callSuper = false)
public class NgayLv {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "NGAY_LV_SEQ")
    @SequenceGenerator(name = "NGAY_LV_SEQ", sequenceName = "NGAY_LV_SEQ", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NGAY_LV", nullable = false, length = 20)
    private String ngayLv; // Ngày làm việc

    @Column(name = "TRANG_THAI", nullable = false, length = 1)
    private String trangThai; // Trạng thái (1: Làm việc, 0: Nghỉ)

    @Column(name = "COT", nullable = false, length = 20)
    private String cot; // Cột phân loại
}
