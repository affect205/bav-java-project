package com.acme.exceptions;

import com.acme.domain.account.AbstractAccount;
import com.acme.domain.account.Account;

public class OverDraftLimitExceededException extends NotEnoughFundsException {
	
	private double amount;
	private AbstractAccount account;
	
	public OverDraftLimitExceededException() { }
	
	public OverDraftLimitExceededException(final double amount, final AbstractAccount account) { 
		this.amount		= amount;
		this.account	= account;
	}
	
	public OverDraftLimitExceededException(String msg, final double amount, final AbstractAccount account) {
		super(msg, amount);
		this.amount		= amount;
		this.account	= account;
	}
	
	public double getAmount() { return amount; }
	
	public AbstractAccount getAccount() { return account; }
} 