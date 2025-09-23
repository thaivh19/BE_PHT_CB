package com.pht.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.type.Type;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuditData {

    private Object entity;
    private Serializable id;
    private Object[] currentState;
    private Object[] previousState;
    private String[] propertyNames;
    private Type[] types;
    private String auditMethod;
    private String requestId;
    private LocalDateTime changedTime;
}