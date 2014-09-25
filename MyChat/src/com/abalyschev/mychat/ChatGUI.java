/**
 * alexbalu-alpha7@mail.ru
 */
package com.abalyschev.mychat;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChatGUI extends JFrame {
	
	private static Logger log = LoggerFactory.getLogger(ChatGUI.class);
	
	private static String ACTION_SEND_MESSAGE	= "send_message";
	private static String ACTION_SEND_FILE 		= "send_file";
	
	public static final int PORT = 19000;
    public static final String HOST = "localhost";
    
    // команды сервера
    private static final String COMMAND_EXIT		= ".exit";
    private static final String COMMAND_LOGIN 		= ".login";
    private static final String COMMAND_USERLIST 	= ".userlist";
    private static final String COMMAND_FILE 		= ".file";
    
    
    // сокет и клиент для соединения с сервером
    private static Socket socket;
    private static ChatClient client;
    
	// логин пользователя
	private String userLogin = "";
	
	// элементы управления
	private JPanel viewMsgPnl;
	private JTextArea lineMsgTA;
	
	private JPanel sendMsgPnl;
	private JTextArea editMsgTA;
	private JButton sendMsgBtn;
	
	private JPanel contactPnl;
	private JLabel contactLb;
	private JList contactLst;
	
	private JPanel actionPnl;
	private JLabel actionLb;
	private JButton fileBtn;
	
	// список сообщения
	private List<String> messages;
	
	// обработчик нажатия кнопки
	private static ActionListener actionListener;
		
	public static void startChat(final String login) {
	    javax.swing.SwingUtilities.invokeLater(new Runnable() {
	        public void run() {
	        	javax.swing.JFrame frame = new ChatGUI(login);
	    	    frame.setVisible(true);
	        }
	    });
	}
	
	public ChatGUI(final String login) {
		super("Simple chat");
		
		// логин пользователя
		userLogin = login;
		
		// инициализация окна
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    setSize(400, 400);
	    setLayout(new BorderLayout());
	    
	    messages = new ArrayList<String>();
	    
	    // инициализация элементов управления
	    initControls();
	    
	    // компоновка элементов окна
	    add(contactPnl, BorderLayout.WEST);
	    add(actionPnl, 	BorderLayout.EAST);
	    add(viewMsgPnl, BorderLayout.CENTER);
	    add(sendMsgPnl, BorderLayout.SOUTH); 
	    
	    // запуск клиента
	 	try {
	 		socket = new Socket(HOST, PORT);
	 		client = new ChatClient(socket);
	 		client.start();
	 		// выполним инициализацию
	 		client.doEntrance();
	 	} catch (Exception e) {
	 		e.printStackTrace();
	 	}
	}
		
	/**
	 * инициализация элементов управления
	 */
	private void initControls() {
		// обработчики событий
		actionListener = new ButtonListener();
		
		// панель просмотра сообщения
	    viewMsgPnl	= new JPanel();
	    viewMsgPnl.setLayout(new BoxLayout(viewMsgPnl, BoxLayout.Y_AXIS));
	    viewMsgPnl.setBorder(BorderFactory.createLineBorder(Color.black));
	    
	    lineMsgTA	= new JTextArea(10, 30);
	    lineMsgTA.setEditable(false);
	    viewMsgPnl.add(lineMsgTA);
	    
	    // панель отправки сообщения
	    sendMsgPnl 	= new JPanel();
	    sendMsgPnl.setLayout(new BoxLayout(sendMsgPnl, BoxLayout.X_AXIS));
	    sendMsgPnl.setBorder(BorderFactory.createLineBorder(Color.black));
	    
	    editMsgTA	= new JTextArea(10, 30);
	    sendMsgBtn	= new JButton("Enter");
	    sendMsgBtn.setActionCommand(ACTION_SEND_MESSAGE);
	    sendMsgBtn.addActionListener(actionListener);
	    
	    sendMsgPnl.add(editMsgTA);
	    sendMsgPnl.add(sendMsgBtn);
	    
	    // панель контактов
	    contactPnl = new JPanel();
	    contactPnl.setPreferredSize(new Dimension(100, 400));
	    contactPnl.setLayout(new BoxLayout(contactPnl, BoxLayout.Y_AXIS));

	    contactLb	= new JLabel("Contacts");
	    //contactLb.setHorizontalAlignment(JLabel.RIGHT);
	    contactLst	= new JList(new String[] { "Alex", "Nick", "Clave", "Sarah", "Helen" });
	    contactLst.setBorder(BorderFactory.createLineBorder(Color.gray));
	    contactLst.setAutoscrolls(true);
	    contactLst.setPrototypeCellValue("11111111111");
	    contactLst.setPreferredSize(new Dimension(100, 120));
	    
	    
	    contactPnl.add(contactLb);
	    contactPnl.add(contactLst);
	    
	    // панель действий
	    actionPnl	= new JPanel();
	    actionPnl.setPreferredSize(new Dimension(100, 400));
	    actionPnl.setLayout(new BoxLayout(actionPnl, BoxLayout.Y_AXIS));
	    
	    actionLb	= new JLabel("Actions");
	    fileBtn		= new JButton("Send File");
	    fileBtn.setActionCommand(ACTION_SEND_FILE);
	    fileBtn.addActionListener(actionListener);
	    
	    actionPnl.add(actionLb);
	    actionPnl.add(fileBtn);
	}
	
	protected void addLineMessage(final String msg) {
		String text = lineMsgTA.getText();
		String line = "\n" + getNowStr() + " (" + userLogin + "): " + msg + "\n";
		text += line;
		lineMsgTA.setText(text);
	}
	
	protected String getNowStr() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss");
		return dateFormat.format(new Date());
	}
	
	
	private class ButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			String action = event.getActionCommand();
			if ( action.equals(ACTION_SEND_MESSAGE) ) {
				// отправка сообщения
				String msg = editMsgTA.getText().trim();
				messages.add(msg);
				addLineMessage(msg);
				client.sendMsg(msg);
				log.info("Action: " + action + " msg: " + msg);
			}
			if ( action.equals(ACTION_SEND_FILE) ) {
				// отправка файла
				client.sendFile();
				log.info("Action: " + action);
			}
		}
	}
	
	/**
	 * Класс клиента
	 */
	private class ChatClient extends Thread {
		private PrintWriter out		= null;
		private BufferedReader in 	= null;
		private Socket skClient		= null;
		public ChatClient(final Socket socket) {
			try {
				skClient	= socket;
				out 		= new PrintWriter(socket.getOutputStream());
				in			= new BufferedReader(new InputStreamReader(socket.getInputStream()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		public void run() {
			try {
				String line = null;
	            while ( (line = in.readLine()) != null ) {
	            	if ( line.equals(COMMAND_EXIT) ) {
	            		log.info("Closing programm");
	            		break;
	            	}
	            	// выводим пришедшее сообщение
	            	addLineMessage(line);
	                System.out.println(">> " + line);
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        } finally {
	            Util.closeResource(in);
	            Util.closeResource(out);
	            Util.closeResource(skClient);
	        }
		}
		
		/**
		 * отправка сообщения пользователям
		 */
		public void sendMsg(final String msg) {
			log.info("Send message to server.");
			out.println(msg);
			out.flush();
		}
		
		/**
		 * отправка файла пользователям
		 */
		public void sendFile() {
			log.info("Send file to server.");
			FileInputStream fReader 	= null;
			OutputStreamWriter sWriter	= null;
			OutputStream oStream		= null;
			try {
				// @TODO: рассмотреть эту идею
//				BufferedOutputStream out = new BufferedOutputStream(skClient.getOutputStream());
//				try ( DataOutputStream d = new DataOutputStream(out) ) {
//					d.writeUTF(fileName);
//					d.writeUTF(String.valueOf(file.length()));
//					Files.copy(file.toPath(), d);
//					d.close();
//				}
				
				// открываем файл
				String fileName	= "files/data.xml";
				File file		= new File(fileName);
				if ( ! file.exists() ) {
					log.info("No file to send: " + fileName);
				}
				
				fReader 	= new FileInputStream(file);
				
				// запись файла
				sWriter 	= new OutputStreamWriter(skClient.getOutputStream());
				
				// команда отправки файла
				sWriter.write(COMMAND_FILE+"\n");
				sWriter.flush();
				
				// имя файла
				sWriter.write(file.getName()+"\n");
				sWriter.flush();
				
				// размер файла
				sWriter.write(Long.toString(file.length())+"\n");
				sWriter.flush();
				
				// подождем, пока дойдут данные
				Thread.sleep(400);
				
				// отправка файла
				byte[] buffer = new byte[Long.valueOf(file.length()).intValue()];
				int bytesRead = 0;
				
				oStream = skClient.getOutputStream();
				
				while ( (bytesRead = fReader.read(buffer)) > 0 ) {
					// пока не дошли до конца файла - отправляем байты
					log.info("Bytes received: " + bytesRead);
					oStream.write(buffer, 0, bytesRead);
				}
				oStream.flush();
				log.info("Send file to server has sent");
				
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				Util.closeResource(fReader);
				//Util.closeResource(sWriter);
				//Util.closeResource(oStream);
			}
		}
		
		/**
		 * выполнить вход в систему
		 */
		public void doLogin(final String userLogin) {
			String msg = COMMAND_LOGIN + " " + userLogin;
			out.println(msg);
			out.flush();
		}
		
		public void getContacts() {
			out.println(COMMAND_USERLIST);
			out.flush();
		}
		
		public void doEntrance() {
			// вход 
	 		doLogin(userLogin);
	 		// загрузка данных (контакты)
	 		getContacts();
		}
	}
}


