package com.acme.service.bank;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;

import com.acme.domain.bank.Bank;



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
		System.out.println("path: " + path);
		
		FileReader filereader = new FileReader(path);
		LineNumberReader linereader = new LineNumberReader(filereader); 
		
		try {
			String line;
			while ( (line = linereader.readLine()) != null ) {
				System.out.println(line);
				
				
				//bank.addClient(new Client)
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
