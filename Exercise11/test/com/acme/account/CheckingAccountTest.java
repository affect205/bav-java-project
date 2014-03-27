package com.acme.account;

import static org.junit.Assert.*;

import org.junit.Test;

import com.acme.exceptions.NoEnoughFundsException;

public class CheckingAccountTest {

	@Test
	public void testDeposit() {

		com.acme.domain.account.Account account2 = new com.acme.domain.account.CheckingAccount(
				2, 100, 0);

		assertEquals(100.0, account2.getBalance());
		account2.deposit(11);
		assertEquals(111.0, account2.getBalance());
		try {
			account2.deposit(-10);
			assertTrue(false);
		} catch (IllegalArgumentException e) {
			assertTrue(true);
		}
	}

	@Test
	public void testWithdraw() {
		com.acme.domain.account.Account account2 = new com.acme.domain.account.CheckingAccount(2, 100, 10);

		assertEquals(100.0, account2.getBalance());
		try {
			account2.withdraw(10);
		} catch (NoEnoughFundsException e1) {
			assertTrue(false);
		}
		assertEquals(90.0, account2.getBalance());

		try {
			account2.withdraw(90);
		} catch (NoEnoughFundsException e) {
			assertTrue(false);
		}
		assertEquals(0.0, account2.getBalance());
		
		try {
			account2.withdraw(190);
			assertTrue(false);
		} catch (NoEnoughFundsException e) {
			assertTrue(true);
		}

	}

}
