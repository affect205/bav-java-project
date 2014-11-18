/**
 * @author alexbalu-alpha7@mail.ru
 */

package com.abalyschev.mychat;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Класс для хранения данных приложения (сообщения, конфиг, логгирование и т.д.)
 */
public class ChatStorage {
	
	protected static Logger logger = LoggerFactory.getLogger(ChatStorage.class);
	private Connection conn;
	private Statement stmt;
	private PreparedStatement pStmt;
	private static ChatStorage instance;
	
	static {
		instance = new ChatStorage();
	}
	
	private ChatStorage() {
		connect();
		initStorage();
	}
	
	public static ChatStorage getInstance() {
		return instance;
	}
	
	private void connect() {
		try {
			// загрузка класса драйвера
			Class.forName("org.sqlite.JDBC");
			
			// url бд
			String dbUrl = "jdbc:sqlite:db/mychat.db";
			logger.info("Open Database: " + dbUrl);
			
			// создание соединения
			conn = DriverManager.getConnection(dbUrl);
			
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Создание структуры БД
	 */
	private void initStorage() {	
		StringBuilder query = new StringBuilder();
		query.append("CREATE TABLE IF NOT EXISTS messages ( " 
			+ " id INTEGER PRIMARY KEY NOT NULL, "
			+ " author TEXT DEFAULT 'anonymous', "
			+ " datetime INTEGER, "
			+ " message TEXT, "
			+ " type TEXT DEFAULT 'common', "
			+ " ishidden INTEGER DEFAULT 0 "
			+ ")\n");
		try {
			stmt = conn.createStatement();
			stmt.executeUpdate(query.toString());
			stmt.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		logger.info("Database has been created");
	}
	
	/**
	 * Список последних сообщений
	 */
	public List<ChatMessage> getLastMessages() {
		List<ChatMessage> lastMsgLst = new ArrayList<>();
		try {
			String query = "SELECT * FROM messages LIMIT 10 ORDER BY messages.datetime DESC";
			stmt = conn.createStatement();
			
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next()) {
				// получение сообщения
				ChatMessage msg = new ChatMessage(rs.getString("message"), 
						rs.getString("author"), 
						rs.getInt("datetime"));
				lastMsgLst.add(msg);
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		return lastMsgLst;
	}
	
	/**
	 * Добавление нового сообщения в БД
	 * @return
	 */
	public boolean addMessage(final ChatMessage msg) {
		boolean res = true;
		try {
			String query = "INSERT INTO messages(message, author, datetime) VALUES(?, ?, ?)";
			pStmt = conn.prepareStatement(query);
			pStmt.setString(1, msg.getMessage());
			pStmt.setString(2, msg.getAuthor());
			pStmt.setLong(3, msg.getDatetime());
			res = pStmt.execute();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return res;
	}
}