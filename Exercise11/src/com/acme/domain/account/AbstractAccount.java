package com.acme.domain.account;

import com.acme.exceptions.NotEnoughFundsException;


public abstract class AbstractAccount implements Account {

	private static final long serialVersionUID = 1L;

	protected int id;
	protected double balance;
	protected AccountState state;

	public static int getUniqueAccountNumber() {
		return (int) System.currentTimeMillis();
	}

	public AbstractAccount(final int id, final double amount) {
		this.balance = amount;
		this.id = id;
		state = AccountState.OPENED;
	}

	public double getBalance() {
		return balance;
	}

	public int getAccountNumber() {
		return id;
	}

	public abstract void deposit(final double amount) throws IllegalArgumentException ;

	public abstract void withdraw(final double amount) throws NotEnoughFundsException;

	public void setState(final AccountState state) {
		this.state = state;
	}

	public AccountState getState() {
		return state;
	}


	public boolean isOpened() {
		return state == AccountState.OPENED;
	}
}
