package com.acme.mock;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.StringTokenizer;

import com.acme.domain.bank.Bank;

public class BankServer {
	ServerSocket providerSocket;
	Socket connection = null;
	ObjectOutputStream out;
	ObjectInputStream in;
	String message;
	
	// connected clients list
	private ArrayList<String> clients;
	
	private int port = 10002;
	
	private Bank bank = null;

	public BankServer(final Bank bank, final int port) {
		this.bank = bank;
		this.port = port;
		clients = new ArrayList<String>();
	}

	public void run() {
		try {
			// 1. creating a server socket
			providerSocket = new ServerSocket(port, 0);
			
			// 2. Wait for connection
			System.out.println("server> Waiting for connection...");
			connection = providerSocket.accept();
			System.out.println("server> Connection received from "
					+ connection.getInetAddress().getHostName());
			
			// 3. get Input and Output streams
			out = new ObjectOutputStream(connection.getOutputStream());
			out.flush();
			in = new ObjectInputStream(connection.getInputStream());
			sendMessage("Connection successful");
			
			// 4. The two parts communicate via the input and output streams
			do {
				try {
					message = (String) in.readObject();
					
					if ( message == null ) {
						continue;
					}
					
					System.out.println("server> received: " + message);
											
					if ( message.contains("-login") ) {
						 
						// login command: split the message on command and value
						StringTokenizer token = new StringTokenizer(message, ":");
						String cmd = token.nextToken();
						String val = token.nextToken();
						
						System.out.println("server> request on login... " + val);
						
						if ( clients.contains(val) ) {	
							// already login
							sendMessage("Client already loggined...");
						} else if ( bank.contains_client(val) ) {
							// client exists - login
							clients.add(val);
							sendMessage("client is login...");
						} else {
							// client doesn't exist - no login
							sendMessage("no such client...");
						}
						
						// close command
						sendMessage("-bye");
					
					} else if ( message.contains("-balance") ) {
						
						// balance command
						sendMessage("Balance is: 500");
						sendMessage("-bye");
					
					} else if (message.equals("-exit")) {
						
						// exit command
						clients.clear(); 
						sendMessage("-bye");
					
					} else {
						
						// unknown command
						sendMessage("unknown command");
						sendMessage("-bye");						
					} 
						
				} catch (ClassNotFoundException classnot) {
					System.err.println("Data received in unknown format");
				}
			} while ( !message.equals("-bye") );
		} catch (IOException ioException) {
			ioException.printStackTrace();
			//System.err.println(ioException.getMessage());
		} finally {
			// 4: Closing connection
			try {
				providerSocket.close();
				out.close();
				in.close();
			} catch (IOException ioException) {
				ioException.printStackTrace();
				//System.err.println(ioException.getMessage());
			}
		}
	}

	void sendMessage(final String msg) {
		try {
			out.writeObject(msg);
			out.flush();
			System.out.println("server> send: " + msg);
		} catch (IOException ioException) {
			ioException.printStackTrace();
			//System.err.println(ioException.getMessage());
		}
	}
	
	public void startServer() {
		while ( true ) {
			this.run();
		}
	}
	
}
