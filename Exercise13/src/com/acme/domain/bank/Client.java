package com.acme.domain.bank;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.acme.domain.account.Account;

public class Client implements Serializable {

	private String name;
	private Gender gender;

	private List<Account> accounts = new ArrayList<Account>();

	public Client(final String name, final Gender gender) {
		this.name = name;
		this.gender = gender;
	}

	public String getName() {
		return name;
	}
	
	public Gender getGender() {
		return gender;
	}

	public String getSalutation() {
		if (gender != null) {
			return gender.getSalutation() + " " + name;
		} else {
			return name;
		}
	}

	public Account getAccount(final int id) {
		for (Account acc : accounts) {
			if (acc.getAccountNumber() == id) {
				return acc;
			}
		}
		return null;
	}

	public void addAccount(final Account account) {
		accounts.add(account);
	}

	//TODO: implement toString method which outputs infor for this client
	public String toString() {
		
		StringBuilder str = new StringBuilder();
		str.append(this.getSalutation());
		str.append("\n");
		
		for ( Account account : this.accounts ) {
			str.append(account.toString());
			str.append("\n");
		}
		return str.toString();
	}
	
	public List<Account> getAccountsList() {
		return Collections.unmodifiableList(accounts);
	}

	//TODO: implement hashCode() and equals() methods which will be used in the following examples (collections)
	/**
	 * Сравнение объекта с представленным
	 */
	@Override
	public boolean equals(Object obj) {
		
		if ( this == obj ) {
			// сравниваем объект сам с собой
			return true;
		}
		
		if ( obj == null ) {
			return false;
		}
				
		if ( this.getClass() != obj.getClass() ) {
			return false;
		}
		
		Client other = (Client) obj;
		if ( this.name != other.getName() ) {
			return false;
		}
		
		if ( this.gender != other.getGender() ) {
			return false;
		}
		
		return true;
	}
	
	@Override
	public int hashCode() {
		int prime = 22;
		int result = 1;
		result = prime * result + name.hashCode();
		result = prime * result + gender.hashCode();
		return result;
	}


}
