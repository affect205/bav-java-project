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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.alex.balyschev.emanager.Record.Operation;

public class DBHelper extends SQLiteOpenHelper {

	
	// instance
	private static DBHelper dbConn;
	// context
	private static Context context;
	
	// DB structure script
	private static String SQL_DB_STRUCTURE = "" 
			+ " DROP TABLE IF EXISTS  users; " 
			+ " DROP TABLE IF EXISTS  accounts; " 
			+ " DROP TABLE IF EXISTS  records; " 
			+ " DROP TABLE IF EXISTS  categories; " 
			+ " DROP TABLE IF EXISTS  counters; " 
			+ " CREATE TABLE users ("
			+ " id INTEGER PRIMARY KEY AUTOINCREMENT," 
			+ " number INTEGER, "
			+ " login TEXT, "
			+ " password TEXT, " 
			+ " firstname TEXT, "
			+ " surname TEXT, "
			+ " middlename TEXT, "
			+ " email TEXT " 
			+ " ); " 
			+ " CREATE TABLE records ("
			+ " id INTEGER PRIMARY KEY AUTOINCREMENT," 
			+ " number INTEGER, "
			+ " accountid INTEGER, " 
			+ " description TEXT, "
			+ " amount REAL, "
			+ " isreceipt INTEGER, "
			+ " categoryid INTEGER, "
			+ " date INTEGER "
			+ " ); " 
			+ " CREATE TABLE categories ("
			+ " id INTEGER PRIMARY KEY AUTOINCREMENT," 
			+ " number INTEGER, "
			+ " name TEXT, "
			+ " description TEXT " 
			+ " ); " 
			+ " CREATE TABLE accounts ("
			+ " id INTEGER PRIMARY KEY AUTOINCREMENT," 
			+ " number INTEGER, "
			+ " userid INTEGER, " 
			+ " description TEXT, "
			+ " balance REAL "
			+ " ); " 
			+ " CREATE TABLE counters ("
			+ " id INTEGER PRIMARY KEY AUTOINCREMENT," 
			+ " counter TEXT, "
			+ " value INTEGER "
			+ " ); " 
			+ " INSERT INTO counters(counter, value) VALUES ('users', 0) ;" 
			+ " INSERT INTO counters(counter, value) VALUES ('accounts', 0) ;" 
			+ " INSERT INTO counters(counter, value) VALUES ('records', 0) ;" 
			+ " INSERT INTO counters(counter, value) VALUES ('categories', 0) ;";
	
	/**
	 * constructor
	 */
	private DBHelper(Context context) {
		super(context, "emanagerDB", null, 1);
	}

	/**
	 * get connection
	 * @param context
	 * @return
	 */
	public static DBHelper getConnection(Context context) {
		Log.i("DBHELPER", "get conn");
		DBHelper.context = context; 
		if ( dbConn == null ) {
			dbConn = new DBHelper(context);
		}
		return dbConn;
	}
	
	/**
	 * get all users
	 */
	public Set<User> getUsers() {
		Log.i("DBHELPER", "get users");
		Set<User> users = new HashSet<User>();
				
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.query("users", null, null, null, null, null, null);
		
		if ( c.moveToFirst() ) {
			do {
				Log.i("USER ROW", "ID: " 		+ c.getInt(c.getColumnIndex("id")));
				Log.i("USER ROW", "FIRSTNAME: " + c.getString(c.getColumnIndex("firstname")));
				Log.i("USER ROW", "LOGIN: " 	+ c.getString(c.getColumnIndex("login")));
				Log.i("USER ROW", "PASSWORD: " 	+ c.getString(c.getColumnIndex("password")));
				Log.i("USER ROW", "EMAIL: " 	+ c.getString(c.getColumnIndex("email")));
				// create user from DB data
				User user = new User(
						c.getLong(c.getColumnIndex("number")),
						c.getString(c.getColumnIndex("login")),
						c.getString(c.getColumnIndex("password")));
				// add user to set
				users.add(user);
			} while ( c.moveToNext() );
		}
		c.close();
		db.close();
		return users;
	}

