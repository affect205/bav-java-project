package com.acme.domain.client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.acme.domain.account.Account;

public class Client implements Serializable {

	private String name;
	private Gender gender;

	//TODO: define a list of accounts
	ArrayList<Account> accounts;
	

	public Client(final String name, final Gender gender) {
		this.name = name;
		this.gender = gender;
		accounts = new ArrayList<Account>();
	}

	public String getName() {
		return name;
	}

	public String getSalutation() {
		if (gender != null) {
			return gender.getSalutation() + " " + name;
		} else {
			return name;
		}
	}

	public Account getAccount(final int id) {
		//Get account by id
		for ( Iterator<Account> it = accounts.iterator(); it.hasNext(); ) {
			
			Account account = (Account) it.next();
			if ( account.getAccountNumber() == id ) {
				return account;
			}
		}
		return null;
	}

	public void addAccount(final Account account) {
		
		accounts.add(account);
	}

	@Override
	public String toString() {
		StringBuffer accs = new StringBuffer(getSalutation() + " accounts:\n");
		//TODO: collect the account information
		for ( Iterator<Account> it = accounts.iterator(); it.hasNext(); ) {
			
			Account account = (Account) it.next();
			accs.append(account.toString());
		}
		return accs.toString();
	}

	public final List<Account> getAccountsList() {
		
		//TODO: apply unmodifiable strategy
		return new ArrayList<Account>(accounts);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((accounts == null) ? 0 : accounts.hashCode());
		result = prime * result + ((gender == null) ? 0 : gender.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Client other = (Client) obj;
		if (accounts == null) {
			if (other.accounts != null)
				return false;
		} else if (!accounts.equals(other.accounts))
			return false;
		if (gender == null) {
			if (other.gender != null)
				return false;
		} else if (!gender.equals(other.gender))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}
