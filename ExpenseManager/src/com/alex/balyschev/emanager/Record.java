/**
 * @author alexbalu-alpha7@mail.ru
 */
package com.alex.balyschev.emanager;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

public class Record {
	
	// account operations
	public enum Operation { 
		WITHDRAW, RECHARGE 
	}
	
	// operation type
	Operation operation;
	// amount
	private final double amount;
	// record date
	private Date date;
	// date format
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd-hh:mm:ss");
	// account id
	private final long id;
	// description
	private String description;
	// category
	private Category category;
	
	/**
	 * Constructor
	 */
	public Record(final long id, 
			final Operation operation, 
			final double amount, 
			final Date date, 
			final Category category,
			final String description) {	
		this.id 		= id;
		this.operation	= operation;
		this.amount 	= amount;
		this.date 		= date;
		this.category	= category;
		this.description = description;
	}
	
	/**
	 * get record id
	 * @return long id
	 */
	public long getId() {
		return id;
	}
	
	/**
	 * get record description
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * get record amount
	 */
	public double getAmount() {
		return amount;
	}
	
	/**
	 * get record operation
	 */
	public String getOperationStr() {
		return ( operation == Operation.RECHARGE ) ? "+" : "-";
	}
	
	public Operation getOperation() {
		return this.operation;
	}
	
	/**
	 * get record date
	 */
	public String getDateStr() {
		return dateFormat.format(date);
	}
	
	public Date getDateObj() {
		return date;
	}
	
	/**
	 * get record category
	 */
	public Category getCategory() {
		return category;
	}
	
	/**
	 * get date timestamp
	 */
	public long getDateTimestamp() {
		return date.getTime();
	}
	
	/**
	 *  java.util.Date time=new java.util.Date((long)timeStamp*1000);

	 *  DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	 *  Date date = dateFormat.parse("23/09/2007");
	 *  long time = date.getTime();
	 *  new Timestamp(time);
	 */
	
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
		Record record = (Record) obj;
		if ( id != record.getId() ) {
			return false;
		}
		if ( getOperation() == record.getOperation() ) {
			return false;
		}
		if ( amount != record.getAmount() ) {
			return false;
		}
		if ( ! date.equals(record.getDateObj()) ) {
			return false;
		}
		return true;
	}
	
	@Override
	public int hashCode() {
		final int prime = 11;
		int result = 1;
		result = prime * result + Long.valueOf(id).hashCode();
		result = prime * result + Double.valueOf(amount).hashCode();
		result = prime * result + getOperation().hashCode();
		result = prime * result + date.hashCode();
		return result;
	}
	
	/**
	 * @author Alex Diaz
	 * @brief date comparator (by desc)
	 * 
	 */
	static class DateComparator implements Comparator<Record> {
		public enum Order { ASC, DESC }
		private Order sortby = Order.DESC;
		
		@Override 
		public int compare(Record arg1, Record arg2) {
			switch(sortby) {
				case DESC:
					if ( arg1.getDateTimestamp() < arg2.getDateTimestamp() ) {
						return 1;
					}	
					if ( arg1.getDateTimestamp() > arg2.getDateTimestamp() ) {
						return -1;
					}
					return 0;
				
				case ASC:
					if ( arg1.getDateTimestamp() > arg2.getDateTimestamp() ) {
						return 1;
					}	
					if ( arg1.getDateTimestamp() < arg2.getDateTimestamp() ) {
						return -1;
					}
					return 0;
			}
			return 0;
		}
		public void setSorting(Order order) {
			sortby = order;
		}
	}
}