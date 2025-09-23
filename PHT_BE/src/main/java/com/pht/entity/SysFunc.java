package com.pht.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "SYS_FUNC")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysFunc {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sys_func_seq")
    @SequenceGenerator(name = "sys_func_seq", sequenceName = "SYS_FUNC_SEQ", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @Column(name = "FUNC_ID", nullable = false, unique = true, length = 50)
    private String funcId;

    @Column(name = "FUNC_NAME", nullable = false, length = 255)
    private String funcName;
}








