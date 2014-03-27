package com.acme.domain.account;

import com.acme.exceptions.NotEnoughFundsException;


public interface Account {

	//TODO: add declaration of IllegalArgumentException here. Amount can not be negative
	void deposit(double amount) throws IllegalArgumentException;

	//TODO: add declaration of NoEnoughFundsException here. A user can ask too much funds
	void withdraw(double amount) throws NotEnoughFundsException;

	int getAccountNumber();

	AccountType getAccountType();

	boolean isOpened();

	double getBalance();

}
