package com.acme.exceptions;

import com.acme.domain.bank.Client;


public class ClientExistsException extends BankException {

	private Client client;

	public ClientExistsException(final String string) {
		super(string);
	}

	public ClientExistsException(final String string, final Client client) {
		this.client = client;
	}

	public Client getClient() {
		return client;
	}

}
