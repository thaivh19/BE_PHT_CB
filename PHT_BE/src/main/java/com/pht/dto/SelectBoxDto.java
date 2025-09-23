package com.pht.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SelectBoxDto {
    private String code;
    private String name;
    private String parent;

    public SelectBoxDto(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
