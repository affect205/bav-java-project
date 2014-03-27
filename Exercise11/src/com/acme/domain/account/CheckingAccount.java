package com.acme.domain.account;

import com.acme.exceptions.OverDraftLimitExceededException;
import com.acme.exceptions.NotEnoughFundsException;

public class CheckingAccount extends AbstractAccount /*implements Account*/ {

	private double overdraft;

	public CheckingAccount(final int id, final double amount,
			final double overdraft) {
		super(id, amount);
		this.overdraft = overdraft;
	}

	@Override
	public void withdraw(final double amount) 
			throws OverDraftLimitExceededException {
		if (amount < 0) {
			//TODO: Have a look at the xception throwing
			throw new IllegalArgumentException("Amount can not be negative");
		}
		if (this.balance - amount >= -overdraft) {
			this.balance -= amount;
		} else {
			//TODO: throw an exception is applicable
			throw new OverDraftLimitExceededException("Overdraft Limit Exceeded!", amount, this);
		}

		if (this.balance < amount) {
			// Not enough balance to cover the amount requested to withdraw
			// Check if there is enough in the overdraft protection (if any)
			double overdraftNeeded = amount - this.balance;
			if (overdraft < overdraftNeeded) {
				//TODO: throw an exception is applicable. Ship an information how much money can we withdrawn within the exception  
				throw new OverDraftLimitExceededException("Overdraft Limit Exceeded!", amount, this);
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
	public void deposit(final double amount) throws IllegalArgumentException {
		if (amount > 0) {
			this.balance += amount;
		} else {
			//TODO: throw an exception is applicable
			throw new IllegalArgumentException("Illegal argument!");
		}

	}


	public AccountType getAccountType() {
		return AccountType.CHECKING;
	}

	public double getOverdraft() {
		return overdraft;
	}
}
