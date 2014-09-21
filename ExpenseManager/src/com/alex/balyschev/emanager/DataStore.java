/**
 * @author alexbalu-alpha7@mail.ru
 */
package com.alex.balyschev.emanager;

import java.util.Set;

public interface DataStore {
	// return null if no such user
	public User getUser(String name);

	// If no users, return empty collection (not null)
	public Set<String> getUserNames();

	// If no accounts, return empty collection (not null)
	public Set<Account> getAccounts(User owner);

	// If no records, return empty collection (not null)
	public Set<Record> getRecords(Account account);

	public void addUser(User user);

	public void addAccount(User user, Account account);

	public void addRecord(Account account, Record record);
	
	public void addCategory(Category category);

	// return removed User or null if no such user
	public User removeUser(String name);

	// return null if no such account
	public Account removeAccount(User owner, Account account);

	// return null if no such record
	public Record removeRecord(Account from, Record record);
	
	// If no categories, return empty collection (not null)
	public Set<Category> getCategories();
}