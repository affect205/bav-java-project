/**
 * @author alexbalu-alpha7@mail.ru
 */
package com.abalyschev.mychat;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ChatThreadedClient {

    protected static Logger log = LoggerFactory.getLogger(ChatThreadedClient.class);

    public static final int PORT = 19000;
    public static final String HOST = "localhost";
    private static final String EXIT = ".exit";

    public static void main(String[] args) throws Exception {
        ChatThreadedClient client = new ChatThreadedClient();
        client.startClient();
    }
    
    public static void startChatClient() {
    	ChatThreadedClient client = new ChatThreadedClient();
        client.startClient();
    }

    public void startClient() {
        Socket socket = null;
        BufferedReader in = null;
        try {
            socket = new Socket(HOST, PORT);
            ConsoleThread console = new ConsoleThread(socket);
            console.start();

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String line = null;
            while ( (line = in.readLine()) != null ) {
            	if ( line.equals(EXIT) ) {
            		log.info("Closing programm");
            		break;
            	}
                System.out.println(">> " + line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Util.closeResource(in);
            Util.closeResource(socket);
        }
    }

    public void send(String message) {

    }

    class ConsoleThread extends Thread {
        BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter out;

        public ConsoleThread(Socket socket) throws Exception {
            out = new PrintWriter(socket.getOutputStream());
        }

        @Override
        public void run() {
            try {
            	while ( ! Thread.currentThread().isInterrupted() ) {
            		String line;
                    while ((line = console.readLine()) != null) {
                        out.println(line);
                        out.flush();
                        if (EXIT.equalsIgnoreCase(line)) {
                            log.info("Closing chat");
                            interrupt();
                        	sleep(100);
                        }
                    }
            	}
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            } catch (Exception e) {
            	e.printStackTrace();
            } finally {
                Util.closeResource(out);
            }
        }

    }

}
