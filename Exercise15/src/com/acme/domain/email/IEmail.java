package com.acme.domain.email;


public interface IEmail {
	
	public String getSubject();

	public String getFrom();

	public String getTo();

	public String getMessage();

	public void setSubject(String subject);

	public void setFrom(String from);

	public void setTo(String to);

	public void setMessage(String message);

	public Client getClient();

}
