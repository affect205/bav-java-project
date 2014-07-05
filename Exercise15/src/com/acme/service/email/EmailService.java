package com.acme.service.email;

import com.acme.domain.email.IEmail;
import com.acme.domain.email.Queue;
import com.acme.domain.email.impl.QueueImpl;

public class EmailService implements Runnable {

	private static Queue queue = new QueueImpl();
	private static boolean ready = false;
	
	private static EmailService instance = new EmailService();

	public static EmailService getEmailService() {
		
		return instance;
	}
	
	public static void sendNotificationEmail(IEmail email) {
			
		// add to queue
		System.out.println("Add email...");
		
		synchronized (queue) {
			
			queue.addEmail(email);
			queue.notifyAll();
			ready = true;
		}
	}
	
	public EmailService() {
		
		System.out.println("Constructor...");
		
		// create and run thread
		Thread thread = new Thread(this);
		thread.start();
	}
	
	public void run() {
				
		synchronized (queue) {
			
			while ( !ready ) {
			
				try {
					queue.wait();
				} catch (InterruptedException e) {
					System.out.println(e.getMessage());
				}
			}
			
			while ( queue.getSize() > 0 ) {
				
				// get email from queue 
				System.out.println("Get email...");
				IEmail email = queue.getEmail();
				
				// send email
				System.out.println("Send email...");
				sendEmail(email);
				
				// delete email from queue
				System.out.println("Delete email...");
				queue.deleteLast(email);
			
				System.out.println("Mails left " + queue.getSize() + "...");
			}
			
			// it's not ready to send
			ready = false;
			queue.notifyAll();
		}
	}
	
	public void close() {
		
		queue.close();
	}
	
	public void sendEmail(IEmail email) {
		
		System.out.println("The message has been sent...");
	}
}
