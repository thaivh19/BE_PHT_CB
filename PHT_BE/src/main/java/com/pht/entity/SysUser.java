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
@Table(name = "SYS_USER")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysUser {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sys_user_seq")
    @SequenceGenerator(name = "sys_user_seq", sequenceName = "SYS_USER_SEQ", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @Column(name = "USERNAME", nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "PASSWORD", nullable = false, length = 255)
    private String password;

    @Column(name = "GROUP_ID")
    private Long groupId;

    @Column(name = "FULLNAME", length = 255)
    private String fullname;

    @Column(name = "MAIL", length = 255)
    private String mail;

    @Column(name = "PHONE", length = 20)
    private String phone;

    @Column(name = "ADDRESS", length = 500)
    private String address;

    @Column(name = "NOTE", length = 1000)
    private String note;
}








