/**
 * @author alexbalu-alpha7@mail.ru
 */
package com.abalyschev.mychat;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChatThreadedServer {

    protected static Logger log = LoggerFactory.getLogger("ThreadedServer");
    private static final int PORT_MESSAGE			= 19000;
    private static final int PORT_FILE_SENDING		= 19001;
    private static final int PORT_FILE_RECEIVING	= 19002;
    private static final int PORT_DESK_SHARING		= 19003;
    private static final int PORT_DESK_VIEWING		= 19004;
    
    private static int counter = 0;
    
    // путь закачиваемых на сервер файлов
    private static final String PATH_UPLOAD = "files/upload";
    
    // команды, доступные на сервере
    private static final String COMMAND_HELP		= ".help";
    private static final String COMMAND_LOGIN 		= ".login";
    private static final String COMMAND_PRIVATE 	= ".private";
    private static final String COMMAND_PUBLIC		= ".public";
    private static final String COMMAND_SEND_FILE	= ".file";
    private static final String COMMAND_USERLIST	= ".userlist";
    private static final String COMMAND_EXIT 		= ".exit";
    private static final String COMMAND_DESK_ON 	= ".deskon";
    private static final String COMMAND_DESK_OFF 	= ".deskoff";
    
    private static final String MESSAGE_NOT_LOGINED 	= "You are not logined!";    
    private static final int USER_LIMIT = 10;
    
    private static Set<String> commands = new HashSet<String>(Arrays.asList(
    		COMMAND_HELP,
    		COMMAND_LOGIN,
    		COMMAND_PRIVATE,
    		COMMAND_PUBLIC,
    		COMMAND_SEND_FILE,
    		COMMAND_USERLIST,
    		COMMAND_EXIT));

    // список обработчиков для клиентов
    private List<ClientHandler> handlers = new ArrayList<>();
    
    // фиксированный пул соединений
    // @TODO private ExecutorService service = Executors.newFixedThreadPool(USER_LIMIT);

    public static void main(String[] args) throws Exception {
        ChatThreadedServer server = new ChatThreadedServer();
        server.startServer();
    }

    public void startServer() throws Exception {
        log.info("Starting server...");
        ServerSocket srvMsgSocket			= new ServerSocket(PORT_MESSAGE);
        ServerSocket srvFileReceivingSocket	= new ServerSocket(PORT_FILE_RECEIVING);
        ServerSocket srvDeskSocket			= new ServerSocket(PORT_DESK_SHARING);
        
        // обработчик файлов
        FileHandler srvFile					= new FileHandler(this);
        // обработчик запросов на просмотр доски
        DeskViewingHandler srvDeskViewng 	= new DeskViewingHandler(this);
        
        while (true) {

            // блокируемся и ждем клиента
            Socket skMsgClient = srvMsgSocket.accept();
            log.info("Client connected: " + skMsgClient.getInetAddress().toString() + ":" + skMsgClient.getPort());
            
            Socket skFileClient = srvFileReceivingSocket.accept();
            log.info("File client connected: " + skFileClient.getInetAddress().toString() + ":" + skFileClient.getPort());
        
            Socket skDeskPainter = srvDeskSocket.accept();
            log.info("Desk sharing client connected: " + skDeskPainter.getInetAddress().toString() + ":" + skDeskPainter.getPort());
            
            // создаем обработчик доски для рисования
            new DeskSharingHandler(this, skDeskPainter);
            
            // создаем обработчик сообщений
            ClientHandler handler = new ClientHandler(this, skMsgClient, skFileClient, counter++);
            
            // @TODO service.submit(handler);
            handlers.add(handler);
        }
    }
    
    /**
     * обработчик клиентских команд 
     */
    protected String handleServerCommand(final ClientHandler handler, final String command) {
    	String msg = "";
    	// парсинг параметров командной строки
    	String[] params = command.split("\\s+");
    	
    	if ( ! commands.contains(params[0]) ) {
    		// нет команды - вернем пустую строку
    		return msg;
    	}
    	
    	// базовые команд - вход не нужен
    	if ( COMMAND_HELP.equals(params[0]) ) {
    		msg = getHelpList();
    		
    	} else if ( COMMAND_LOGIN.equals(params[0]) ) {
    		String username = ( params.length > 1 ) ? params[1] : "";
    		msg = this.dologin(handler, username);
    		
    	} else if ( COMMAND_EXIT.equals(params[0]) ) {
    		msg = doExit(handler);
    	}
    	
    	// основные команды - здесь вход нужен
    	if ( msg.equals("") ) {
    		if ( ! handler.isLogined() ) {
        		return MESSAGE_NOT_LOGINED;
        	}
    	}
    	
    	if ( COMMAND_PRIVATE.equals(params[0]) ) {
    		List<String> users = new ArrayList<>();
    		for ( int i=1; i < params.length; ++i ) {
    			users.add(params[i]);
    		}
    		msg = setPrivateMode(handler, users);
    		
    	} else if ( COMMAND_PUBLIC.equals(params[0]) ) {
    		msg = setPublicMode(handler);
    		
    	} else if ( COMMAND_SEND_FILE.equals(params[0]) ) {
    		msg = doFileTransfer(handler);
    		
    	} else if ( COMMAND_USERLIST.equals(params[0]) ) {
    		msg = getLoginedUsers(handler);
    	
    	} else if ( COMMAND_DESK_ON.equals(params[0]) ) {
    		msg = "paint desk on"; 
    	
    	} else if ( COMMAND_DESK_OFF.equals(params[0]) ) {
    		msg = "paint desk off";
    	}
    	return msg;
    }
    
    /**
     * список пользовательских команд
     */
    public String getHelpList() {
    	log.info("Do help list");
    	StringBuilder str = new StringBuilder();
    	str.append("\n.exit\t- leave chat\n");
    	str.append(".help\t- command list\n");
    	str.append(".login <username>\t- login in chat\n");
    	str.append(".private <username1[ username2]>\t- private messaging mode\n");
    	str.append(".public\t- public messaging mode");
    	str.append(".userlist\t- logined users list");
    	return str.toString();
    }
    
    /**
     * список доступных пользователей
     */
    public String getLoginedUsers(final ClientHandler handler) {
    	StringBuilder users = new StringBuilder();
    	users.append(COMMAND_USERLIST);
    	users.append(":");
    	for ( ClientHandler client : handlers ) {
    		if ( ! client.getClientName().equals("") && ! client.getClientName().equals(handler.getClientName()) ) {
    			users.append(client.getClientName());
        		users.append(";");
    		}
    	}
    	int ndx = -1;
    	if ( (ndx = users.lastIndexOf(";")) != -1 ) {
    		users.deleteCharAt(ndx);
    	}
    	return users.toString();
    }
    
    /**
     * Получение обработчика по имени
     */
    public ClientHandler getClientHandlerByName(final String name) {
    	log.info("Get client handler by name: " + name);
    	if ( name.equals("") ) {
    		return null;
    	}
    	for ( ClientHandler client : handlers ) {
    		log.info("Client: " + client.getClientName());
    		if ( client.getClientName().equals(name) ) {
    			return client;
    		}
    	}
    	return null;
    }
    
    /**
     * выход пользователя из системы!
     */
    protected String doExit(final ClientHandler handler) {
    	log.info("Do exit");
    	// сигнал о выходе
    	handler.send(COMMAND_EXIT);
    	// разрыв соединения
    	handler.disconnect();
    	// удаление клиента из списка
    	handlers.remove(handler);
    	return COMMAND_EXIT;
    }
    
    /**
     * отправление личного сообщения
     */
    protected String setPrivateMode(final ClientHandler handler, final List<String> users) {
    	log.info(handler.getClientName() + " - set private mode");
    	List<String> group = new ArrayList<>();
    	for ( String user : users ) {
    		if ( clientExists(user) != null ) {
    			group.add(user);
    		}
    	}
    	if ( group.size() <= 0 ) {
    		return "no users for private messaging";
    	}
    	// установка режима и группы пользователей
    	handler.setPrivateMode(true);
    	handler.setPrivateGroup(group);
    	return "private mode on"; 
    }
    
    protected String setPublicMode(final ClientHandler handler) {
    	handler.setPrivateMode(false);
    	return "public mode on";
    }
    
    /**
     * вход в систему
     */
    protected String dologin(final ClientHandler handler, final String username) {
    	log.info("Do login");
    	if ( handler.isLogined() ) {
    		return "you are already logined";
    	}
    	if ( username.trim().isEmpty() ) {
    		return "bad name! try another one";
    	}
    	for ( ClientHandler client : handlers ) {
    		if ( client.getClientName().equals(username) ) {
    			return "client with such name already exists";
    		}
    	}
    	// проверка пройдена - залогим
    	handler.setClientName(username);
    	handler.setLogined(true);
    	
    	// посылаем участникам данные о новом контакте
    	for ( ClientHandler client : handlers ) {
    		if ( ! client.getClientName().equals(username) ) {
    			client.send(COMMAND_USERLIST + ":" + username);
    		}
    	}
    	return "you are logined";
    } 
    
    /**
     * рассылка передаваемого файла
     * @param handler
     * @param bReader
     * @return
     */
    protected String doFileTransfer(final ClientHandler handler) {
    	log.info("Do file transfer");
//    	try {	
//    		// @TODO: рассмотреть эту идею
//    		BufferedInputStream in = new BufferedInputStream(handler.getInputStream());
//    		try ( DataInputStream d = new DataInputStream(in) ) {
//    			String fileName = d.readUTF();
//    			String fileSize	= d.readUTF();
//    			String path = PATH_UPLOAD + "/" + handler.getClientName() + "/" + fileName;
//    			Files.copy(d, Paths.get(path));
//    			d.close();
//    		}
    	return "file has sent";
    }

    /**
     * Отправка файла
     */
    public void sendFile(final String senderName, File file) {
    	log.info("Send file to getters");
		FileInputStream fReader 		= null;
		BufferedInputStream bfReader	= null;
		OutputStreamWriter sWriter		= null;
		OutputStream oStream			= null;
		try {			
			// открываем файл
			if ( ! file.exists() ) {
				log.info("No file to send: " + file.getName());
				return;
			}
			
			// получение отправителя
			ClientHandler sender = getClientHandlerByName(senderName);
			if ( sender == null ) {
				// проблемы с отправителем - выходим
				log.info("No sender to send file");
				return;
			}
			
			// отправитель найден - определим список получателей
			List<String> getters = new ArrayList<String>();
			if ( sender.isPrivateMode() ) {
				// файл для приватной группы
				getters = sender.getPrivateGroup();
			} else {
				// для всех, кроме отправителя
				getters = getBroadcastList(senderName); 
			}
			
			if ( getters.isEmpty() ) {
				// отправлять некому - выходим
				log.info("No file getters");
				return;
			}
			
			// параметры файла
			String fileSender 	= senderName;
			String fileName		= file.getName();
			long fileSize		= file.length();
			fReader				= new FileInputStream(file);
			bfReader			= new BufferedInputStream(fReader);
			
			// отправка файла
			for ( String getter : getters ) {
				// проходимся по списку получателей
				ClientHandler handler = getClientHandlerByName(senderName);
				if ( handler == null ) {
					continue;
				}
				// отправка данных по файлу
				sWriter	= new OutputStreamWriter(handler.getFileOutputStream());
				if ( sWriter == null ) {
					log.info("Handler outputstream is null");
				} else {
					log.info("Handler outputstream is active");
				}
				sWriter.write(fileSender + "\n");
				sWriter.flush();
				sWriter.write(fileName + "\n");
				sWriter.flush();
				sWriter.write(String.valueOf(fileSize) + "\n");
				sWriter.flush();
				
				// отправка самого файла
				Thread.sleep(200);
				byte[] buffer 		= new byte[Long.valueOf(fileSize).intValue()];
				int bytesRead	 	= 0;
				oStream				= handler.getFileOutputStream();
				
				while ( (bytesRead = bfReader.read(buffer)) > 0 ) {
					// пишем данные в сокет
					log.info("Bytes Read: " + bytesRead);
					oStream.write(buffer, 0, bytesRead);
					oStream.flush();
				}
				log.info("File to user " + getter + " has been sent");
				
				// вернемся к началу файла
				bfReader.mark(0);
				//Util.closeResource(oStream);
				//Util.closeResource(sWriter);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			Util.closeResource(fReader);
			log.info("-----------------------------------------");
			//Util.closeResource(sWriter);
			//Util.closeResource(oStream);
		}	
    }
    
    /**
     * Список пользователей для открытой переписки
     */
    public List<String> getBroadcastList(String senderName) {
    	log.info("Get broadcast list");
    	List<String> getters = new ArrayList<String>();
    	for ( ClientHandler getter : handlers ) {
    		if ( getter.isLogined() && !senderName.equals(getter.getClientName()) ) {
    			// клиент залогирован и он не отправитель - добавляем в список
    			getters.add(getter.getClientName());
    		}
    	}
    	return getters;
    }
    
    // ---------------------------------------------INNER CLASS---------------------------------------
    
    /**
     * Для каждого присоединившегося пользователя создается поток обработки независимый от остальных
     */
    private static class ClientHandler /*extends Thread*/ implements Runnable {

        private final ChatThreadedServer server;
        private BufferedReader in;
        private PrintWriter out;
        private Socket skMsgClient;
        private Socket skFileClient;

        // объект потока
        private Thread thread;
        
        // номер, чтобы различать потоки
        private int number;
        
        // имя пользователя
        private String name = "";
        
        // флаг входа
        private boolean logined = false;

        // флаг исполнения
        private boolean connection = true;
        
        // режим личных сообщений
        private boolean privateMode = false;
        
        // адрес клиента
        private InetAddress addr;
        
        // список получателей личных сообщений
        private List<String> privateGroup = new ArrayList<String>();
        
        /**
         * Конструктор обработчика
         * @param server	- сслыка на сервер
         * @param skMsg		- сокет текстовых сообщений
         * @param skFile	- сокет файловых сообщений
         * @param counter	- идентификатор клиента
         * @throws Exception
         */
        public ClientHandler(ChatThreadedServer server, Socket skMsgClient, Socket skFileClient, int counter) throws Exception {
        	this.thread			= new Thread(this);
        	this.server			= server;
            this.in 			= new BufferedReader(new InputStreamReader(skMsgClient.getInputStream()));
            this.out 			= new PrintWriter(skMsgClient.getOutputStream());
            this.number 		= counter;
            this.skMsgClient	= skMsgClient;
            this.addr			= skMsgClient.getInetAddress();
            this.skFileClient	= skFileClient;
            this.connection 	= true;
            
            thread.start();
        }

        // Отправка сообщения в сокет, связанный с клиентом
        public void send(String message) {
            out.println(message);
            out.flush();
        }
        
        public int getNumber() {
        	return number;
        }
        
        /**
         * получение адреса клиента
         */
        public InetAddress getInetAddress() {
        	return this.addr;
        }
        
        /**
         * группа получателей личных сообщений
         */
        public void setPrivateGroup(final List<String> group) {
        	this.privateGroup = group;
        }
        
        public List<String> getPrivateGroup() {
        	return this.privateGroup;
        }
        
        /**
         * проверка режима личной пересылки
         */
        public boolean isPrivateMode() {
        	return this.privateMode;
        }
        
        /**
         * установка режим личной пересылки
         */
        public void setPrivateMode(boolean mode) {
        	if ( mode == false ) {
        		privateGroup.clear();
        	}
        	this.privateMode = mode;
        }
        
        /**
         * получение имени клиента
         */
        public String getClientName() {
        	return this.name;
        }
        
        /**
         * установка имени клиента
         */
        public void setClientName(final String name) {
        	this.name = name;
        }
        
        /**
         * получение статуса клиента
         */
        public boolean isLogined() {
        	return this.logined;
        }
        
        /**
         * установка статуса клиента
         */
        public void setLogined(final boolean logined) {
        	this.logined = logined;
        }
        
        /**
         * завершение процесса
         */
        public void disconnect() {
        	connection = false;
        }
        
        /**
         * получение потока ввода для текстового сокета
         */
        public final InputStream getMsgInputStream() throws IOException {
        	if ( skMsgClient == null ) {
        		return null;
        	}
        	if ( skMsgClient.isClosed() ) {
        		return null;
        	}
        	return skMsgClient.getInputStream();
        }
        
        /**
         * получение потока вывода для текстового сокета
         */
        public final OutputStream getMsgOutputStream() throws IOException {
        	if ( skMsgClient == null ) {
        		return null;
        	}
        	if ( skMsgClient.isClosed() ) {
        		return null;
        	}
        	return skMsgClient.getOutputStream();
        }
        
        /**
         * получение потока ввода для файлового сокета
         */
        public final InputStream getFileInputStream() throws IOException {
        	if ( skFileClient == null ) {
        		return null;
        	}
        	if ( skFileClient.isClosed() ) {
        		return null;
        	}
        	return skFileClient.getInputStream();
        }
        
        /**
         * получение потока вывода для файлового сокета
         */
        public final OutputStream getFileOutputStream() throws IOException {
        	if ( skFileClient == null ) {
        		return null;
        	}
        	if ( skFileClient.isClosed() ) {
        		return null;
        	}
        	return skFileClient.getOutputStream();
        }
        
        /**
         * именованное сообщение
         */
        public String getNamingMessage(final String msg) {
        	if ( ! name.isEmpty() && this.isLogined() ) {
        		// добавим имя к сообщению
        		return name + ":  " + msg;
        	}
        	return "anonimous:  " + msg;
        }
        
        public String getServerMessage(final String msg) {
        	return "system:  " + msg;
        }

        @Override
        public void run() {
            // В отдельном потоке ждем данных от клиента
            try {
                String line = null;
                while ((line = in.readLine()) != null ) {
                	log.info("Handler[" + number + "]<< " + line);
                    
                	// проверка на наличие команды от пользователя
                    String response = server.handleServerCommand(this, line);
                    if ( ! response.equals("") ) {
                    	// получили команду - отправляем ответ от сервера
                    	send(getServerMessage(response));
                    	if ( ! connection ) {
                    		log.info("Disconnect user from server");
                    		thread.interrupt();
                    		thread.sleep(100);
                    	}
                    	continue;
                    }
                    if ( ! isLogined() ) {
                    	// пользователь не вошел - оповестим
                    	send(getServerMessage(MESSAGE_NOT_LOGINED));
                    	continue;
                    }
                    if ( isPrivateMode() ) {
                    	// включен приватный режим
                    	server.sendPrivateMessage(this, getNamingMessage(line));
                    } else {
                    	// иначе рассылка открытого сообщения
                        server.broadcast(getNamingMessage(line), name);
                    }
                }
            } catch (InterruptedException e) {
            	log.error("Interrupt current thread");
            	return;
            } catch (IOException e) {
                log.error("Failed to read from socket");
            } finally {
                Util.closeResource(in);
                Util.closeResource(out);
            }
        }
    }

    // рассылаем всем подписчикам
    public void broadcast(final String msg, final String senderLogin) {
        log.info("Broadcast to all: " + msg);
        for (ClientHandler handler : handlers) {
        	if ( handler.isLogined() && ! handler.getClientName().equals(senderLogin) ) {
        		handler.send(msg);
        	}
        }
    }
    
    /**
     * отправка личного сообщения
     */
    public void sendPrivateMessage(final ClientHandler handler, final String msg) {
    	log.info("Private message: " + msg);
    	ClientHandler client = null;
    	for ( String user : handler.getPrivateGroup() ) {
    		if ( (client = clientExists(user)) != null ) {
    			// нашли пользователя - отправляем сообщение
    			client.send(msg);
    		}
    	}
    	handler.send(msg);
    }
    
    /**
     * проверка налачия клиента с заданным именем
     */
    public ClientHandler clientExists(final String name) {
    	for ( ClientHandler handler : handlers ) {
    		if ( handler.getClientName().equals(name) ) {
    			return handler;
    		}
    	}
    	return null;
    }
    
    // ---------------------------------------------INNER CLASS---------------------------------------
    
    private static class FileHandler implements Runnable {
    	private ServerSocket srvFileSocket;
    	private final ChatThreadedServer server;
    	private Thread thread;
    	
    	/**
    	 * Конструктор файлового сервера
    	 * @param server 		- ссылка на сервер чата
    	 * @param srvFileSocket	- серверный сокет
    	 */
    	public FileHandler(final ChatThreadedServer server) {
    		this.thread			= new Thread(this);
    		this.server			= server;
    		try {
        		this.srvFileSocket	= new ServerSocket(PORT_FILE_SENDING);
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    		thread.start();
    	}
    	
    	public void run() {
    		log.info("Do file transfer");
    		while(true) {
    	    	Socket socket				= null;
    	    	FileOutputStream oStream 	= null;
    	    	BufferedReader bReader		= null;
    	    	try {	
    	    		// подождем
    	    		Thread.sleep(100);
    	    		
    	    		// получение клиентского сокета
        			socket = srvFileSocket.accept();
        			log.info("File handler connection: " + socket.getInetAddress().toString() + ":" + socket.getPort());
        			        			
        			// буферезированное считывание входного потока
        			bReader	= new BufferedReader(new InputStreamReader(socket.getInputStream()));
        			
        			// сохраняем файл на сервере
        			String fileSender	= bReader.readLine();
        			if ( fileSender.equals("") ) {
        				log.info("error! something wrong with file sender");
        				continue;
        			}
    	        	String fileName 	= bReader.readLine();
    	        	String fileSize		= bReader.readLine();
    	        	
    	        	int fSize = Long.valueOf(fileSize).intValue();
    	        	if ( fSize <= 0 ) {
    	        		log.info("No file to saving");
    	        		Thread.sleep(500);
    	        		continue;
    	        	}
    	        		
    	        	// получаем имя и расширение файла
    				String[] fileArgs = fileName.split("\\.");
    				String fExt 	= ( fileArgs.length > 1 ) ? fileArgs[1] : "";
    				String fName 	= fileArgs[0];
    				
    				// вывод данных
    				log.info("Save file:");
    				log.info("Sender: " + fileSender);
    				log.info("Name:" + fileName);
    				log.info("Size:" + fSize);
    				log.info("Ext:"  + fExt);
    				
    				if ( fExt.equals("") ) {
    					log.info("error! something wrong with file");
    					closeResource(bReader, oStream, socket);
    					continue;
    				}
    	        	
    				// директория с файлом
    				File dir	= new File(PATH_UPLOAD + "/" + fileSender);
    				if ( ! dir.exists() ) {
    					// нет директории - создадим
    					dir.mkdir();
    				}
    				
    				// сам файл
    				File file 	= new File(PATH_UPLOAD + "/" + fileSender + "/" + fileName);
    				if ( ! file.exists() ) {
    					// нет файла - создадим
    					file.createNewFile();
    				}
    				
    				oStream = new FileOutputStream(file);
    				byte[] buffer = new byte[fSize];
    				int bytesRead = 0;
    				
    				InputStream iStream = socket.getInputStream();	
    				//socket.shutdownInput();
    				if ( iStream == null ) {
    					log.info("error! something wrong with socket connection");
    					closeResource(bReader, oStream, socket);
    					continue;
    				}
    				
    				while ( (bytesRead = iStream.read(buffer)) > 0 ) {
    					// пишем данные в файл
    					log.info("Bytes received: " + bytesRead);
    					oStream.write(buffer, 0, bytesRead);
    				}
    				
    	        	// отправляем файл подписчикам
    				log.info("File has been saved");
    				server.sendFile(fileSender, file);
    				
    				// закрываем ресурсы
    				closeResource(bReader, oStream, socket);
    				
    	    	} catch (InterruptedException e) {
    	    		log.info("Close file server");
    	    		return;
    	    	} catch (IOException e) {
    	    		e.printStackTrace();
    	    	} catch (Exception e) {
    	    		e.printStackTrace();
    	    	} finally {
    	    		Util.closeResource(oStream);
    	    		Util.closeResource(bReader);
    	    	}    			
    		}
    	}
    	
    	/**
    	 * закрываем присланные ресурсы
    	 */
    	private void closeResource(Closeable...objects) {
    		for ( Closeable obj : objects ) {
    			Util.closeResource(obj);
    		}
    	}
    }
    
    // ---------------------------------------------INNER CLASS---------------------------------------
    
    /**
	 * Обработчик запросов на расшаривоние доски
	 */
	private static class DeskSharingHandler implements Runnable {
		private Thread thread;
		private Socket sharingSk;
		private PrintWriter out;
		private BufferedReader in;
		private String login;
		private final ChatThreadedServer server;
		
		// map обработчиков на предоставление доски K -> логин V - сокет
    	private static Hashtable<String, DeskSharingHandler> deskSharingTbl;
    	
    	static {
    		// инициализация статических данных
    		deskSharingTbl = new Hashtable<>();
    	}
    	
    	// сообщения
    	public static final String MESSAGE_SHARING = "sharing";
    	
		public DeskSharingHandler(final ChatThreadedServer server, final Socket sharingSk) throws IOException {
			this.thread 	= new Thread(this);
			this.server		= server;
			this.sharingSk 	= sharingSk;
			this.login		= "login";
			this.in			= new BufferedReader(new InputStreamReader(this.sharingSk.getInputStream()));
			this.out		= new PrintWriter(this.sharingSk.getOutputStream());
			
			// запускаем поток обработчика
			this.thread.start();
		}
		
		public String getLogin() {
			return this.login;
		}
		
		@Override
		public void run() {
			log.info("Do desk sharing processing");
			try {
				String line = null;
    			while ( (line = in.readLine()) != null ) {
    				if ( line.contains(":" + MESSAGE_SHARING) ) {
    					// сообщение приветствия - извлекаем логин
    					log.info("Desk sharing data: " + line);
    					this.login = line.split(":")[0];
    					continue;
    				}
    				
    				// поиск досок для просмотра, подключенных к текущей доске
    				log.info("Sharing desk data: " + line);
    				for ( Entry<String, Map<String, Object>> entry : DeskViewingHandler.deskViewingTbl.entrySet() ) {
    					if ( entry.getKey().equals(login) ) {
    						// отправляем данные доскам на просмотр
    						log.info("Viewing desk was found");
    						PrintWriter vOut = (PrintWriter)entry.getValue().get("out");
    						vOut.write(line+"\n");
    						vOut.flush();
    					}
    				}
    			}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				Util.closeResource(out);
				Util.closeResource(in);
				Util.closeResource(sharingSk);
			}
		}
	}
	
	/**
	 * Обработчик запросов на просмотр доски
	 */
	private static class DeskViewingHandler implements Runnable {
		private Thread thread;
		private ServerSocket srvViewingSk;
		private final ChatThreadedServer server;
		
		// map соединений на просмотр расшаренных досок
    	private static Hashtable<String, Map<String, Object>> deskViewingTbl; 
    	
    	public static final String MESSAGE_VIEWING = "viewing";
    	
		public DeskViewingHandler(final ChatThreadedServer server) {
    		this.thread			= new Thread(this);
    		this.server			= server;
    		deskViewingTbl		= new Hashtable<>();
    		try {
        		this.srvViewingSk	= new ServerSocket(PORT_DESK_VIEWING);
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    		thread.start();
    	}
		@Override
		public void run() {
			log.info("Do desk viewing processing");
			Socket sock 		= null;
			PrintWriter out 	= null;
			BufferedReader in	= null;
			try {
				while (true) {
					sock	= srvViewingSk.accept();
					in 		= new BufferedReader(new InputStreamReader(sock.getInputStream()));
					out		= new PrintWriter(sock.getOutputStream());
					
	    			// читаем заголовок в формате: login:viewing
					String[] params = in.readLine().split(":");
					if ( params.length < 3 ) {
						// что-то не так с заголовком - в игнор
						log.info("Desk View: something wrong with header");
						Util.closeResource(in);
						Util.closeResource(out);
						Util.closeResource(sock);
						continue;
					}
					log.info("New desk connection: login: " + params[0] + " action: " + params[1] + " to: " + params[2]);
					if ( params[1].equals(MESSAGE_VIEWING) ) {
						// соединение на просмотр - заносим в мап
						Map<String, Object> map = new HashMap<>();
						map.put("sock", sock);
						map.put("in", in);
						map.put("out", out);
						deskViewingTbl.put(params[2], map);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			
			} finally {
				Util.closeResource(in);
				Util.closeResource(out);
				Util.closeResource(sock);
			}
		}
	}
}
