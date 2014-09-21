package com.acme;

public interface ClientDao {

	void add(Client client);

	void update(Client client);

	void find(int clientId);
}