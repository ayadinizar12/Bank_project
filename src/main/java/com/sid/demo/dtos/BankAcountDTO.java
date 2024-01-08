package com.sid.demo.dtos;

import lombok.Data;

@Data
public class BankAcountDTO {

	private String type;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
}
