package com.acme.domain.account;

public enum AccountType {
	SAVING("saving"), CHECKING("checking");

	private String type;

	AccountType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
}
