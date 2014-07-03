package com.acme.exceptions;

public class NoEnoughFundsException extends BankException {
	private double amount;

	public NoEnoughFundsException(final double amount) {
		this.amount = amount;
	}

	public String getError() {
		return "Withdraw amount is incorrect: " + amount;

	}

}
