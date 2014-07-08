package com.acme.domain.client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.acme.exceptions.ClientExistsException;

public class Bank implements Serializable {

	//TODO: define a list of clients using generics.
	Set<Client> clients = new HashSet<Client>();
	
	
	public Client addClient(final Client client) throws ClientExistsException {

		if (getClientByName(client.getName()) != null) {
			throw new ClientExistsException("Client with name: "
					+ client.getName() + " has already in the bank", client);
		}
		
		//TODO: add a client to the list
		clients.add(client);
		
		return client;
	}

	public final Client getClientByName(final String name) {
		//TODO: iterate thru the list of clients and find one whith the required name
		
		for ( Client client : clients ) {
			// client already exists
			if ( client.getName().equals(name) ) {
				return client;
			}
		}
		return null;
	}

	@Override
	public String toString() {

		StringBuffer clientsString = new StringBuffer("The bank has: \n");
		//TODO: add information about all the cleints
		
		Iterator<Client> it = clients.iterator();
		while ( it.hasNext() ) {
			// add client to string
			Client client = (Client)it.next();
			clientsString.append(client.getName());
			clientsString.append("\n");
		}
		
		return clientsString.toString();
	}

	public final List<Client> getClientsList() {
		//TODO: apply unmodifiable strategy
		return new ArrayList<Client>(clients);
	}

}
