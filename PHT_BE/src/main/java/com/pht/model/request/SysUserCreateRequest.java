package com.pht.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysUserCreateRequest {

    private String username;
    private String password;
    private Long groupId;
    private String fullname;
    private String mail;
    private String phone;
    private String address;
    private String note;
}









