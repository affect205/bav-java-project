package com.acme.service.bank.impl;

import com.acme.domain.client.Bank;
import com.acme.domain.client.Client;
import com.acme.domain.client.Gender;
import com.acme.exceptions.ClientExistsException;


public class BankServiceImpl {

	public Client addClient(final Bank bank, final String name,
			final Gender gender) throws ClientExistsException {
		Client client = new Client(name, gender);
		bank.addClient(client);
		return client;
	}

	
}
