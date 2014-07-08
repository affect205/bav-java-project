package com.acme.service.bank.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import com.acme.domain.account.AbstractAccount;
import com.acme.domain.account.Account;
import com.acme.domain.client.Bank;
import com.acme.domain.client.Client;
import com.acme.service.bank.BankReportService;


public class BankReportServiceImpl implements BankReportService {

	public int getNumberOfClients(Bank bank) {
		
		return bank.getClientsList().size();
	}

	public int getNumberOfOpenAccounts(Bank bank) {
		
		int total = 0;
		
		// iterator by bank clients
		Iterator<Client> clientIter = bank.getClientsList().iterator();
		
		while ( clientIter.hasNext() ) {
			// iterator by client's accounts
			Iterator<Account> accountIter = clientIter.next().getAccountsList().iterator();
			
			while ( accountIter.hasNext() ) {	
				if ( accountIter.next().isOpened() ) {
					// account is opened - increase counter
					total += 1;
				}
			}
		}
		return total;
	}

	public int getNumberOfAccounts(Bank bank) {
		
		int total = 0;
		Iterator<Client> it = bank.getClientsList().iterator();
		while ( it.hasNext() ) {
			total += it.next().getAccountsList().size();
		}
		return total;
	}

	//TODO: define appropriate return type
	public List<Account> getAccountsSortedByDeposit(Bank bank, boolean ascending) {
		
		// accounts
		ArrayList<Object> accounts = new ArrayList<Object>();
		// clients
		ArrayList<Client> clients = (ArrayList<Client>)bank.getClientsList();
		
		Iterator<Client> clientIter = clients.iterator();
		while ( clientIter.hasNext() ) {
			accounts.addAll(clientIter.next().getAccountsList());
		}
				
		// sort accounts
		Collections.sort(accounts, comparator);

		ArrayList<Account> result = new ArrayList<Account>();
		Iterator<Object> accountIter = accounts.iterator();
		while ( accountIter.hasNext() ) {
			// get next
			Object obj = (Object)accountIter.next();
			
			// add account to final list
			result.add((Account)obj);
		}
		
		return result;
	}

	public List<Account> getCustomerAccounts(Bank bank) {
		
		return null;
	}
	
	
	static Comparator<Object> comparator = new Comparator() {
		
		public int compare(final Object acnt1, final Object acnt2) {
			
			AbstractAccount account1 = (AbstractAccount) acnt1;
			AbstractAccount account2 = (AbstractAccount) acnt2;
			
			if ( account1.getBalance() > account2.getBalance() ) {
				return 1;
			} else if ( account1.getBalance() < account2.getBalance() ) {
				return -1;
			}	
			return 0;
		}
	};
}