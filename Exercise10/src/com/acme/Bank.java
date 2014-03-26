package com.acme;

import java.io.Serializable;

public class Bank implements Serializable {

	private final Client[] clients = new Client[10];
	private int maxClients = 0;
	private ClientRegistrationListener[] listeners = null;

	public Bank(ClientRegistrationListener[] listeners) {
		
		this.listeners = listeners;
	}

	public Client addClient(final Client client) {
		clients[maxClients++] = client;

		// TODO: iterate a list of listeners and notify about new client
		for ( ClientRegistrationListener listener : listeners ) {
			// вызываем метод события
			listener.onClientAdded(client);
		}  
		
		return client;
	}
	
	
	// ----------------------------INNER CLASSES-----------------------------
	public static class EmailNotificationListener
	implements ClientRegistrationListener {
		
		public void onClientAdded(Client c) {
			System.out.println("Notification email for client "
					+ c.getSalutation() + " to be sent.");
		}
	}
	
	public static class PrintClientListener 
	implements ClientRegistrationListener {
		
		public void onClientAdded(Client c) {
			System.out.println(c.toString());
		}
	}

}
