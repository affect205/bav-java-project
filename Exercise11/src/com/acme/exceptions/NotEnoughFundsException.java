package com.acme.exceptions;

public class NotEnoughFundsException extends BankException {
	
	private double amount;
	
	public NotEnoughFundsException() { }
	
	public NotEnoughFundsException(final double amount) { 
		this.amount = amount; 
	}
	
	public NotEnoughFundsException(String msg, final double amount) {
		super(msg);
		this.amount = amount;
	}
}