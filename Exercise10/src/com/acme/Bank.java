package com.acme;

import java.io.Serializable;

public class Bank implements Serializable {

	private final Client[] clients = new Client[10];
	private int maxClients = 0;
	private ClientRegistrationListener[] listeners;

	public Bank() {
		
		listeners = new ClientRegistrationListener[2];
		// создаем слушатели событий
		listeners[0] = new PrintClientListener();
		listeners[1] = new EmailNotificationListener();
	}

	public Client addClient(final Client client) {
		clients[maxClients++] = client;

		// TODO: iterate a list of listeners and notify about new client
		return client;
	}
	
	
	// ----------------------------INNER CLASSES-----------------------------
	protected static class EmailNotificationListener
	implements ClientRegistrationListener {
		
		public void onClientAdded(Client c) {
			System.out.println("Notification email for client "
					+ c.getName() + " to be sent.");
		}
	}
	
	protected static class PrintClientListener 
	implements ClientRegistrationListener {
		
		public void onClientAdded(Client c) {
			System.out.println(c.toString());
		}
	}

}
