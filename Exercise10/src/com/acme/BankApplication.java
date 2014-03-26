package com.acme;

public class BankApplication {
	public static void main(String[] args) {
		
		// создаем клиентов
		Client clients[] = new Client[4];
		clients[0] = new Client("Andersen",	Gender.MALE);
		clients[1] = new Client("Smith", 	Gender.MALE);
		clients[2] = new Client("Holmes",	Gender.MALE);
		clients[3] = new Client("Watson", 	Gender.MALE);
		
		// слушатели событий
		ClientRegistrationListener[] listeners = new ClientRegistrationListener[2]; 
		listeners[0] = new Bank.PrintClientListener();
		listeners[1] = new Bank.EmailNotificationListener();
		
		// создаем банк
		Bank bank = new Bank(listeners);
		
		// добавление клиентов в банк
		for ( Client client : clients ) {
			bank.addClient(client);
		}
	}
}