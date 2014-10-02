/**
 * alexbalu-alpha7@mail.ru
 */
package com.abalyschev.mychat;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
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
	
	public static final String PATH_DOWNLOADS	= "files/downloads";
	
	public static final int PORT_MESSAGE 		= 19000;
	public static final int PORT_FILE_SENDING	= 19001;
	public static final int PORT_FILE_RECEIVING	= 19002;
	
	
    public static final String HOST = "localhost";
    
    // команды сервера
    private static final String COMMAND_EXIT		= ".exit";
    private static final String COMMAND_LOGIN 		= ".login";
    private static final String COMMAND_USERLIST 	= ".userlist";
    private static final String COMMAND_FILE 		= ".file";
    	
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
	
	// клиент чата
	private static ClientChat client;
	
	// логин пользователя
	private static final String LOGIN_UNKNOWN = "unknown";
	private static String userLogin = LOGIN_UNKNOWN;
		
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
	    
	    // логин пользователя
	    ChatGUI.setUserLogin(login);
	    
	    // запуск клиента
	    client = new ClientChat(this);
	 		
	    // выполним инициализацию
	 	client.doEntrance();
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
	
	protected static String getUserLogin() {
		if ( userLogin.equals("") ) {
			return LOGIN_UNKNOWN;
		}
		return userLogin;
	}
	
	protected static void setUserLogin(String login) {
		userLogin = login;
	}
	
	protected void addLineMessage(final String msg) {
		String text = lineMsgTA.getText();
		String line = "\n" + getNowStr() + " (" + getUserLogin() + "): " + msg + "\n";
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
				client.getClientMsg().sendMsg(msg);
				log.info("Action: " + action + " msg: " + msg);
			}
			if ( action.equals(ACTION_SEND_FILE) ) {
				// отправка файла
				client.getClientFile().sendFile();
				log.info("Action: " + action);
			}
		}
	}
	
	
	/**
	 * Класс чата клиента
	 */
	private static class ClientChat {		
		// сокеты и клиенты чатa
	    private ClientMessage clientMsg;
	    private ClientFile clientFile;
	    
	    private Socket socketMsg;
	    
	    // ссылка на интерфейс чата
	    private static ChatGUI chatGui;
	    
	    public ClientChat(final ChatGUI chat) {
	    	try {
	    		chatGui			= chat;
	    		
	    		this.socketMsg	= new Socket(HOST, PORT_MESSAGE);
	    		
	    		this.clientMsg	= new ClientMessage(socketMsg);
	    		this.clientFile	= new ClientFile();
	    		
	    		this.clientMsg.start();
	    		this.clientFile.start();
	    	} catch(Exception e) {
	    		e.printStackTrace();
	    	}
	    }
	    
	    public ClientMessage getClientMsg() {
	    	return clientMsg;
	    }
	    
	    public ClientFile getClientFile() {
	    	return clientFile;
	    }
	    
	    public void doEntrance() {
			// вход в систему 
	    	String msg = COMMAND_LOGIN + " " + userLogin;
			clientMsg.sendMsg(msg);
	 		
	 		// загрузка данных (контакты)
	 		clientMsg.sendMsg(COMMAND_USERLIST);
		}
	    
	    public static ChatGUI getChatGUI() {
	    	return chatGui;
	    }
	}
	
	/**
	 * Класс клиента сообщений
	 */
	private static class ClientMessage extends Thread {
		private PrintWriter out		= null;
		private BufferedReader in 	= null;
		private Socket skClient		= null;
		public ClientMessage(final Socket socket) throws Exception {
			skClient	= socket;
			out 		= new PrintWriter(socket.getOutputStream());
			in			= new BufferedReader(new InputStreamReader(socket.getInputStream()));
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
	            	ClientChat.getChatGUI().addLineMessage(line);
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
	}
	
	/**
	 * Класс файлового клиента
	 */
	private static class ClientFile extends Thread {
		public ClientFile() throws Exception {}
		
		public void run() {
			BufferedReader bReader		= null;
			Socket skFileReceive		= null;
			FileOutputStream oStream	= null;
			try {
				// подключаемся к сокету
				skFileReceive	= new Socket(HOST, PORT_FILE_RECEIVING);
				bReader			= new BufferedReader(new InputStreamReader(skFileReceive.getInputStream()));
				
				while (true) {
					// ожидаем прием файла
					String line = null;
					int cnt = 0;
					List<String> params = new ArrayList<>();
					while ( (line = bReader.readLine()) != null ) {
						log.info("Received file params: " + line);
						params.add(cnt++, line);
						if ( cnt == 3 ) {
							// данные по файлу получены - сохраняем файл
							File dir	= new File(PATH_DOWNLOADS + "/" + ChatGUI.getUserLogin());
		    				if ( ! dir.exists() ) {
		    					// нет директории - создадим
		    					dir.mkdir();
		    				}
							
		    				File file	= new File(PATH_DOWNLOADS + "/" + ChatGUI.getUserLogin() + "/" + params.get(1));
							if ( ! file.exists() ) {
								file.createNewFile();
							}

							oStream				= new FileOutputStream(file);
							InputStream iStream = skFileReceive.getInputStream();
							
							byte[] buffer 		= new byte[Long.valueOf(params.get(2)).intValue()];
							int bytesReceived 	= 0;
							while ( (bytesReceived = iStream.read(buffer)) > 0 ) {
								oStream.write(buffer, 0, bytesReceived);
								oStream.flush();
							}
							// закрываем ресурсы
							Util.closeResource(iStream);
							Util.closeResource(oStream);
						}
					}
				}
			} catch(IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				Util.closeResource(skFileReceive);
			}
		}
		
		/**
		 * отправка файла пользователям
		 */
		public void sendFile() {
			log.info("Send file to server.");
			FileInputStream fReader 	= null;
			OutputStreamWriter sWriter	= null;
			OutputStream oStream		= null;
			Socket skFile				= null;
			
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
				
				// открываем сокет
				skFile	= new Socket(HOST, PORT_FILE_SENDING);
				
				// запись вспомогательной инфы
				sWriter 	= new OutputStreamWriter(skFile.getOutputStream());
				
				// команда отправки файла
				// sWriter.write(COMMAND_FILE+"\n");
				// sWriter.flush();
				
				// имя отправителя
				sWriter.write(ChatGUI.getUserLogin() + "\n");
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
				
				oStream = skFile.getOutputStream();
				
				while ( (bytesRead = fReader.read(buffer)) > 0 ) {
					// пока не дошли до конца файла - отправляем байты
					log.info("Bytes received: " + bytesRead);
					oStream.write(buffer, 0, bytesRead);
					oStream.flush();
				}
				log.info("File to server has sent");
				
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				Util.closeResource(fReader);
				Util.closeResource(sWriter);
				Util.closeResource(oStream);
				Util.closeResource(skFile);
			}
		}
	}
}


