package com.acme.service.bank.impl;

import java.io.IOException;

import com.acme.domain.bank.Bank;
import com.acme.domain.bank.Client;
import com.acme.domain.bank.Gender;
import com.acme.exceptions.ClientExistsException;
import com.acme.service.bank.BankService;

public class BankServiceImpl implements BankService {

	//TODO: serialize the bank to the file represented by pathTo
	public void saveBank(final Bank bank, final String pathTo)
			throws IOException {
		
	}

	//TODO: deserialize the bank to the file represented by pathTo
	public Bank loadBank(final String pathFrom) throws IOException {
		return new Bank();
	}
	
	// TODO: add client method
	public Client addClient(Bank bank, String name, Gender gender)
			throws ClientExistsException
	{
		return new Client("Andersen", Gender.MALE);
	}
}
