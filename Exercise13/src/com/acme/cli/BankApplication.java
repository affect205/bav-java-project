package com.acme.cli;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.acme.domain.account.Account;
import com.acme.domain.account.CheckingAccount;
import com.acme.domain.account.SavingAccount;
import com.acme.domain.bank.Bank;
import com.acme.domain.bank.Client;
import com.acme.domain.bank.Gender;
import com.acme.exceptions.BankException;
import com.acme.exceptions.NoEnoughFundsException;
import com.acme.service.bank.BankDataLoader;
import com.acme.service.bank.BankService;
import com.acme.service.bank.impl.BankServiceImpl;


/**
 * The following scenarios are available:<br>
 * 1) "BankApplication -m" In-memory mode. Creates a few entities
 * programmatically, prints them.<br>
 * 2) "BankApplication -s" Forces to save bank to bank.ser file (file name can
 * not be specified for simplicity)<br>
 * 3) "BankApplication -l" Loads the bank from bank.ser file<br>
 * 4) "BankApplication -lf" Loads feed file from bank.data file, tries to add
 * data to the bank and then saves it to bank.ser file<br>
 * 
 * 
 */
public class BankApplication {
	private static final String ALREADY_LOADED = "Bank has already been loaded via another program argument. Only one option from the list {-m, -lf, -l} may be specified at a time.";

	private static BankDataLoader bankDataLoader;

	private static BankService bankService;

	public static void main(final String[] args) throws IOException,
			BankException, IllegalArgumentException {
		Map<String, String> arguments = new HashMap(); 
		Set<String> keys = new HashSet<String>();
				
		String key = "def";
		for ( int i=0; i < args.length; ++i ) 
		{		
			if ( i%2 == 0 ) {
				keys.add(args[i]);
				key = args[i];
			} else {
				arguments.put(key, args[i]);
			}
		}
		initServices();

		boolean bankLoaded = false;
		Bank bank = null;
		if (keys.contains("-m")) {
			bank = createInMemoryBank();
			bankLoaded = true;
		}

		if (keys.contains("-lf")) {
			if (bankLoaded) {
				System.out.println(ALREADY_LOADED);
				return;
			}
			bank = loadBankFromFeed(arguments.get("-lf"));
			bankLoaded = true;
		}

		if (keys.contains("-l")) {
			if (bankLoaded) {
				System.out.println(ALREADY_LOADED);
				return;
			}
			bank = loadBankFromDataSource();
			bankLoaded = true;
		}
		if (bank == null) {
			System.out.println("Bank is not loaded");
			return;
		}
		
		// Now we have a bank.		
		if (keys.contains("-s")) {
			saveTheBankToDataSource(bank);
		}
		
		// print the bank content
		System.out.println(bank.toString());
	}

	private static void initServices() {
		bankService = new BankServiceImpl();
		bankDataLoader = new BankDataLoader();
		bankDataLoader.setBankService(bankService);

	}

	private static void saveTheBankToDataSource(final Bank bank)
			throws IOException {
		bankService.saveBank(bank, "bank.ser");
	}

	

	private static Bank loadBankFromDataSource() throws IOException {
		return bankService.loadBank("bank.ser");
	}

	private static Bank loadBankFromFeed(String path) throws IOException {
		Bank bank = new Bank();
		bankDataLoader.load(bank, path);
		saveTheBankToDataSource(bank);
		return bank;
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

		account4.withdraw(90);

		try {
			account4.withdraw(10);
		} catch (NoEnoughFundsException e) {
			e.printStackTrace();
		}

		return bank;
	}
}
