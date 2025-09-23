package com.pht.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPermissionDto {

    // Thông tin người dùng
    private Long userId;
    private String username;
    private String fullname;
    private String mail;
    private String phone;
    private String address;
    private String note;

    // Thông tin nhóm
    private Long groupId;
    private String groupName;

    // Danh sách các function được phép (gộp từ group và loại trừ disabled)
    private List<FunctionDto> allowedFunctions;
    
    // Danh sách các function code (func1, func2, ...)
    private List<String> listFunction;
}

