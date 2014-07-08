package com.acme.service.bank;

import java.util.List;
import java.util.Map;

import com.acme.domain.account.Account;
import com.acme.domain.client.Bank;
import com.acme.domain.client.Client;

public interface BankReportService {

	int getNumberOfClients(Bank bank);

	int getNumberOfOpenAccounts(Bank bank);

	int getNumberOfAccounts(Bank bank);

	//TODO: define appropriate return type
	List<Account> getAccountsSortedByDeposit(Bank bank, boolean ascending);

	List<Account> getCustomerAccounts(Bank bank);

}