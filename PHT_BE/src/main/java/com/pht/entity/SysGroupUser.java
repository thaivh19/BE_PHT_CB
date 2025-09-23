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
@Table(name = "SYS_GROUP_USER")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysGroupUser {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sys_group_user_seq")
    @SequenceGenerator(name = "sys_group_user_seq", sequenceName = "SYS_GROUP_USER_SEQ", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @Column(name = "GROUP_NAME", length = 255)
    private String groupName;
}