	/**
	 * get all users
	 */
	public Set<Account> getAccounts() {
		Log.i("DBHELPER", "get accounts");
		Set<Account> accounts = new HashSet<Account>();
		
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.query("accounts", null, null, null, null, null, null);		
		if ( c.moveToFirst() ) {
			do {
				for ( String name : c.getColumnNames() ) {
					Log.i("COLUMN NAME : VALUE", name + " | " + c.getString(c.getColumnIndex(name)));
				}
				Log.i("ACCOUNT ROW", "ID: " 	+ c.getInt(c.getColumnIndex("id")));
				Log.i("ACCOUNT ROW", "NUMBER: " + c.getInt(c.getColumnIndex("number")));
				Log.i("ACCOUNT ROW", "USERID: " + c.getInt(c.getColumnIndex("userid")));
				Log.i("ACCOUNT ROW", "DESC: " 	+ c.getString(c.getColumnIndex("description")));
				Log.i("ACCOUNT ROW", "BALANCE: " + c.getDouble(c.getColumnIndex("balance")));

				// get account records
				String sql = "SELECT * FROM records JOIN categories ON records.categoryid = categories.id" +
						" WHERE records.accountid = ? ORDER BY records.date DESC"; 
				String[] selectionArgs = { c.getString(c.getColumnIndex("id")) };
				Cursor c2 = db.rawQuery(sql, selectionArgs);
				
				// records
				Set<Record> records = new HashSet<Record>();
				
				if ( c2.moveToNext() ) {
					do {
						Log.i("RECORD ROW", "ID: " 			+ c2.getInt(0));
						Log.i("RECORD ROW", "NUMBER: " 		+ c2.getInt(c2.getColumnIndex("number")));
						Log.i("RECORD ROW", "ACCOUNTID: " 	+ c2.getInt(c2.getColumnIndex("accountid")));
						Log.i("RECORD ROW", "DESC: " 		+ c2.getString(3));
						Log.i("RECORD ROW", "AMOUNT: " 		+ c2.getDouble(c2.getColumnIndex("amount")));
						Log.i("RECORD ROW", "ISRECEIPT: " 	+ c2.getInt(c2.getColumnIndex("isreceipt")));
						Log.i("RECORD ROW", "CATEGORYID: " 	+ c2.getInt(c2.getColumnIndex("categoryid")));
						Log.i("RECORD ROW", "DATE: " 		+ c2.getInt(c2.getColumnIndex("date")));
						Log.i("RECORD ROW", "CATEGORYID: " 	+ c2.getInt(c2.getColumnIndex("id")));
						Log.i("RECORD ROW", "NAME: " 		+ c2.getString(c2.getColumnIndex("name")));
						Log.i("RECORD ROW", "DESC: " 		+ c2.getString(c2.getColumnIndex("description")));
						
						// record parameters
						Operation operation = ( c2.getInt(c2.getColumnIndex("isreceipt")) == 1 ) 
								? Operation.RECHARGE : Operation.WITHDRAW;
						
						Category category	= new Category(
								c2.getLong(c2.getColumnIndex("number")),
								c2.getString(c2.getColumnIndex("name")),
								c2.getString(c2.getColumnIndex("description")));
						
						long id 			= c2.getLong(1);
						double amount 		= c2.getDouble(c2.getColumnIndex("amount"));
						Date date 			= new Date(c2.getLong(c2.getColumnIndex("date")));
						String description	= c2.getString(3);
						// create record
						Record record = new Record(id, operation, amount, date, category, description);
						Log.i("CREATE RECORD", "record created...");
						records.add(record);
					} while( c2.moveToNext() );
				}
				
				// account parameters
				String accountOwner		= "AlexDiaz";
				long accountId 			= c.getLong(c.getColumnIndex("number"));
				Double accountBalance 	= c.getDouble(c.getColumnIndex("balance"));
				String accountDesc		= c.getString(c.getColumnIndex("description"));
				// create account
				Account account = new Account(accountOwner, accountId, accountBalance, accountDesc);
				Log.i("CREATE ACCOUNT", "account created...");
				// add records to account
				for ( Record rec : records ) {
					account.addRecord(rec);
				}
				
				// add account to set
				accounts.add(account);
			} while ( c.moveToNext() );
		}
		c.close();
		db.close();
		return accounts;
	}
	
