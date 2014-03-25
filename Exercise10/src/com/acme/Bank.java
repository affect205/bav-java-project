package com.acme;

import java.io.Serializable;

public class Bank implements Serializable {

	private final Client[] clients = new Client[10];
	private int maxClients = 0;
	private ClientRegistrationListener[] listeners;

	public Bank() {

	}

	public Client addClient(final Client client) {
		clients[maxClients++] = client;

		// TODO: iterate a list of listeners and notify about new client
		return client;
	}

}
