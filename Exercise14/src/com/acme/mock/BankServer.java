package com.acme.mock;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;

import com.acme.domain.bank.Bank;
import com.acme.domain.bank.Client;

public class BankServer {
	ServerSocket providerSocket;
	Socket connection = null;
	ObjectOutputStream out;
	ObjectInputStream in;
	String message;
	
	// active client
	private Client active_client = null;
	private boolean logined = false;
	
	private int port = 10002;
	
	private Bank bank = null;

	public BankServer(final Bank bank, final int port) {
		this.bank = bank;
		this.port = port;
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
						
					// split the message on command and value
					StringTokenizer token = new StringTokenizer(message, ":");
					String cmd = token.nextToken();
					String val = token.nextToken();
					
					if ( cmd.equals("-login") ) {
						 
						System.out.println("server> request on login... " + val);
						
						if ( logined == true && active_client != null 
								&& active_client.getName().equals(val) ) {	
							
							// already login
							sendMessage("Client already loggined...");
						} else if ( logined == false && bank.containsClient(val) ) {
							
							// client exists - login
							active_client = bank.getClientByName(val);
							logined = true;
							sendMessage("client is login...");
							
						} else {
							// client doesn't exist - no login
							sendMessage("no such client...");
						}
						
						// close command
						sendMessage("-bye");
					}
					
					
					if ( logined == true && active_client != null ) {
						
						// access to commands
						if ( cmd.equals("-info") ) {
							
							// info command
							sendMessage("Balance is: " + active_client.toString());
							sendMessage("-bye");
						
						} else if ( cmd.equals("-exit") ) {
							
							// exit command
							active_client = null;
							logined = false;
							sendMessage("-bye");
						
						} else {
							
							// unknown command
							sendMessage("unknown command");
							sendMessage("-bye");						
						}
					
					} else {
						
						// no login - decline request
						sendMessage("client is not logined");
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