	/**
	 * get all categories
	 */
	public Set<Category> getCategories() {
		Log.i("DBHELPER", "get categories");
		Set<Category> categories = new HashSet<Category>();
		
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.query("categories", null, null, null, null, null, null);
		
		if ( c.moveToFirst() ) {
			do {
				final long id 		= c.getLong(c.getColumnIndex("number"));
				final String name 	= c.getString(c.getColumnIndex("name"));
				final String desc 	= c.getString(c.getColumnIndex("description"));
				Log.i("CATEGORY ROW", "ID: " 			+ id);
				Log.i("CATEGORY ROW", "NAME: " 			+ name);
				Log.i("CATEGORY ROW", "DESCRIPTION: " 	+ desc);
				Category cat = new Category(id, name, desc);
				categories.add(cat);
			} while ( c.moveToNext() );
		}
		db.close();
		c.close();
		return categories;
	}
	
	/**
	 * insert user
	 */
	public long insertUser(final User user) {
		Log.i("INSERT DATA", "Insert new user");
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues cv = new ContentValues();
		cv.put("login", 	user.getLogin());
		cv.put("password",	user.getPassword());
		cv.put("firstname", user.getFirstname());
		cv.put("middlename",user.getMiddlename());
		cv.put("surname", 	user.getSurname());
		cv.put("email", 	user.getEmail());
		// insertion trying
		long id = db.insert("users", null, cv);
		db.close();
		
		return id;
	}
	
	/**
	 * get user id by number
	 */
	public long getUserIdByNumber(final long number) {
		Log.i("DBHELPER", "get user id by number");
		SQLiteDatabase db = this.getReadableDatabase();
		String selection = " number = ? ";
		String[] selectionArgs = { String.valueOf(number) };
		Cursor c = db.query("users", null, selection, selectionArgs, null, null, null);
		
		if ( c.getCount() > 0 ) {
			c.moveToFirst();
			return c.getLong(c.getColumnIndex("id"));
		}
		return -1;
	}
	
	/**
	 * get account id by number
	 */
	public long getAccountIdByNumber(final long number) {
		Log.i("DBHELPER", "get account id by number");
		SQLiteDatabase db = this.getReadableDatabase();
		String selection = " number = ? ";
		String[] selectionArgs = { String.valueOf(number) };
		Cursor c = db.query("accounts", null, selection, selectionArgs, null, null, null);
		
		if ( c.getCount() > 0 ) {
			c.moveToFirst();
			return c.getLong(c.getColumnIndex("id"));
		}
		return -1;
	}
	
	/**
	 * get category id by number
	 */
	public long getCategoryIdByName(final String name) {
		Log.i("DBHELPER", "get category id by number");
		SQLiteDatabase db = this.getReadableDatabase();
		String selection = " name = ? ";
		String[] selectionArgs = { name };
		Cursor c = db.query("categories", null, selection, selectionArgs, null, null, null);
		
		if ( c.getCount() > 0 ) {
			c.moveToFirst();
			return c.getLong(c.getColumnIndex("id"));
		}
		return -1;
	}
	
	
	/**
	 * insert account
	 */
	public long insertAccount(final User user, final Account account) {
		Log.i("INSERT DATA", "Insert new user");
		if ( user == null || account == null ) {
			return -1;
		}
		
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("number",		account.getId());
		cv.put("userid",		getUserIdByNumber(user.getId()));
		cv.put("description", 	account.getDescription());
		cv.put("balance", 		account.getBalance());
		// insertion trying
		long id = db.insert("accounts", null, cv);
		db.close();
		
		return id;
	}
	
