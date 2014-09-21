/**
 * @class AppController
 * @brief This class contains methods and data for application activities  
 * @author alexbalu-alpha7@mail.ru
 */

package com.alex.balyschev.emanager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.alex.balyschev.emanager.Record.DateComparator;

public class AppController {
	private AppController() {
		
	}
	
	/**
	 * check user's existence
	 */
	public static boolean userExists(final ExpenseManager emanager, final String login, final String password) {
		Set<String> users = emanager.getUserNames();
		String md5Password = PasswordCodec.getMD5(password);
		
		for ( String name : users ) {
			User user = emanager.getUser(name);
			final String userLogin 		= user.getLogin();
			final String userPassword 	= user.getPassword();
			
			if ( userLogin.equals(login) && userPassword.equals(md5Password) ) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * print text error in any form
	 * @param v
	 * @param error
	 */
	public static void printError(final TextView v, final String error) {
		v.setVisibility(View.VISIBLE);
		v.setText(error);
	}
		
	/**
	 * get account list
	 */
	public static ArrayList<Map<String, Object>> getAccountList(final ExpenseManager emanager, User user) {
		ArrayList<Map<String, Object>> accounts = new ArrayList<Map<String, Object>>();
		if ( user == null ) {
			return accounts;
		}
		for ( Account account : emanager.getAccounts(user) ) {
			Map<String, Object> map = new HashMap<String, Object>();
			Log.i("GET_AACOUNT_LIST", "number: " + account.getId());
			map.put("id", account.getId());
			map.put("description", account.getDescription());
			accounts.add(map);
		}
		return accounts;
	}
	
	/**
	 * get category list
	 */
	public static ArrayList<Map<String, Object>> getCategoryList(final ExpenseManager emanager) {
		ArrayList<Map<String, Object>> categories = new ArrayList<Map<String, Object>>();
		for ( Category cat : emanager.getCategories() ) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", cat.getId());
			map.put("name", cat.getName());
			map.put("description", cat.getDescription());
			categories.add(map);
		}
		return categories;
	}
	
	/**
	 * get account data by user 
	 */
	public static ArrayList<Map<String, String>> getAccountData(final ExpenseManager emanager, final String owner) {
		// account data
		ArrayList<Map<String, String>> accountData = new ArrayList<Map<String, String>>(); 
		
		// accounts
		Set<Account> userAccounts = emanager.getAccounts(emanager.getUser(owner));
		
		if ( userAccounts.isEmpty() ) {
			Log.i("ACCOUNT INFO", "accounts are empty");
			return accountData;
		}
		
		String strId 		= "ID:";
		String strBalance 	= "balance:";
		String strTitle 	= "title:";
		String strCount 	= "records:";
		
		for ( Account account : userAccounts ) {
			if ( account.getOwner().equals(owner) ) {
				// collect account data
				Log.i("ACCOUNT INFO", "account are not empty");
				Map<String, String> map = new HashMap<String, String>();
				map.put(ExpenseManager.LV_KEY_ACCOUNT_ID,		strId 		+ " " + String.valueOf(account.getId()));
				map.put(ExpenseManager.LV_KEY_ACCOUNT_BALANCE,	strBalance 	+ " " + String.valueOf(account.getBalance()));
				map.put(ExpenseManager.LV_KEY_ACCOUNT_TITLE, 	strTitle 	+ " " + account.getDescription());
				map.put(ExpenseManager.LV_KEY_ACCOUNT_COUNT, 	strCount 	+ " " + String.valueOf(account.getRecordCount()));
				map.put(ExpenseManager.LV_KEY_ACCOUNT_NUMBER, 	String.valueOf(account.getId()));
				accountData.add(map);
			}
		}
		return accountData;
	}
	
	/**
	 * get record data by user
	 */
	public static ArrayList<ArrayList<Map<String, String>>> getRecordData(final ExpenseManager emanager, final String owner) {
		// record data
		ArrayList<ArrayList<Map<String, String>>> recordData = new ArrayList<ArrayList<Map<String, String>>>(); 
	
		String strId 		= "ID:";
		String strTitle 	= "title:";
		String strDate	 	= "date:";
		
		for ( Account account : emanager.getAccounts(emanager.getUser(owner)) ) {
			// account records
			ArrayList<Map<String, String>> recordDataItem = new ArrayList<Map<String, String>>(); 
			
			// get record list from set and sort it
			List<Record> accList = new ArrayList<Record>(account.getRecords());
			Record.DateComparator comparator = new Record.DateComparator(); 
			Collections.sort(accList, comparator);
			
			for ( Record record : accList ) {
				// add records to item
				Map<String, String> map = new HashMap<String, String>();
								
				// add data to item
				map.put(ExpenseManager.LV_KEY_RECORD_ID, 		strId 		+ " " + String.valueOf(record.getId()));
				map.put(ExpenseManager.LV_KEY_RECORD_AMOUNT, 	record.getOperationStr() + String.valueOf(record.getAmount()));
				map.put(ExpenseManager.LV_KEY_RECORD_TITLE, 	strTitle 	+ " " + record.getDescription());
				map.put(ExpenseManager.LV_KEY_RECORD_DATE, 		strDate 	+ " " + record.getDateStr());
				map.put(ExpenseManager.LV_KEY_RECORD_NUMBER, 	String.valueOf(record.getId()));
				recordDataItem.add(map);
			}
			recordData.add(recordDataItem);
		}
		return recordData;
	}
	
	/**
	 * get category by id
	 */
	public static Category getCategoryById(final ExpenseManager emanager, final long id) {
		for ( Category cat : emanager.getCategories() ) {
			if ( cat.getId() == id ) {
				return cat;
			}
		}
		return null;
	}
}