package com.pht.model.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Data;

@Data
@JsonPropertyOrder({"status", "code", "text"})
public class ResponseStatus{	
	private String status;
	private String code;
	private String text;
}

