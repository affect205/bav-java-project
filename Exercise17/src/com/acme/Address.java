package com.acme;

import java.io.Serializable;

public class Address implements Serializable  {

	private String region;
	private String city;
	private String street;
	
	public Address() {
		
	}
	
	public void setRegion(final String region) {
		this.region = region;
	}
	
	public String getRegion() {
		return this.region;
	}
	
	public void setCity(final String city) {
		this.city = city;
	}
	
	public String getCity() {
		return this.city;
	}
	
	public void setStreet(final String street) {
		this.street = street;
	}
	
	public String getStreet() {
		return this.street;
	}
	
}
