package com.pht.common.model;

import java.util.List;

import lombok.Data;

@Data
public class Export {
	private String filename;
	private String sData;
	private List<String> loi;
}
