package com.acme.exceptions;

//TODO: make a checked exception here. Make an exception hierarchy as proposed in the labguide.
public class BankException extends Exception {

	public BankException() {}
	
	public BankException(String msg) {
		super(msg);
	}
}
