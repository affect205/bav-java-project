package com.acme.domain.account;

import com.acme.exceptions.NoEnoughFundsException;

public interface Account {

	void deposit(double amount) throws IllegalArgumentException;

	void withdraw(double amount) throws NoEnoughFundsException;

	int getAccountNumber();

	AccountType getAccountType();

	boolean isOpened();

	double getBalance();

	long decimalValue();
}
