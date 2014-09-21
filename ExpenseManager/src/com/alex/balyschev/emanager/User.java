/**
 * @author alexbalu-alpha7@mail.ru
 */
package com.alex.balyschev.emanager;

import java.util.HashSet;
import java.util.Set;

public class User {
	
	// firstname, login, password, email
	private long id;
	private String firstname;
	private String surname;
	private String middlename;
	private String login;
	private String password;
	private String email;
	
	/**
	 * Constructor
	 */
	public User(final long id, final String login, final String password)  {
		this.id			= id;
		this.login 		= login;
		this.password 	= password;
		this.firstname	= "";
		this.middlename	= "";
		this.surname	= "";
		this.email		= "";
	}
	
	/**
	 * Constructor
	 */
	public User(
			final long id,
			final String login, 
			final String password,
			final String firstname,
			final String surname,
			final String middlename,
			final String email)  {
		this.id			= id;
		this.login 		= login;
		this.password 	= password;
		this.firstname	= firstname;
		this.surname	= surname;
		this.middlename	= middlename;
		this.email		= email;
	}
	
	/**
	 * get user name
	 * @return String
	 */
	public String getName() {
		return login;
	}
	
	public String getLogin() {
		return login;
	}
	
	public String getPassword() {
		return password;
	}
	
	public long getId() {
		return id;
	}
	
	public String toString() {
		return "id: " + id + " login: " + login;
	}
	
	public String getFirstname() {
		return this.firstname;
	}
	
	public String getSurname() {
		return this.surname;
	}
	
	public String getMiddlename() {
		return this.middlename;
	}
	
	public String getEmail() {
		return this.email;
	}
	
	@Override
	public boolean equals(Object obj) {
		if ( this == obj ) {
			return true;
		}
		if ( this == null ) {
			return false;
		}
		if (  this.getClass() != obj.getClass() ) {
			return false;
		}
		// compare fields
		User user = (User) obj;
		
		if ( this.id != user.getId() ) {
			return false;
		}
		if ( ! this.login.equals(user.getLogin()) ) {
			return false;
		}
		if ( ! this.password.equals(this.getPassword()) ) {
			return false;
		}
		return true;
	}
	
	@Override
	public int hashCode() {
		final int prime = 11;
		int result = 1;
		result = prime * result + Long.valueOf(id).hashCode();
		result = prime * result + login.hashCode();
		result = prime * result + password.hashCode();
				
		return result;
	}
}