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
@Table(name = "STHAM_SO")
@Data
@EqualsAndHashCode(callSuper = false)
public class SthamSo {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "STHAM_SO_SEQ")
    @SequenceGenerator(name = "STHAM_SO_SEQ", sequenceName = "STHAM_SO_SEQ", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @Column(name = "MA_TS", nullable = false, length = 50)
    private String maTs; // Mã tham số

    @Column(name = "TEN_TS", nullable = false, length = 255)
    private String tenTs; // Tên tham số

    @Column(name = "GIA_TRI", length = 500)
    private String giaTri; // Giá trị tham số
}
