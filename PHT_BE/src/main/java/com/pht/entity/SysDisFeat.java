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
@Table(name = "SYS_DIS_FEAT")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysDisFeat {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sys_dis_feat_seq")
    @SequenceGenerator(name = "sys_dis_feat_seq", sequenceName = "SYS_DIS_FEAT_SEQ", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @Column(name = "USER_ID")
    private Long userId;

    @Column(name = "FUNC_ID")
    private Long funcId;
}









