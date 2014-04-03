package com.acme.domain.account;

import com.acme.exceptions.NoEnoughFundsException;

public class SavingAccount extends AbstractAccount 
implements Account {

	public SavingAccount(final int id, final double amount) {
		super(id, amount);
	}

	@Override
	public void deposit(final double amount) throws IllegalArgumentException {
		if (amount < 0) {
			throw new IllegalArgumentException("Amount can not be negative");
		}
		this.balance += amount;
	}

	@Override
	public void withdraw(final double amount) throws NoEnoughFundsException {
		if (amount < 0) {
			throw new IllegalArgumentException("Amount can not be negative");
		}
		if (this.balance - amount >= 0) {
			this.balance -= amount;
		} else {
			throw new NoEnoughFundsException(amount);
		}

	}

	public AccountType getAccountType() {
		return AccountType.SAVING;
	}

	//TODO: implement toString method which details this account information
	public String toString() {
		return "Account: " + this.getAccountType().getType()
				+ " Balance: " + this.getBalance();
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if ( this == obj ) {
			return true;
		}
		
		if ( obj == null ) {
			return false;
		}
		
		if ( this.getClass() != obj.getClass() ) {
			return false;
		}
		
		SavingAccount other = (SavingAccount) obj;
		
		if ( balance != other.getBalance() ) {
			return false;
		}
		
		return true;
	}
	
	@Override
	public int hashCode() {
		int prime = 44;
		int result = 1;
		result = prime * result + (int)decimalValue(balance);
		return result;
	}
	
	@Override
	public long decimalValue(double value) {
		return Math.round(value);
	}
}
