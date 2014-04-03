package com.acme.domain.account;

import com.acme.exceptions.OverDraftLimitExceededException;

public class BankApplication {
	public static void main(String[] args) {
		
		try {
			// создадим счет
			AbstractAccount[] accounts = new AbstractAccount[10];
			accounts[0] = new CheckingAccount(123123, 2000.0, 1000.0);
			
			// спровоцируем исключение
			accounts[0].withdraw(5000.0);
			
		} catch(OverDraftLimitExceededException e) {
			System.out.println(e.getMessage());
			// посмотрим баланс сработавшего счета
			double balance	= e.getAccount().getBalance();
			double amount	= e.getAmount();
			
			System.out.println("balance: " + balance + " amount: " + amount);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}