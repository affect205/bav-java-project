package com.acme.domain.account;

import com.acme.exceptions.NoEnoughFundsException;
import com.acme.exceptions.OverDraftLimitExceededException;

public class CheckingAccount extends AbstractAccount
implements Account {

	private double overdraft;

	public CheckingAccount(final int id, final double amount,
			final double overdraft) {
		super(id, amount);
		this.overdraft = overdraft;
	}

	@Override
	public void withdraw(final double amount) throws NoEnoughFundsException {
		if (amount < 0) {
			throw new IllegalArgumentException("Amount can not be negative");
		}
		if (this.balance - amount >= -overdraft) {
			this.balance -= amount;
		} else {
			throw new NoEnoughFundsException(amount);
		}

		if (this.balance < amount) {
			// Not enough balance to cover the amount requested to withdraw
			// Check if there is enough in the overdraft protection (if any)
			double overdraftNeeded = amount - this.balance;
			if (overdraft < overdraftNeeded) {
				throw new OverDraftLimitExceededException(overdraftNeeded
						- overdraft);
			} else {
				// Yes, there is overdraft protection and enough to cover the
				// amount
				this.balance = 0.0;
				overdraft -= overdraftNeeded;
			}
		} else {
			// Yes, there is enough balance to cover the amount
			// Proceed as usual
			this.balance = this.balance - amount;
		}


	}

	@Override
	public void deposit(final double amount) {
		if (amount > 0) {
			this.balance += amount;
		} else {
			throw new IllegalArgumentException("amount ca not be negative");
		}

	}

	//TODO: implement toString method which details this account information
	public String toString() {
		return "Account: " + this.getAccountType().getType()
				+ " Balance: " + this.getBalance()
				+ " Overdraft: " + this.getOverdraft();
	}

	public AccountType getAccountType() {
		return AccountType.CHECKING;
	}

	public double getOverdraft() {
		return overdraft;
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
		
		CheckingAccount other = (CheckingAccount) obj;
		
		if ( balance != other.getBalance() ||
				overdraft != other.getOverdraft() ) {
			return false;
		}
		
		return true;
	}
	
	@Override
	public int hashCode() {
		int prime = 44;
		int result = 1;
		result = prime * result + (int)decimalValue(balance);
		result = prime * result + (int)decimalValue(overdraft);
		return result;
	}
	
	
	public long decimalValue(double value) {
		return Math.round(value);
	}
}
