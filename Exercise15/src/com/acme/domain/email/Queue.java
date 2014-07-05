package com.acme.domain.email;

public interface Queue {

	public IEmail getEmail();

	public void addEmail(IEmail iEmail);

	public void close();
	
	public void deleteLast(IEmail email);
	
	public int getSize();
}
