package com.acme.service.bank.impl;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.acme.domain.bank.Bank;
import com.acme.domain.bank.Client;
import com.acme.domain.bank.Gender;
import com.acme.exceptions.ClientExistsException;
import com.acme.service.bank.BankService;

public class BankServiceImpl implements BankService {

	//TODO: serialize the bank to the file represented by pathTo
	public void saveBank(final Bank bank, final String pathTo)
			throws IOException {
		
		// serialize and save bank object
		System.out.println("Bank saving...");
		
		FileOutputStream fos	= new FileOutputStream(pathTo);
		ObjectOutputStream oos	= new ObjectOutputStream(fos);
		// write object
		oos.writeObject(bank);
		oos.close();
		fos.close();
	}

	//TODO: deserialize the bank to the file represented by pathTo
	public Bank loadBank(final String pathFrom) 
			throws IOException, ClassNotFoundException {
		
		// unserialize and load bank object
		System.out.println("Bank loading...");
		
		FileInputStream fis		= new FileInputStream(pathFrom);
		ObjectInputStream ois	= new ObjectInputStream(fis);
		
		// write object
		Bank bank = (Bank) ois.readObject();
		ois.close();
		fis.close();
		
		return bank;
	}
	
	// TODO: add client method
	public Client addClient(Bank bank, String name, Gender gender)
			throws ClientExistsException
	{
		return new Client("Andersen", Gender.MALE);
	}
}
