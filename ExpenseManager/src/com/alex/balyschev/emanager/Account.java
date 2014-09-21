/**
 * @author alexbalu-alpha7@mail.ru
 */
package com.alex.balyschev.emanager;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Account {
	
	// owner name
	private String owner;
	// account id
	private final long id;
	// description
	private String description;
	// balance
	private double balance;
	// records
	private Set<Record> records;
	
	
	/**
	 * Constructor
	 */
	public Account(final String owner, final long id, final double balance, final String description) {
			this.records	= new HashSet<Record>();
			this.owner		= owner;
			this.id			= id;
			this.balance 	= balance;
			this.description= description;
	}
	
	/**
	 * get account id
	 * return Set<Record>
	 */
	public long getId() {
		return id;
	}
	
	/**
	 * get account owner
	 * return Set<Record>
	 */
	public String getOwner() {
		return owner;
	}
		
	/**
	 * add record to account
	 */
	public boolean addRecord(Record record) {
		if ( record == null ) {
			return false;
		}
		// update balance
		this.balance += ( record.getOperationStr().equals("+") ) 
				? record.getAmount() : -record.getAmount();
		return records.add(record);
	}
	
	/**
	 * remove record from account
	 */
	public boolean removeRecord(Record record) {
		// update balance
		boolean res = records.remove(record);
		if ( res == true ) {
			this.balance += ( record.getOperationStr().equals("+") )
					? -record.getAmount() : record.getAmount();
		}
		return res;
	}
	
	/**
	 * get account records
	 */
	public Set<Record> getRecords() {
		return records;
	}
	
	/**
	 * get record count
	 */
	public int getRecordCount() {
		return records.size();
	}
	
	/**
	 * get description
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * get balance
	 */
	public double getBalance() {
		return balance;
	}
	
	public String toString() {
		return "id: " + id + " balance: " + balance + " desc: " + description;
	}
		
	@Override
	public boolean equals(Object obj) {
		if ( this == obj ) {
			return true;
		}
		if ( obj == null ) {
			return false;
		}
		if ( this.getClass() != obj.getClass() )  {
			return false;
		}
		// compare fields
		Account account = (Account) obj;
		if ( id != account.getId() ) {
			return false;
		}
		if ( balance != account.getBalance() ) {
			return false;
		}
		if ( ! owner.equals(account.getOwner()) ) {
			return false;
		}
		if ( getRecordCount() != account.getRecordCount() ) {
			return false;
		}		
		return true;
	}
	
	@Override
	public int hashCode() {
		final int prime = 11;
		int result = 1;
		result = prime * result + Long.valueOf(id).hashCode();
		result = prime * result + Double.valueOf(balance).hashCode();
		result = prime * result + owner.hashCode();
		result = prime * result + getRecordCount();
		return result;
	}
} 