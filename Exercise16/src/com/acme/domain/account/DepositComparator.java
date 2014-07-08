package com.acme.domain.account;

import java.util.Comparator;

public class DepositComparator implements Comparator<Account> {
	private boolean ascending = true;

	public DepositComparator(final boolean ascDesc) {
		ascending = ascDesc;
	}

	public int compare(final Account acc1, final Account acc2) {
		if (acc1.getBalance() > acc2.getBalance()) {
			return ascending ? 1 : -1;
		} else if (acc1.getBalance() < acc2.getBalance()) {
			return ascending ? -1 : 1;
		}
		return 0;
	}
}
