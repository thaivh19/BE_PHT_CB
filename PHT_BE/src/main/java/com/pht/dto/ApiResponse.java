package com.pht.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiResponse {
	private Boolean success;
	private String message;
	private Object data;

	public ApiResponse(Boolean success, String message, Object data) {
		this.success = success;
		this.message = message;
		this.data = data;
	}

}
