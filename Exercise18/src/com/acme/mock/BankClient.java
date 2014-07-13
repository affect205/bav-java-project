package com.acme.mock;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class BankClient {

	Socket requestSocket;
	ObjectOutputStream out;
	ObjectInputStream in;
	String message;
	private final int port = 10002;

	public static void main(final String args[]) {
		BankClient client = new BankClient();
		
		System.out.println("GB>Global Bank application entrance...");
		System.out.println("GB>Login (-login <name>)");
				
		while (true) {
			
			Scanner input = new Scanner(System.in);
			//String line = input.nextLine();
			String cmd = input.next();
			String val = input.next();
							
			if ( cmd.equals("-exit") ) {
					
				// exit command
				System.out.println("client> Exit from bank...");
				
				// close connection
				client.run("-exit", "sm");
				break;
					
			} else {
				
				// other command
				System.out.println("client> Do request (" + cmd + ")...");
				client.run(cmd, val);
			} 
		}	
	}
	
	/**
	 * Constructor
	 */
	public BankClient() {
	}

	public void run(String cmd, String val) {
		try {
			// 1. creating a socket to connect to the server
			requestSocket = new Socket("localhost", port);
			System.out.println("GB> Connected to localhost in port " + port);
			
			// 2. get Input and Output streams
			out = new ObjectOutputStream(requestSocket.getOutputStream());
			out.flush();
			in = new ObjectInputStream(requestSocket.getInputStream());
			
			// 3: Communicating with the server
			do {
				try {
					sendMessage(cmd + ":" + val);
					message = (String) in.readObject();
					System.out.println("client> received: " + message);
					if ( message.equals("-bye") ) {
						sendMessage("-bye");
					}
				} catch (ClassNotFoundException classNot) {
					System.err.println("data received in unknown format");
				}
			} while ( !message.equals("-bye") );
		} catch (UnknownHostException unknownHost) {
			System.err.println("You are trying to connect to an unknown host!");
		} catch (IOException ioException) {
			ioException.printStackTrace();
		} finally {
			// 4: Closing connection
			try {
				in.close();
				out.close();
				requestSocket.close();
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}
	}

	public void sendMessage(final String msg) {
		try {
			out.writeObject(msg);
			out.flush();
			System.out.println("client> send: " + msg);
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}
}