	/**
	 * insert record
	 */
	public long insertRecord(Account account, Record record) {
		Log.i("INSERT DATA", "Insert new user");
		if ( account == null || record == null ) {
			return -1;
		}
		
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues cv = new ContentValues();
		cv.put("number",		record.getId());
		cv.put("accountid",		getAccountIdByNumber(account.getId()));
		cv.put("amount",		record.getAmount());
		cv.put("date",			record.getDateTimestamp());
		cv.put("description", 	record.getDescription());
		cv.put("isreceipt", 	( record.getOperationStr().equals("+") ) ? 1 : 0);
		cv.put("categoryid", 	getCategoryIdByName(record.getCategory().getName()));
		// insert record
		long id = db.insert("records", null, cv);
		
		// update account
		cv.clear();
		cv.put("balance", account.getBalance());
		String whereClause = " number = ? ";
		String[] whereArgs = { String.valueOf(account.getId()) };
		db.update("accounts", cv, whereClause, whereArgs);
		
		db.close();
		return id;
	}
	
	/**
	 * insert category
	 */
	public long insertCategory(Category category) {
		Log.i("INSERT DATA", "Insert new user");
		if ( category == null ) {
			return -1;
		}
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("name", 			category.getName());
		cv.put("description",	category.getDescription());
		// insertion trying
		long id = db.insert("categories", null, cv);
		
		db.close();
		return id;
	}
	
	/**
	 * get unique number
	 */
	public long getUniqueNumber(final String table) {
		Log.i("DBHELPER GET_UNIQUE_NUMBER", "get unique number");
		long unique = -1;
		SQLiteDatabase db = this.getWritableDatabase();
			
		String selection = " counter = ? ";
		String[] selectionArgs = { table };
		Cursor c = db.query("counters", null, selection, selectionArgs, null, null, null);
		
		if ( c.getCount() > 0 ) {
			c.moveToFirst();
			unique = c.getLong(c.getColumnIndex("value")) + 10;
			
			// insert into table new value
			ContentValues cv = new ContentValues();
			cv.put("value", unique);
			int updated = db.update("counters", cv, " id = ? ", new String[] { c.getString(c.getColumnIndex("id")) });
			Log.i("DBHELPER GET_UNIQUE_NUMBER", "rows updated: " + updated);
		}
		db.close();
		c.close();
		return unique;
	}
	
	/**
	 * get categories list
	 */
	public ArrayList<Map<String, Object>> getCategoryList() {
		ArrayList<Map<String, Object>> categories = new ArrayList<Map<String, Object>>();
		
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.query("categories", null, null, null, null, null, null);
		
		if ( c.moveToFirst() ) {
			do {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("id", c.getLong(c.getColumnIndex("number")));
				map.put("name", c.getString(c.getColumnIndex("name")));
				map.put("description", c.getString(c.getColumnIndex("description")));
				categories.add(map);
			} while ( c.moveToNext() );
		}
		db.close();
		c.close();
		return categories;
	}
	
	/**
	 * get accounts list
	 */
	public ArrayList<Map<String, Object>> getAccountList(final User user) {
		Log.i("DBHELPER", "get account list");
		ArrayList<Map<String, Object>> accounts = new ArrayList<Map<String, Object>>();
		if ( user == null ) {
			return accounts;
		}
		
		SQLiteDatabase db = this.getReadableDatabase();
		String selection = " userid = ? ";
		String[] selectionArgs = new String[] { String.valueOf(this.getUserIdByNumber(user.getId())) };
		Cursor c = db.query("accounts", null, selection, selectionArgs, null, null, null);
		
		if ( c.moveToFirst() ) {
			do {
				Map<String, Object> map = new HashMap<String, Object>();
				Log.i("GET_AACOUNT_LIST", "number: " + c.getInt(c.getColumnIndex("number")));
				map.put("id", c.getLong(c.getColumnIndex("number")));
				map.put("description", c.getString(c.getColumnIndex("description")));
				accounts.add(map);
			} while ( c.moveToNext() );
		}
		db.close();
		c.close();
		return accounts;
	}
	
