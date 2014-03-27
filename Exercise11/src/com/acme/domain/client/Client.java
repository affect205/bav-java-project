package com.acme.domain.client;

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

	public List<Account> getAccountsList() {
		return Collections.unmodifiableList(accounts);
	}

}
