/**
 * @author alexbalu-alpha7@mail.ru
 */
package com.abalyschev.mychat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChatThreadedServer {

    protected static Logger log = LoggerFactory.getLogger("ThreadedServer");
    private static final int PORT = 19000;
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
    
    private static final String MESSAGE_NOT_LOGINED = "You are not logined!";
    
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
        ServerSocket serverSocket = new ServerSocket(PORT);
        while (true) {

            // блокируемся и ждем клиента
            Socket socket = serverSocket.accept();
            log.info("Client connected: " + socket.getInetAddress().toString() + ":" + socket.getPort());

            // создаем обработчик
            ClientHandler handler = new ClientHandler(this, socket, counter++);
            // @TODO service.submit(handler);
            handlers.add(handler);
        }
    }
    
    /**
     * обработчик клиентских команд 
     */
    protected String handleServerCommand(final ClientHandler handler, final String command, BufferedReader bReader) {
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
    		msg = doFileTransfer(handler, bReader);
    		
    	} else if ( COMMAND_USERLIST.equals(params[0]) ) {
    		msg = getLoginedUsers();
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
    public String getLoginedUsers() {
    	StringBuilder users = new StringBuilder();
    	for ( ClientHandler client : handlers ) {
    		if ( ! client.getClientName().equals("") ) {
    			users.append(client.getClientName());
        		users.append(";");
    		}
    	}
    	users.deleteCharAt(users.lastIndexOf(";"));
    	return users.toString();
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
    	return "you are logined";
    } 
    
    /**
     * рассылка передаваемого файла
     * @param handler
     * @param bReader
     * @return
     */
    protected String doFileTransfer(final ClientHandler handler, BufferedReader bReader) {
    	log.info("Do file transfer");
    	FileOutputStream oStream = null;
    	try {	
    		// @TODO: рассмотреть эту идею
//    		BufferedInputStream in = new BufferedInputStream(handler.getInputStream());
//    		try ( DataInputStream d = new DataInputStream(in) ) {
//    			String fileName = d.readUTF();
//    			String fileSize	= d.readUTF();
//    			String path = PATH_UPLOAD + "/" + handler.getClientName() + "/" + fileName;
//    			Files.copy(d, Paths.get(path));
//    			d.close();
//    		}
    		// сохраняем файл на сервере
        	String fileName = bReader.readLine();
        	String fileSize	= bReader.readLine();
        	int fSize = Long.valueOf(fileSize).intValue();
        	
        	// получаем имя и расширение файла
			String[] fileArgs = fileName.split("\\.");
			String fExt 	= ( fileArgs.length > 1 ) ? fileArgs[1] : "";
			String fName 	= fileArgs[0];
			
			
			log.info("Save file:");
			log.info("Name:" + fileName);
			log.info("Size:" + fSize);
			log.info("Ext:"  + fExt);
			
			if ( fExt.equals("") ) {
				return "error! something wrong with file";
			}
        	
			File dir	= new File(PATH_UPLOAD + "/" + handler.getClientName());
			if ( ! dir.exists() ) {
				// нет директории - создадим
				dir.mkdir();
			}
			
			// сам файл
			File file 	= new File(PATH_UPLOAD + "/" + handler.getClientName() + "/" + fileName);
			if ( ! file.exists() ) {
				// нет файла - создадим
				file.createNewFile();
			}
			
			oStream = new FileOutputStream(file);
			byte[] buffer = new byte[fSize];
			int bytesRead = 0;
			
			InputStream iStream = handler.getInputStream();			
			if ( iStream == null ) {
				return "error! something wrong with socket connection";
			}
			
			while ( (bytesRead = iStream.read(buffer)) > 0 ) {
				// пишем данные в файл
				log.info("Bytes received: " + bytesRead);
				oStream.write(buffer, 0, bytesRead);
			}
			
        	// отправляем файл подписчикам
			log.info("Send file to users");
			
    	} catch (IOException e) {
    		e.printStackTrace();
    	} catch (Exception e) {
    		e.printStackTrace();
    	} finally {
    		Util.closeResource(oStream);
    	}
    	return "file has sent";
    }

    /*
    Для каждого присоединившегося пользователя создается поток обработки независимый от остальных
     */

    class ClientHandler /*extends Thread*/ implements Runnable {

        private ChatThreadedServer server;
        private BufferedReader in;
        private PrintWriter out;
        private Socket skClient;

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
        
        // список получателей личных сообщений
        private List<String> privateGroup = new ArrayList<String>();
        
        public ClientHandler(ChatThreadedServer server, Socket socket, int counter) throws Exception {
        	thread	= new Thread(this);
        	this.server	= server;
            in 		= new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out 	= new PrintWriter(socket.getOutputStream());
            number 	= counter;
            skClient	= socket;
            connection 	= true;
            
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
         * получение потока ввода для текущего сокета
         */
        public final InputStream getInputStream() throws IOException {
        	if ( skClient == null ) {
        		return null;
        	}
        	if ( skClient.isClosed() ) {
        		return null;
        	}
        	return this.skClient.getInputStream();
        }
        
        /**
         * получение потока вывода для итекущего сокета
         */
        public final OutputStream getOutputStream() throws IOException {
        	if ( skClient == null ) {
        		return null;
        	}
        	if ( skClient.isClosed() ) {
        		return null;
        	}
        	return this.skClient.getOutputStream();
        }
        
        /**
         * именованное сообщение
         */
        public String getNamingMessage(final String msg) {
        	if ( ! name.isEmpty() && this.isLogined() ) {
        		// добавим имя к сообщению
        		return name + ":\t" + msg;
        	}
        	return "anonimous:\t" + msg;
        }
        
        public String getServerMessage(final String msg) {
        	return "system:\t" + msg;
        }

        @Override
        public void run() {
            // В отдельном потоке ждем данных от клиента
            try {
                String line = null;
                while ((line = in.readLine()) != null ) {
                	log.info("Handler[" + number + "]<< " + line);
                    
                	// проверка на наличие команды от пользователя
                    String response = server.handleServerCommand(this, line, in);
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
                        server.broadcast(getNamingMessage(line));
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
    public void broadcast(final String msg) {
        log.info("Broadcast to all: " + msg);
        for (ClientHandler handler : handlers) {
        	if ( handler.isLogined() ) {
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
}