	/**
	 * delete record
	 */
	public int deleteRecord(final Account account, final Record record) {
		Log.i("DBHELPER", "delete record");
		SQLiteDatabase db = this.getWritableDatabase();
		// update account
		ContentValues cv = new ContentValues();
		cv.put("balance", account.getBalance());
		String whereClause = " number = ? ";
		String[] whereArgs = { String.valueOf(account.getId()) };
		int updated = db.update("accounts", cv, whereClause, whereArgs);
		if ( updated > 0 ) {
			// we updated - delete record
			whereClause = " number = ? "; 
			return db.delete("records", whereClause, new String[] { String.valueOf(record.getId()) });
		}
		return -1;
	}
	
	/**
	 * delete account
	 */
	public int deleteAccount(final Account account) {
		Log.i("DBHELPER", "delete account");
		SQLiteDatabase db = this.getWritableDatabase();
		
		// delete records
		int records = account.getRecordCount();
		String whereClause = " accountid = ? ";
		String[] whereArgs = { String.valueOf(this.getAccountIdByNumber(account.getId())) };
		int removed = db.delete("records", whereClause, whereArgs);
		Log.i("DELETEACCOUNT", "records: " + records + " removed: " + removed);
		
		// now delete account
		whereClause = " number = ? ";
		return db.delete("accounts", whereClause, new String[] { String.valueOf(account.getId()) });
	}
	
	/**
	 * test data for a initial data manipulation
	 */
	public void loadTestData() {
		Log.i("DBHELPER", "load test data");
		SQLiteDatabase db = this.getReadableDatabase();
		
		recreateTables();
		
		ContentValues cv = new ContentValues();
		cv.put("login",			"AlexDiaz");
		cv.put("password",		PasswordCodec.getMD5("liberty"));
		cv.put("number",		10);
		cv.put("firstname", 	"Alex");
		cv.put("surname", 		"Balyschev");
		cv.put("middlename", 	"Vladimirovich");
		cv.put("email", 		"alexbalu-alpha7@mail.ru");
		long id = db.insert("users", null, cv);
		Log.i("INSERT DATA", "ID: " + id);
		
		cv.clear();
		cv.put("login",			"HeyMike");
		cv.put("number",		20);
		cv.put("password",		PasswordCodec.getMD5("honor"));
		cv.put("firstname", 	"Mike");
		cv.put("surname", 		"Garcia");
		cv.put("middlename", 	"Fernando");
		cv.put("email", 		"heymikey@gmail.com");
		id = db.insert("users", null, cv);
		Log.i("INSERT DATA", "ID: " + id);
		
		//recreateTable("accounts");
		cv = new ContentValues();
		cv.put("number",		10);
		cv.put("userid",		1);
		cv.put("description", 	"personal account");
		cv.put("balance", 		2400.0);
		id = db.insert("accounts", null, cv);
		Log.i("INSERT DATA", "ID: " + id);
		
		cv.clear();
		cv.put("number",		12);
		cv.put("userid",		1);
		cv.put("description", 	"family account");
		cv.put("balance", 		6720.0);
		id = db.insert("accounts", null, cv);
		Log.i("INSERT DATA", "ID: " + id);
		
		//recreateTable("records");
		cv = new ContentValues();
		cv.put("number",		120);
		cv.put("accountid",		1);
		cv.put("date",		171019311);
		cv.put("amount",		340.0);
		cv.put("description", 	"purchase in shop");
		cv.put("isreceipt", 	0);
		cv.put("categoryid", 	1);
		id = db.insert("records", null, cv);
		Log.i("INSERT DATA", "ID: " + id);
		
		cv.clear();
		cv.put("number",		122);
		cv.put("accountid",		1);
		cv.put("date",		151019311);
		cv.put("amount",		520.0);
		cv.put("description", 	"purchase in cafe");
		cv.put("isreceipt", 	0);
		cv.put("categoryid", 	1);
		id = db.insert("records", null, cv);
		Log.i("INSERT DATA", "ID: " + id);
		
		cv.clear();
		cv.put("number",		124);
		cv.put("accountid",		2);
		cv.put("amount",		80.0);
		cv.put("date",		191019311);
		cv.put("description", 	"just one bribe");
		cv.put("isreceipt", 	1);
		cv.put("categoryid", 	4);
		id = db.insert("records", null, cv);
		Log.i("INSERT DATA", "ID: " + id);
		
		cv.clear();
		cv.put("number",		125);
		cv.put("accountid",		2);
		cv.put("amount",		110.0);
		cv.put("date",		198019311);
		cv.put("description", 	"another one bribe");
		cv.put("isreceipt", 	1);
		cv.put("categoryid", 	4);
		id = db.insert("records", null, cv);
		Log.i("INSERT DATA", "ID: " + id);
		
		//recreateTable("categories");
		cv = new ContentValues();
		cv.put("number",		10);
		cv.put("name",			"food");
		cv.put("description", 	"food category");
		id = db.insert("categories", null, cv);
		Log.i("INSERT DATA", "ID: " + id);
		
		cv.clear();
		cv.put("number",		20);
		cv.put("name",			"clothes");
		cv.put("description", 	"clothes category");
		id = db.insert("categories", null, cv);
		Log.i("INSERT DATA", "ID: " + id);
		
		cv.clear();
		cv.put("number",		30);
		cv.put("name",			"fuel");
		cv.put("description", 	"fuel category");
		id = db.insert("categories", null, cv);
		Log.i("INSERT DATA", "ID: " + id);
		
		cv.clear();
		cv.put("number",		40);
		cv.put("name",			"other");
		cv.put("description", 	"other category");
		id = db.insert("categories", null, cv);
		Log.i("INSERT DATA", "ID: " + id);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d("CREATE emanagerDB", "--- onCreate database ---");
		db.execSQL(SQL_DB_STRUCTURE);
	}

	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
	
