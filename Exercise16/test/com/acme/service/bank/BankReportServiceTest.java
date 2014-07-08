package com.acme.service.bank;

import static org.junit.Assert.assertEquals;

import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.acme.domain.account.Account;
import com.acme.domain.client.Bank;
import com.acme.domain.client.Client;
import com.acme.domain.client.Gender;
import com.acme.exceptions.ClientExistsException;
import com.acme.service.bank.impl.BankReportServiceImpl;
import com.acme.service.bank.impl.BankServiceImpl;

public class BankReportServiceTest {

	private BankReportService bankReportService;
	private Bank bank;
	@Before
	public void setup() throws ClientExistsException {
		bankReportService = new BankReportServiceImpl();
		BankServiceImpl bankService = new BankServiceImpl();
		bank = new Bank();
		Client client1 = bankService.addClient(bank, "John", Gender.MALE);
		
		com.acme.domain.account.Account account2 = new com.acme.domain.account.CheckingAccount(2, 100, 0);
		client1.addAccount(account2);
		
		com.acme.domain.account.Account account3 = new com.acme.domain.account.CheckingAccount(3, 50, 0);
		client1.addAccount(account3);

	}
	@Test
	public void testGetNumberOfClients() {
		assertEquals(1, bankReportService.getNumberOfClients(bank));
	}

	@Test
	public void testGetNumberOfOpenAccounts() {
		assertEquals(2, bankReportService.getNumberOfOpenAccounts(bank));
	}
	

	@Test
	public void testGetAccountsSortedByDeposit() {
		List<Account> accountsSortedByDeposit = bankReportService.getAccountsSortedByDeposit(bank, true);
		Iterator<Account> iterator = accountsSortedByDeposit.iterator();
		assertEquals(3, iterator.next().getAccountNumber());
		assertEquals(2, iterator.next().getAccountNumber());
	}

	
}
