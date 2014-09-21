package com.acme;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ClientDaoImpl implements ClientDao {

	private DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd_hh:mm:ss"); 
	private final String beginTransact = dateFormat.format(new Date());
	
	@Transactional(begin = "Transaction: add began...")
	public void add(final Client client) {
		System.out.println("ClientDaoImpl: will be saving client");
	}

	@Transactional(begin = "Transaction: update began...")
	public void update(final Client client) {
		System.out.println("ClientDaoImpl: will be updating client");
	}

	@Transactional(begin = "Transaction: find began...")
	public void find(final int clientId) {
		System.out.println("ClientDaoImpl: will be finding a client");
		
	}
}
