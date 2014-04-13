package com.acme.domain.bank;

import java.io.Serializable;

public class Bank implements Serializable {

	private final Client[] clients = new Client[10];
	private int maxClients = 0;
	
	public Bank() { }
	
	public Client addClient(final Client client) {
		clients[maxClients++] = client;
		System.out.println("new client...");
		return client;
	}
	
	@Override
	public String toString() {
		
		StringBuilder str = new StringBuilder();
		for ( int i=0; i < maxClients; ++i ) {
			str.append(clients[i].toString());
		}
		str.append("\n");
		return str.toString();
	}

}
