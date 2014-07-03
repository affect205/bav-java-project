package com.acme.service.bank;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import com.acme.domain.account.CheckingAccount;
import com.acme.domain.account.SavingAccount;
import com.acme.domain.bank.Bank;
import com.acme.domain.bank.Client;
import com.acme.domain.bank.Gender;



public class BankDataLoader {
	private BankService bankService;

	/**
	 * Loads a file which contains data feed in the following format: <br>
	 * accounttype=c|s;balance=100;overdraft=50;name=John;gender=m|f;
	 * 
	 * @param bank
	 * @param path
	 * @throws FileNotFoundException
	 */
	public void load(final Bank bank, final String path)
			throws FileNotFoundException {

		//TODO: implement this method parsing a file whose path is passed to the method. Add data to the bank
		System.out.println(path);
		
		FileReader filereader = new FileReader(path);
		LineNumberReader linereader = new LineNumberReader(filereader); 
		Map<String, String> attrs = new HashMap<String, String>();
		
		try {
			String line;
			int id=1;
			while ( (line = linereader.readLine()) != null ) {
				//System.out.println(line);
				StringTokenizer tokenizer = new StringTokenizer(line, ";");
				
				while ( tokenizer.hasMoreElements() ) {
					
					StringTokenizer element = new StringTokenizer(tokenizer.nextToken(), "=");
					
					String attr = element.nextToken();
					String value = element.nextToken();
					System.out.println("attr: " + attr + " value: " + value);
					
					attrs.put(attr, value);
				}
				
				// create client
				Gender gender = ( attrs.get("gender") == "m" ) ? 
						Gender.MALE : Gender.FEMALE;
				
				Client client = new Client(attrs.get("name"), gender);
				
				// create accounts
				double balance		= Double.valueOf(attrs.get("balance"));
				double overdraft 	= Double.valueOf(attrs.get("overdraft")); 
				if ( attrs.get("accounttype").contains("s") ) {
					
					client.addAccount(new SavingAccount(id++, balance));
				}
				
				if ( attrs.get("accounttype").contains("c") ) {
					
					client.addAccount(new CheckingAccount(id++, balance, overdraft));
				}
				
				// add client
				bank.addClient(client);
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	
	public BankService getBankService() {
		return bankService;
	}

	public void setBankService(final BankService bankService) {
		this.bankService = bankService;
	}
}
