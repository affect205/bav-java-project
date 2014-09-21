/**
 * @author alexbalu-alpha7@mail.ru
 */

package com.alex.balyschev.emanager;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.util.Log;

public final class ExpenseManager implements DataStore {

	// users
	private Set<User> users; 
	// accounts
	private Set<Account> accounts;
	// categories
	private Set<Category> categories;
	// data keys for account list view
	public static final String LV_KEY_ACCOUNT_ID 		= "id";
	public static final String LV_KEY_ACCOUNT_BALANCE 	= "balance";
	public static final String LV_KEY_ACCOUNT_TITLE 	= "title";
	public static final String LV_KEY_ACCOUNT_COUNT 	= "count";
	public static final String LV_KEY_ACCOUNT_NUMBER	= "number";
	// data keys for record list view
	public static final String LV_KEY_RECORD_ID 		= "id";
	public static final String LV_KEY_RECORD_AMOUNT 	= "amount";
	public static final String LV_KEY_RECORD_TITLE 		= "title";
	public static final String LV_KEY_RECORD_DATE 		= "date";
	public static final String LV_KEY_RECORD_NUMBER		= "number";
	// DB connection
	private DBHelper dbConn;
	// manager entities
	public static final String EMANAGER_TABLE_USERS			= "users"; 
	public static final String EMANAGER_TABLE_ACCOUNTS		= "accounts"; 
	public static final String EMANAGER_TABLE_RECORDS		= "records"; 
	public static final String EMANAGER_TABLE_CATEGORIES	= "categories"; 
	
	/**
	 * Constructor
	 */
	public ExpenseManager(Context context) {
		this.users		= new HashSet<User>();
		this.accounts 	= new HashSet<Account>();
		this.categories = new HashSet<Category>();
		dbConn 			= DBHelper.getConnection(context);
		if ( dbConn != null ) {
			this.users		= dbConn.getUsers();
			this.accounts	= dbConn.getAccounts();
			this.categories	= dbConn.getCategories();
		}
	}
	
	/**
	 * return null if no such user
	 */
	public User getUser(String name) {
		for ( User user : users ) {
			if ( user.getLogin().equals(name) ) {
				return user;
			}
		}
		return null;
	}

	/**
	 * If no users, return empty collection (not null)
	 */
	public Set<String> getUserNames() {
		Set<String> names = new HashSet<String>();
		
		for ( User user : users ) {
			names.add(user.getName());
		}
		return names;
	}

	/**
	 * If no accounts, return empty collection (not null)
	 */
	public Set<Account> getAccounts(User owner) {
		Set<Account> userAccounts = new HashSet<Account>();
		
		if ( owner == null ) {
			return userAccounts;
		}
		
		for ( Account account : accounts ) {
			if ( owner.getName().equals(account.getOwner()) ) {
				userAccounts.add(account);
			}
		}
		return userAccounts;
	}
	
	/**
	 * If no records, return empty collection (not null)
	 */
	public Set<Record> getRecords(Account account) {
		if ( account == null ) {
			return new HashSet<Record>();
		}
		return account.getRecords();
	}

	/**
	 * add user
	 */
	public void addUser(User user) {
		if ( user != null ) {
			// add to manager
			users.add(user);
			// add to DB
			long id = dbConn.insertUser(user);
			Log.i("INSERT USER", "id: " + id);
		}
	}

	/**
	 * add account
	 */
	public void addAccount(User user, Account account) {
		if ( account != null && user != null ) {
			// add to manager
			accounts.add(account);
			// add to DB
			long id = dbConn.insertAccount(user, account);
			Log.i("INSERT ACCOUNT", "id: " + id);
		}
	}

	/**
	 * add record
	 */
	public void addRecord(Account account, Record record) {
		if ( account != null && record != null ) {
			// add to manager
			account.addRecord(record);
			// add to DB
			long id = dbConn.insertRecord(account, record);
			Log.i("INSERT RECORD", "id: " + id);
		}
	}
	
	/**
	 * add category
	 */
	public void addCategory(Category category) {
		if ( category != null ) {
			categories.add(category);
			long id = dbConn.insertCategory(category);
			Log.i("INSERT CATEGORY", "id: " + id);
		}
	}
	
	/**
	 * get categories
	 */
	public Set<Category> getCategories() {
		return this.categories;
	}

	/**
	 * return removed User or null if no such user
	 */
	public User removeUser(String name) {
		User user = null;
		for ( User savedUser : users ) {
			if ( savedUser.getName().equals(name) ) {
				user = savedUser;
				users.remove(savedUser);
				break;
			}
		}
		return user;
	}

	/**
	 * return null if no such account
	 */
	public Account removeAccount(User owner, Account account) {
		if ( account == null || owner == null ) {
			return null;
		}
		for ( Account removedAccount : accounts ) {
			if ( removedAccount.getId() == account.getId() ) {
				// now remove account from manager
				if ( accounts.remove(account) ) {
					// also from DB
					dbConn.deleteAccount(removedAccount);
					return removedAccount;
				}
				return null;
			}
		}
		return null;
	}

	/**
	 * return null if no such record
	 */
	public Record removeRecord(Account from, Record record) {
		if ( from == null || record == null ) {
			return null;
		}
		if ( from.removeRecord(record) ) {
			dbConn.deleteRecord(from, record);
			return record;
		}
		return null;
	}
	
	/**
	 * get unique number
	 */
	public long getUniqueNumber(final String table) {
		if ( dbConn != null ) {
			return dbConn.getUniqueNumber(table); 
		}
		return 0;
	}
	
	/**
	 * load test data
	 */
	public void loadTestData() {
		dbConn.loadTestData();
	}
	
	/**
	 * test data initialization
	 */
	private void loadData() {
		
		User user 	= new User(1L, "AlexDiaz", "liberty"); 
		User user2 	= new User(2L, "HelloMike", "destiny"); 
		addUser(user);
		addUser(user2);
		
		Account account1 = new Account("AlexDiaz", 111, 8500.0, "personal account");
		Account account2 = new Account("AlexDiaz", 112, 2500.0, "family account");
		addAccount(user, account1);
		addAccount(user, account2);
		
		Record record1 = new Record(1, Record.Operation.RECHARGE, 450.0, new Date(), new Category(10, "food", "on food cat"), "ordinar spend...");
		Record record2 = new Record(2, Record.Operation.WITHDRAW, 120.0, new Date(), new Category(20, "food", "on food cat"), "ordinar spend...");
		Record record3 = new Record(3, Record.Operation.WITHDRAW, 230.0, new Date(), new Category(30, "fuel", "fuel in gaz station"), "ordinar spend...");
		Record record4 = new Record(4, Record.Operation.RECHARGE, 600.0, new Date(), new Category(40, "cafe", "good evening"), "ordinar spend...");
		addRecord(account1, record1);
		addRecord(account1, record2);
		addRecord(account2, record3);
		addRecord(account2, record4);
	}	
}