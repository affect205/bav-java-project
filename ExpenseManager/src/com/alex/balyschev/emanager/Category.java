/**
 * @author alexbalu-alpha7@mail.ru
 */
package com.alex.balyschev.emanager;

public class Category {
	
	private final long id;
	private String name;
	private String description;
	
	public Category(final long id, final String name, final String description) {
		this.id		= id;
		this.name 	= name;
		this.description = description;
	}
	
	public long getId() {
		return this.id;
	}
	
	public void setName(final String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setDescription(final String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
	
	@Override
	public boolean equals(Object obj) {
		if ( obj == null ) {
			return false;
		}
		if ( this == obj ) {
			return true;
		}
		if ( this.getClass() != obj.getClass() ) {
			return false;
		}
		Category cat = (Category)obj;
		if ( id != cat.getId() ) {
			return false;
		}
		if ( ! name.equals(cat.getName()) ) {
			return false;
		}
		return true;
	}
	
	@Override
	public int hashCode() {
		final int prime = 11;
		int result = 1;
		result = prime * result + Long.valueOf(id).hashCode();
		result = prime * result + name.hashCode();
		return result;
	}
}