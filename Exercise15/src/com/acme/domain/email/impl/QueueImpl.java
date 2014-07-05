package com.acme.domain.email.impl;

import java.util.ArrayList;
import java.util.List;

import com.acme.domain.email.IEmail;
import com.acme.domain.email.Queue;

//TODO: implement the queue which is list-based.
public class QueueImpl implements Queue {

	List<IEmail> mails = new ArrayList<IEmail>();

	public void addEmail(IEmail email) {
		
		// TODO implement
		mails.add(email);
	}

	public void close() {
		
		// TODO implement
		System.out.println("Close queue...");
	}

	public IEmail getEmail() {
		
		// TODO implement
		if ( ! mails.isEmpty() ) {
			return mails.get(mails.size()-1);
		}
		return null;
	}
	
	public void deleteLast(IEmail email) {
		
		if ( mails.contains(email) ) {
			
			mails.remove(email);
		}
	}
	
	public int getSize() {
		
		return mails.size();
	}
	
}