	public void recreateTables(/*final String table*/) {
		
		SQLiteDatabase db = this.getWritableDatabase();
		
		Log.i("RECREATE TABLES", "recreate tables");
		//db.execSQL("DROP TABLE IF EXISTS " + table);
		
		db.execSQL("DROP TABLE IF EXISTS  users");
		db.execSQL("DROP TABLE IF EXISTS  accounts");
		db.execSQL("DROP TABLE IF EXISTS  records");
		db.execSQL("DROP TABLE IF EXISTS  categories");
		db.execSQL("DROP TABLE IF EXISTS  counters");
		
		db.execSQL("CREATE TABLE users ("
				+ " id INTEGER PRIMARY KEY AUTOINCREMENT," 
				+ " number INTEGER, "
				+ " login TEXT, "
				+ " password TEXT, " 
				+ " firstname TEXT, "
				+ " surname TEXT, "
				+ " middlename TEXT, "
				+ " email TEXT " 
				+ " );");
		
		db.execSQL("CREATE TABLE records ("
				+ " id INTEGER PRIMARY KEY AUTOINCREMENT," 
				+ " number INTEGER, "
				+ " accountid INTEGER, " 
				+ " description TEXT, "
				+ " amount REAL, "
				+ " isreceipt INTEGER, "
				+ " categoryid INTEGER, "
				+ " date INTEGER "
				+ " );");
		
		db.execSQL("CREATE TABLE categories ("
				+ " id INTEGER PRIMARY KEY AUTOINCREMENT," 
				+ " number INTEGER, "
				+ " name TEXT, "
				+ " description TEXT " 
				+ " );");
		
		db.execSQL("CREATE TABLE accounts ("
				+ " id INTEGER PRIMARY KEY AUTOINCREMENT," 
				+ " number INTEGER, "
				+ " userid INTEGER, " 
				+ " description TEXT, "
				+ " balance REAL "
				+ " );");
		
		// create table counters
		db.execSQL("CREATE TABLE counters ("
				+ " id INTEGER PRIMARY KEY AUTOINCREMENT," 
				+ " counter TEXT, "
				+ " value INTEGER "
				+ " );");
		db.execSQL("INSERT INTO counters(counter, value) VALUES ('users', 0) ;");
		db.execSQL("INSERT INTO counters(counter, value) VALUES ('accounts', 0) ;");
		db.execSQL("INSERT INTO counters(counter, value) VALUES ('records', 0) ;");
		db.execSQL("INSERT INTO counters(counter, value) VALUES ('categories', 0) ;");
	}
}