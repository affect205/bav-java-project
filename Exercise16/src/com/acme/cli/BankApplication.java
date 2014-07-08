package com.acme.cli;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import com.acme.domain.account.Account;
import com.acme.domain.account.CheckingAccount;
import com.acme.domain.account.SavingAccount;
import com.acme.domain.client.Bank;
import com.acme.domain.client.Client;
import com.acme.domain.client.Gender;
import com.acme.exceptions.BankException;
import com.acme.exceptions.NoEnoughFundsException;
import com.acme.service.bank.BankReportPresentationService;
import com.acme.service.bank.BankReportService;
import com.acme.service.bank.impl.BankReportServiceImpl;
import com.acme.service.bank.impl.BankServiceImpl;
import com.acme.service.bank.impl.ConsoleBankReportPresentationService;


public class BankApplication {
	private static final String ALREADY_LOADED = "Bank has already been loaded via another program argument. Only one option from the list {-m, -lf, -l} may be specified at a time.";

	private static BankReportPresentationService bankReportPresentationService;

	private static BankReportService bankReportService;

	private static BankServiceImpl bankService;

	public static void main(final String[] args) throws IOException,
			BankException {

		initServices();

		Bank bank = createInMemoryBank();

		printTheBank(bank);

	}

	private static void initServices() {
		bankService = new BankServiceImpl();
		bankReportService = new BankReportServiceImpl();
		bankReportPresentationService = new ConsoleBankReportPresentationService();
		bankReportPresentationService.setBankReportService(bankReportService);
	}

	private static void printTheBank(final Bank bank) {
		bankReportPresentationService.printAccountsSortedByDeposit(bank, true);
		bankReportPresentationService.printClientsAccounts(bank);
		bankReportPresentationService.printNumberOfClients(bank);
		bankReportPresentationService.printNumberOfOpenedAccounts(bank);
	}

	private static Bank createInMemoryBank() throws BankException {
		Bank bank = new Bank();
		Client client1 = bankService.addClient(bank, "John", Gender.MALE);
		Account account1 = new SavingAccount(1, 100);
		Account account2 = new CheckingAccount(2, 100, 0);
		client1.addAccount(account1);
		client1.addAccount(account2);

		Client client2 = bankService.addClient(bank, "Miranda", Gender.FEMALE);
		Account account3 = new SavingAccount(3, 50);
		Account account4 = new CheckingAccount(4, 75, 20);
		client2.addAccount(account3);
		client2.addAccount(account4);

		account1.deposit(100);

		account4.withdraw(10);

		try {
			account4.withdraw(10);
		} catch (NoEnoughFundsException e) {
			e.printStackTrace();
		}

		return bank;
	}
}
