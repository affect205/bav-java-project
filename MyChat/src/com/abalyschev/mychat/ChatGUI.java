/**
 * alexbalu-alpha7@mail.ru
 */
package com.abalyschev.mychat;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
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
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.border.TitledBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChatGUI extends JFrame {
	
	private static Logger log = LoggerFactory.getLogger(ChatGUI.class);
	
	private static String ACTION_SEND_MESSAGE	= "sm";
	private static String ACTION_SEND_FILE 		= "sf";
	private static String ACTION_OPEN_FILE 		= "of";
	private static String ACTION_CLEAR_MESSAGE 	= "cm";
	private static String ACTION_SHARE_PAINTER 	= "sp";
	private static String ACTION_GET_PAINTER 	= "gp";
	
	
	public static final String PATH_DOWNLOADS	= "files/downloads";
	
	public static final int PORT_MESSAGE 		= 19000;
	public static final int PORT_FILE_SENDING	= 19001;
	public static final int PORT_FILE_RECEIVING	= 19002;
	public static final int PORT_DESK_SHARING	= 19003;
	public static final int PORT_DESK_VIEWING	= 19004;
	
	
    public static final String HOST = "localhost";
    
    // команды клиента - сервера
    private static final String COMMAND_EXIT		= ".exit";
    private static final String COMMAND_LOGIN 		= ".login";
    private static final String COMMAND_USERLIST 	= ".userlist";
    private static final String COMMAND_FILE 		= ".file";
    private static final String COMMAND_DESK_ON		= ".deskon";
    private static final String COMMAND_DESK_OFF	= ".deskoff";
    	
	// элементы управления
	private JPanel viewMsgPnl;
	private JTextArea lineMsgTA;
	
	private JPanel sendMsgPnl;
	private JTextArea editMsgTA;
	private JButton sendMsgBtn;
	private JButton clearMsgBtn;
	
	private JPanel contactPnl;
	private JList contactLst;
	private static final String[] DEFAULT_CONTACTS 		= { "Friends:", "", "", "", "", "", "", "", "", "" }; 
	private static final String MESSAGE_EMPTY_CONTACTS	= "system:  no users in chat";
	private List<String> contacts;
	
	private JPanel actionPnl;
	private JButton fileOpenBtn;
	private JButton fileSendBtn;
	private JToggleButton painterShareTgl;
	private JButton painterGetBtn;
	private JComboBox painterSelectCb;
	
	private JPanel infoPnl;
	private JLabel loginLb;
	
	// выбор файла
	private JFileChooser fChooser;
	// прикрепленный файл
	private static File attachedFile;
	
	// список сообщений
	private List<String> messages;
	
	// обработчик нажатия кнопки
	private static ActionListener actionListener;
	
	// обработчик нажатия лавиш
	private static KeyListener keyListener;
	
	// клиент чата
	private static ClientChat client;
	
	// дочернее окно: доска для рисования
	private static JFrame paintDeskFrm;
	
	// логин пользователя
	private static final String LOGIN_UNKNOWN = "unknown";
	private static String userLogin = LOGIN_UNKNOWN;
		
	public static void startChat(final String login) {
	    javax.swing.SwingUtilities.invokeLater(new Runnable() {
	        public void run() {
	        	// основное окно
	        	javax.swing.JFrame frame = new ChatGUI(login);
	    	    frame.setVisible(true);
	    	    
	    	    // доска для рисования
	    	    try {
	    	    	// сокет обмена данными между досками
	    	    	Socket paintDeskSk = new Socket(HOST, PORT_DESK_SHARING);
		    	    paintDeskFrm = new PaintFrame(userLogin, PaintFrame.Mode.SHARING, paintDeskSk);
	            	paintDeskFrm.setVisible(false);
	    	    } catch(IOException e) {
	    	    	e.getStackTrace();
	    	    }
	        }
	    });
	}
	
	public ChatGUI(final String login) {
		super("Simple chat");
		
		// инициализация окна
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    setSize(700, 400);
	    BorderLayout appLt = new BorderLayout();
	    appLt.setHgap(10);
	    appLt.setVgap(10);
	    setLayout(appLt);
	    
	    // логин пользователя
	    ChatGUI.setUserLogin(login);
	    
	    // список сообщений
	    messages = new ArrayList<String>();
	    
	    // список контактов
	    contacts = new ArrayList<String>();
	    contacts.addAll(Arrays.asList(DEFAULT_CONTACTS));
	    
	    // инициализация данных приложения
	    attachedFile = new File("");
	    
	    // инициализация элементов управления
	    initControls();
	    
	    // компоновка элементов окна
	    add(contactPnl, BorderLayout.WEST);
	    add(actionPnl, 	BorderLayout.EAST);
	    add(viewMsgPnl, BorderLayout.CENTER);
	    add(sendMsgPnl, BorderLayout.SOUTH); 
	    add(infoPnl,	BorderLayout.NORTH);
	    
	    // запуск клиента
	    client = new ClientChat(this);
	 		
	    // выполним инициализацию клиента
	 	client.doEntrance();
	}
		
	/**
	 * инициализация элементов управления
	 */
	private void initControls() {
		// обработчики событий
		actionListener 	= new ButtonListener();
		keyListener		= new TAListener();
		
		// панель просмотра сообщения
	    viewMsgPnl	= new JPanel();
	    viewMsgPnl.setLayout(new BoxLayout(viewMsgPnl, BoxLayout.Y_AXIS));
	    viewMsgPnl.setBorder(BorderFactory.createLineBorder(Color.black));
	    
	    lineMsgTA	= new JTextArea(10, 30);
	    lineMsgTA.setEditable(false);	
	    lineMsgTA.setLineWrap(true);
	    viewMsgPnl.add(lineMsgTA);
	    
	    // панель отправки сообщения
	    sendMsgPnl 	= new JPanel();
	    sendMsgPnl.setLayout(new BoxLayout(sendMsgPnl, BoxLayout.X_AXIS));
	    sendMsgPnl.setBorder(BorderFactory.createTitledBorder("Messaging"));
	    
	    editMsgTA	= new JTextArea(10, 30);
	    editMsgTA.addKeyListener(keyListener);
	    editMsgTA.setLineWrap(true);
	    sendMsgBtn	= new JButton("Enter");
	    sendMsgBtn.setActionCommand(ACTION_SEND_MESSAGE);
	    sendMsgBtn.addActionListener(actionListener);
	    clearMsgBtn	= new JButton("Clear");
	    clearMsgBtn.setActionCommand(ACTION_CLEAR_MESSAGE);
	    clearMsgBtn.addActionListener(actionListener);
	    
	    JPanel sendCmdPnl = new JPanel();
	    sendCmdPnl.setLayout(new BoxLayout(sendCmdPnl, BoxLayout.Y_AXIS));
	    sendCmdPnl.add(sendMsgBtn);
	    sendCmdPnl.add(Box.createVerticalStrut(10));
	    sendCmdPnl.add(clearMsgBtn);
	    
	    sendMsgPnl.add(editMsgTA);
	    sendMsgPnl.add(sendCmdPnl);
	    
	    // панель контактов
	    contactPnl = new JPanel();
	    contactPnl.setPreferredSize(new Dimension(140, 400));
	    TitledBorder title = BorderFactory.createTitledBorder("Contatcs");
	    title.setTitlePosition(TitledBorder.DEFAULT_POSITION);
	    contactPnl.setBorder(title);
	    contactPnl.setLayout(new BoxLayout(contactPnl, BoxLayout.Y_AXIS));

	    contactLst	= new JList(contacts.toArray());	    		
	    contactLst.setBorder(BorderFactory.createLineBorder(Color.gray));
	    contactLst.setAutoscrolls(true);
	    contactLst.setPrototypeCellValue(".................................");
	    contactLst.setSize(120, 120);
	    
	    contactPnl.add(contactLst);
	    
	    // панель действий
	    actionPnl	= new JPanel();
	    actionPnl.setPreferredSize(new Dimension(140, 400));
	    actionPnl.setLayout(new BoxLayout(actionPnl, BoxLayout.Y_AXIS));
	    actionPnl.setBorder(BorderFactory.createTitledBorder("Actions"));

	    // пересылка файла
	    fileSendBtn	= new JButton(new ImageIcon("files/img/enter-16.png"));
	    fileSendBtn.setActionCommand(ACTION_SEND_FILE);
	    fileSendBtn.addActionListener(actionListener);
	    fileSendBtn.setToolTipText("Send File");
	    
	    fileOpenBtn	= new JButton("...");
	    fileOpenBtn.setActionCommand(ACTION_OPEN_FILE);
	    fileOpenBtn.addActionListener(actionListener);
	    
	    JPanel fileSendPnl = new JPanel();
	    fileSendPnl.setLayout(new BoxLayout(fileSendPnl, BoxLayout.X_AXIS));
	    fileSendPnl.add(fileOpenBtn);
	    fileSendPnl.add(Box.createHorizontalStrut(10));
	    fileSendPnl.add(fileSendBtn);
	    
	    // доска для рисования
	    JPanel painterPnl = new JPanel();
	    painterPnl.setLayout(new BoxLayout(painterPnl, BoxLayout.Y_AXIS));
	    
	    painterShareTgl	= new JToggleButton("Share");
	    painterShareTgl.setToolTipText("Show paint desk");
	    painterShareTgl.setActionCommand(ACTION_SHARE_PAINTER);
	    painterShareTgl.addActionListener(actionListener);
	    
	    painterGetBtn 	= new JButton("Get");
	    painterGetBtn.setActionCommand(ACTION_GET_PAINTER);
	    painterGetBtn.addActionListener(actionListener);
		painterSelectCb = new JComboBox();
		painterSelectCb.setMaximumSize(new Dimension(120, 60));
	    
	    JPanel painterSubPnl = new JPanel();
	    painterSubPnl.setLayout(new BoxLayout(painterSubPnl, BoxLayout.X_AXIS));
	    painterSubPnl.add(painterShareTgl);
	    painterSubPnl.add(painterGetBtn);
	    
	    painterPnl.add(painterSelectCb);
	    painterPnl.add(painterSubPnl);
	    
	    // диалог выбора файла
	    fChooser		= new JFileChooser();
	    
	    actionPnl.add(fileSendPnl);
	    actionPnl.add(painterPnl);
	    
	    
	    
	    // панель состояния
	    infoPnl		= new JPanel();
	    infoPnl.setLayout(new BoxLayout(infoPnl, BoxLayout.X_AXIS));
	    infoPnl.setPreferredSize(new Dimension(400, 60));
	    infoPnl.setBorder(BorderFactory.createTitledBorder("User"));
	    
	    loginLb		= new JLabel(getLogin(), new ImageIcon("files/img/user-32.png"), JLabel.LEFT);
	    loginLb.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 10));
	    
	    infoPnl.add(loginLb);
	    
	}
	
	protected static String getLogin() {
		if ( userLogin.equals("") ) {
			return LOGIN_UNKNOWN;
		}
		return userLogin;
	}
	
	protected static void setUserLogin(String login) {
		userLogin = login;
	}
	
	/**
	 * Установка прикрепленного файла
	 */
	protected void setAttachedFile(final File file) {
		if ( file == null || ! file.exists() ) {
			return;
		}
		attachedFile = file;
		fileOpenBtn.setToolTipText(file.getAbsolutePath());
	}
	
	/**
	 * Получение прикрепленного файла
	 */
	protected static File getAttachedFile() {
		return attachedFile;
	}
	
	/**
	 * Добавление сообщение в поле сообщения
	 * @param msg
	 */
	protected void addLineMessage(final String msg) {
		String text = lineMsgTA.getText();
		String line = msg + "\n";
		text += getNowStr() + "  -  " + line;
		lineMsgTA.setText(text);
	}
	
	protected String getNamingMessage(final String msg) {
		return ChatGUI.getLogin() + ":  " + msg;
	}
	
	/**
	 * Получение строки с текущим временем
	 */
	protected String getNowStr() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss");
		return dateFormat.format(new Date());
	}
	
	/**
	 * Проверка на наличие доступных пользователей в списке контактов
	 */
	protected boolean isEmptyContacts() {
		for ( String str : contacts ) {
			if ( ! str.equals("") ) {
				return false; 
			}
		}
		return true;
	}
	
	/**
	 * Добавление контакта/контактов
	 */
	protected void addContact(String contact) {
		log.info("Contact list: " + contact);
		String[] params = contact.split(COMMAND_USERLIST + ":");
		if ( params.length < 2 ) {
			return;
		}
		String[] values	= params[1].split(";");
		for ( int i=0; i < values.length; ++i ) {
			int ndx = -1;
			if ( (ndx = contacts.indexOf("")) != -1) {
				contacts.add(ndx, values[i]);
			} else {
				contacts.add(values[i]);
			}
		}
		// обновляем список контактов
		contactLst.setListData(contacts.toArray());
	}
	
	/**
	 * Добавление доски для рисования в список выбора
	 */
	protected void addPaintDesk(final String msg) {
		String[] params = msg.split(":");
		if ( params[0] == null || params[0].trim().equals("") ) {
			return;
		}
		// добавляем элемент в список
		painterSelectCb.addItem(params[0]);
	}
	
	/**
	 * Удаление доски для рисования из списка выбора
	 */
	protected void deletePaintDesk(final String msg) {
		String[] params = msg.split(":");
		if ( params[0] == null || params[0].trim().equals("") ) {
			return;
		}
		// удаляем элемент из списка
		painterSelectCb.removeItem(params[0]);
	}
	
	// ----------------------------------INNER CLASS----------------------------------
	
	private class TAListener implements KeyListener {
		@Override
		public void keyPressed(KeyEvent e) {
			switch(e.getKeyCode()) {
				case KeyEvent.VK_ENTER:
					e.consume();
					sendMsgBtn.doClick();
			}
		}
		@Override
		public void keyTyped(KeyEvent e) {
			
		}
		@Override
		public void keyReleased(KeyEvent e) {
			
		}
	}
	
	
	// ----------------------------------INNER CLASS----------------------------------
	
	private class ButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			String action = event.getActionCommand();
			if ( action.equals(ACTION_SEND_MESSAGE) ) {
				// отправка сообщения
				String msg = editMsgTA.getText().trim();
				messages.add(msg);
				addLineMessage(getNamingMessage(msg));
				log.info("Message - " + msg);
				if ( isEmptyContacts() ) {
					// чат пуст - предупредим
					addLineMessage(MESSAGE_EMPTY_CONTACTS);
				} else {
					// иначе - отправляем сообщение на сервер
					client.getClientMsg().sendMsg(msg);
				}
			}
			
			if ( action.endsWith(ACTION_CLEAR_MESSAGE) ) {
				// очистка поля сообщения
				ChatGUI.this.editMsgTA.setText("");
			}
			
			if ( action.equals(ACTION_SEND_FILE) ) {
				// отправка файла
				client.getClientFile().sendFile();
			}
			
			if ( action.equals(ACTION_OPEN_FILE) ) {
				// открытие файла
				int value = fChooser.showOpenDialog(ChatGUI.this);
				
				switch(value) {
					case JFileChooser.APPROVE_OPTION:
						// получение прикрепленного файла
						File file = fChooser.getSelectedFile();
						ChatGUI.this.setAttachedFile(file);
				}
			}
			
			if ( action.equals(ACTION_SHARE_PAINTER) ) {
				// открываем доску для рисования
				if ( ChatGUI.this.painterShareTgl.isSelected() ) {
					// показываем доску - оповещаем сервер об открытии доски
					log.info("Share paint desk");
					ChatGUI.this.client.getClientMsg().sendMsg(COMMAND_DESK_ON);
					paintDeskFrm.setVisible(true);
				} else {
					// скрываем доску - оповещаем сервер о закрытии доски
					log.info("Hide paint desk");
					ChatGUI.this.client.getClientMsg().sendMsg(COMMAND_DESK_OFF);
					paintDeskFrm.setVisible(false);
					
				}	
			}
			
			if ( action.equals(ACTION_GET_PAINTER) ) {
				// подключаемся к расшаренной доске
				log.info("Get paint desk");
				
				try {
					log.info("Send desk viewing query");
					Socket sock 		= new Socket(HOST, PORT_DESK_VIEWING);
					PrintWriter out		= new PrintWriter(sock.getOutputStream());
					out.write(ChatGUI.getLogin() + ":" + "viewing");
					out.flush();
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				/*
				new Thread(new Runnable() {
					public void run() {
						try {
							log.info("Send desk viewing query");
							Socket sock 		= new Socket(HOST, PORT_DESK_VIEWING);
							BufferedReader in	= new BufferedReader(new InputStreamReader(sock.getInputStream()));
							PrintWriter out		= new PrintWriter(sock.getOutputStream());
							out.write(ChatGUI.getLogin() + ":" + "viewing");
							out.flush();
							String line = null;
							while ( (line = in.readLine()) != null ) {
								log.info("Input desk data: " + line);
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}).start();
				*/
			} 
		}
	}
	
	// ----------------------------------INNER CLASS----------------------------------
	
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
	
	// ----------------------------------INNER CLASS----------------------------------
	
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
	            	if ( line.contains(COMMAND_USERLIST) ) {
	            		log.info("New contact in chat");
	            		// обновляем контакты
	            		ClientChat.getChatGUI().addContact(line);
	            	}
	            	if ( line.contains(COMMAND_DESK_ON) ) {
	            		// доступна новая доска для рисования
	            		ClientChat.getChatGUI().addPaintDesk(line);
	            	}
	            	if ( line.contains(COMMAND_DESK_OFF) ) {
	            		// убираем доску из списка
	            		ClientChat.getChatGUI().deletePaintDesk(line);
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
			log.info(msg);
			out.println(msg);
			out.flush();
		}
	}
	
	// ----------------------------------INNER CLASS----------------------------------
	
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
							File dir	= new File(PATH_DOWNLOADS + "/" + ChatGUI.getLogin());
		    				if ( ! dir.exists() ) {
		    					// нет директории - создадим
		    					dir.mkdir();
		    				}
							
		    				File file	= new File(PATH_DOWNLOADS + "/" + ChatGUI.getLogin() + "/" + params.get(1));
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
				Util.closeResource(bReader);
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
				
				// получаем файл
				File file		= ChatGUI.getAttachedFile();
				String fileName = file.getName();
				if ( ! file.exists() ) {
					log.info("No file to send: " + fileName);
					return;
				}
				// ридер файла
				fReader = new FileInputStream(file);
				
				// открываем сокет
				skFile	= new Socket(HOST, PORT_FILE_SENDING);
				
				// запись вспомогательной инфы
				sWriter 	= new OutputStreamWriter(skFile.getOutputStream());
				
				// команда отправки файла
				// sWriter.write(COMMAND_FILE+"\n");
				// sWriter.flush();
				
				// имя отправителя
				sWriter.write(ChatGUI.getLogin() + "\n");
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


