package com.acme.exceptions;

public class NotEnoughFundsException extends BankException {
	
	public NotEnoughFundsException() {}
	
	public NotEnoughFundsException(String msg) {
		super(msg);
	}
}