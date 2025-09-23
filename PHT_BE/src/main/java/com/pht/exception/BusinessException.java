package com.pht.exception;

import lombok.Getter;

@Getter
public class BusinessException extends Exception {
    /**
	 * 
	 */
	private static final long serialVersionUID = -1495568908824856782L;
	private String description;
    
    public BusinessException(String errorMessage){
        super(errorMessage);
    }
    public BusinessException(String errorMessage, String description){
        super(errorMessage);
        this.description = description;
    }
}