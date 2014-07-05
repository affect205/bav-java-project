package com.acme.domain.email.impl;

import com.acme.domain.email.Client;
import com.acme.domain.email.IEmail;

public class EmailImpl implements IEmail {

	private String from;
	private String to;
	private String subject;
	private String message;

	private Client client;

	public Client getClient() {
		return client;
	}

	public void setClient(final Client client) {
		this.client = client;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(final String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(final String to) {
		this.to = to;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(final String subject) {
		this.subject = subject;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(final String message) {
		this.message = message;
	}

	@Override
	public String toString() {

		return "From: " + from + " To: " + to + " Subject:" + subject
				+ " Message:" + message;
	}

}
