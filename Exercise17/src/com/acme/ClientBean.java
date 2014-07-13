package com.acme;

import java.io.Serializable;

public class ClientBean implements Serializable  {
	
	private boolean male;
	private String name;
	private String surname;
	private Address address;
	
	public ClientBean() {
		
	}
	
	public void setMale(final boolean male) {
		this.male = male;
	}
	
	public boolean getMale() {
		return this.male;
	}
	
	public void setName(final String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setSurname(final String surname) {
		this.surname = surname;
	}
	
	public String getSurname() {
		return this.surname;
	}
	
	public void setAddress(final Address address) {
		this.address = address;
	}
	
	public Address getAddress() {
		return this.address;
	}

}
