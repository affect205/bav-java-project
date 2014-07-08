package com.acme.service.bank;

import com.acme.domain.client.Bank;


public interface BankReportPresentationService {

	public void printNumberOfClients(Bank bank);

	public void printNumberOfOpenedAccounts(Bank bank);

	public void printAccountsSortedByDeposit(Bank bank, boolean ascending);

	public void printClientsAccounts(Bank bank);

	public void setBankReportService(final BankReportService bankReportService);
}
