package com.acme;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

	public String getSalutation() {
		if (gender != null) {
			return gender.getSalutation() + " " + name;
		} else {
			return name;
		}
	}

	public Account getAccount(final int id) {
		return null;
	}

	public void addAccount(final Account account) {
		accounts.add(account);
	}

	@Override
	public String toString() {
		StringBuffer accs = new StringBuffer(getSalutation() + " accounts:\n");
		for (Account acc : accounts) {
			accs.append(acc);
			accs.append("\n");
		}
		return accs.toString();
	}

	public List<Account> getAccountsList() {
		return Collections.unmodifiableList(accounts);
	}
}
