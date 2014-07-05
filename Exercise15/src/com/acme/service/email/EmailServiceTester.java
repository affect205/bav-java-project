package com.acme.service.email;

import java.util.Random;

import com.acme.domain.email.Client;
import com.acme.domain.email.IEmail;
import com.acme.domain.email.impl.EmailImpl;

public class EmailServiceTester {

	private static String[] emailsTo = {
		"testmail1@mail.com", "testmail2@mail.com",
		"testmail3@mail.com", "testmail4@mail.com"
	};

	private static String[] subjects = {	
			"Alex", "John", "Mike", "Stuart"
	};
	
	private static String[] messages = {	
			"Hi, Alex", "Hi, John", "Hi, Mike", "Hi, Stuart"
	};
	
	public static void main(final String[] args) {
		new EmailServiceTester().go();
	}

	private void go() {
		EmailService service = EmailService.getEmailService();

		System.out.println("*******First email block*******");
		doSending(service);
				
		System.out.println("*******First email block*******");
		doSending(service);
		
		try {
			Thread.sleep(30000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		service.close();
	}
	
	
	private void doSending(EmailService service) {
		
		for ( int i=0; i < 4; ++i  ) {
			
			EmailImpl obj = new EmailImpl();
			obj.setClient(new Client());
			obj.setFrom("mymail@mai.ru");
			obj.setTo(emailsTo[i]);
			obj.setSubject(subjects[i]);
			obj.setMessage(messages[i]);
			
			// send mails
			service.sendNotificationEmail(obj);
		}
		
	}
}
