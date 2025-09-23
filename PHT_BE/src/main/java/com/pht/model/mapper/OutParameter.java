package com.pht.model.mapper;


import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureParameter;
import lombok.Data;

@Data
public class OutParameter {

//	@StoredProcedureParameter(mode = ParameterMode.REF_CURSOR, type = void.class)
	private Object pCur;
}
