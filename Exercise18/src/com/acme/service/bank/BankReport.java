package com.acme.service.bank;

import java.util.ArrayList;

import com.acme.domain.bank.Bank;
import com.acme.domain.bank.Client;

public class BankReport {
	
	public BankReport() {
		
	}
	
	public static boolean isClient(Bank bank, String name) {
				
		for ( Client client : bank.getClients() ) {
			if ( client.getName() == name ) {
				return true;
			}
		}
		return false;
	}
}