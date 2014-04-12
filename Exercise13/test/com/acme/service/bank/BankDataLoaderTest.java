package com.acme.service.bank;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.acme.domain.account.CheckingAccount;
import com.acme.domain.bank.Bank;
import com.acme.domain.bank.Client;
import com.acme.domain.bank.Gender;
import com.acme.service.bank.BankDataLoader;
import com.acme.service.bank.impl.BankServiceImpl;

public class BankDataLoaderTest {

	@Test
	/*
	public void testAddClientFromString() {
		BankDataLoader bankDataLoader = new BankDataLoader();
		bankDataLoader.setBankService(new BankServiceImpl());
		Bank bank = new Bank();
		String line = "accounttype=c;balance=100;overdraft=50;name=John;gender=m;";
		bankDataLoader.addClientFromString(bank, line);
		Client client = bank.getClient(0);
		assertNotNull(client);
		assertEquals("John", client.getName());
		assertEquals(Gender.MALE, client.getGender());
		assertNotNull(client.getAccount());
		
		assertEquals(100, client.getAccount().getBalance());
		assertEquals(50, ((CheckingAccount)client.getAccount()).getOverdraft());
	}
	*/
}
