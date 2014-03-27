package com.acme.domain.account;


public interface Account {

	//TODO: add declaration of IllegalArgumentException here. Amount can not be negative
	void deposit(double amount);

	//TODO: add declaration of NoEnoughFundsException here. A user can ask too much funds
	void withdraw(double amount);

	int getAccountNumber();

	AccountType getAccountType();

	boolean isOpened();

	double getBalance();

}
