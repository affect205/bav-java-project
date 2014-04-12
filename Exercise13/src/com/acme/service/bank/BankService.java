package com.acme.service.bank;

import java.io.IOException;

import com.acme.domain.bank.Bank;
import com.acme.domain.bank.Client;
import com.acme.domain.bank.Gender;
import com.acme.exceptions.ClientExistsException;

public interface BankService {

	void saveBank(Bank bank, String pathTo) throws IOException;

	Bank loadBank(String pathFrom) throws IOException;


	Client addClient(Bank bank, String name, Gender gender)
			throws ClientExistsException;
}