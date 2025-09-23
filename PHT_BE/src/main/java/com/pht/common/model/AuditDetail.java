package com.pht.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuditDetail {

    private String field;
    private String column;
    private Object oldValue;
    private Object newValue;
}