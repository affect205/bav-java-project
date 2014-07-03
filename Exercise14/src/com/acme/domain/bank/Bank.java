package com.acme.domain.bank;

import java.io.Serializable;
import java.util.ArrayList;

public class Bank implements Serializable {

	private final ArrayList<Client> clients = new ArrayList<Client>();
	
	private int maxClients = 0;
	
	public Bank() { }
	
	public Client addClient(final Client client) {
		clients.add(client);
		++maxClients;
		System.out.println("new client...");
		return client;
	}
	
	@Override
	public String toString() {
		
		StringBuilder str = new StringBuilder();
		
		for ( Client client : clients ) {
			str.append(client.toString());
		}
		str.append("\n");
		return str.toString();
	}
	
	public boolean contains_client(final String name) {
		
		for ( Client client : clients ) {
			if ( client.getName().equals(name) ) {
				// client exists in system
				return true;
			}
		}
		
		return false;
	}
	
	
	public ArrayList<Client> getClients() { return clients; }

}
