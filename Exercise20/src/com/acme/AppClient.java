package com.acme;

public class AppClient {

	public static void main(final String[] args) {
		ClientDao clientDao = new ClientDaoImpl();
		Client client = new Client();
		// add method is not called in transaction
		clientDao.add(client);

		// TODO: create ClientDao proxy which will analyse Transactional
		// annotation and "start" a transaction.
		
		// get instance
		TransactionManager transactManager = (TransactionManager)TransactionManager.
				applyTransactionDemarcation(clientDao);
	
		// do something
		Object proxy = new Object();
		transactManager.invoke(proxy, clientDao.getClass().getMethod("find", new Object[] {clientDao.hashCode()}), args);
		
	}

}
