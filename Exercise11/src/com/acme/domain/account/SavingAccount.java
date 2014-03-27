package com.acme.domain.account;


public class SavingAccount extends AbstractAccount {

	public SavingAccount(final int id, final double amount) {
		super(id, amount);
	}

	@Override
	public void deposit(final double amount) throws IllegalArgumentException {
		if (amount < 0) {
			//TODO: throw an exception is applicable
		}
		this.balance += amount;
	}

	@Override
	public void withdraw(final double amount) {
		if (amount < 0) {
			//TODO: throw an exception is applicable
		}
		if (this.balance - amount >= 0) {
			this.balance -= amount;
		} else {
			//TODO: throw an exception is applicable			
		}

	}

	public AccountType getAccountType() {
		return AccountType.SAVING;
	}

}
