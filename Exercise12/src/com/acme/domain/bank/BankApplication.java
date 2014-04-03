package com.acme.domain.bank;

import com.acme.domain.account.CheckingAccount;
import com.acme.domain.account.SavingAccount;
import com.acme.domain.client.Client;
import com.acme.domain.client.Gender;

public class BankApplication {
	public static void main(String[] args) {
		
		// создаем клиентов
		Client clients[] = new Client[4];
		clients[0] = new Client("Andersen",	Gender.MALE);
		clients[1] = new Client("Andersen", Gender.MALE);
		clients[2] = new Client("Holmes",	Gender.MALE);
		clients[3] = new Client("Andersen", Gender.FEMALE);
		
		int id=4;
		for ( Client client : clients ) {
			client.addAccount(new SavingAccount(id++, 2000.0));			
			client.addAccount(new CheckingAccount(id, 2700.0, 1200.0));
			id <<= 2;
		}
		
		for ( Client client : clients ) {
			System.out.println("\nClient: " + client.toString());
			System.out.println("hashCode: " + client.hashCode() + "\n");
			
			for (Client client2 : clients ) {
				System.out.println("EQ: " + client.getSalutation() 
						+ " vs " + client2.getSalutation()
						+ " -> " + client.equals(client2));
			}
		
		}
	}
}