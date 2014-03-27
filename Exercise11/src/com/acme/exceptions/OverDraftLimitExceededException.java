package com.acme.exceptions;

public class OverDraftLimitExceededException extends NotEnoughFundsException {
	
	public OverDraftLimitExceededException() { }
	
	public OverDraftLimitExceededException(String msg) { 
		super(msg);
	}
} 