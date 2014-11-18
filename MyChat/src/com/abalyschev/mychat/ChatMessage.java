/**
 * @author alexbalu-alpha7@mail.ru
 */

package com.abalyschev.mychat;

import java.util.HashMap;
import java.util.Map;

public class ChatMessage {
	// текст сообщения
	private String message;
	// автор сообщения
	private String author;
	// время отправки
	private long datetime;
	// является ли сообещние скрытым 
	private int isHidden;
	// тип сообщения
	private String type;
	
	public ChatMessage(final String message, final String author, final long datetime) {
		this.message 	= message;
		this.author 	= author;
		this.datetime 	= datetime;
		this.isHidden 	= 0;
		this.type 		= "common";
	}
	
	public Map<String, Object> getMessageParams() {
		Map<String, Object> msgMap = new HashMap<>();
		msgMap.put("message",	message);
		msgMap.put("author", 	author);
		msgMap.put("datetime", 	datetime);
		msgMap.put("ishidden", 	isHidden);
		msgMap.put("type", 		type);
		return msgMap;
	}
	
	public String getMessage() {
		return message;
	}
	
	public String getAuthor() {
		return author;
	}
	
	public long getDatetime() {
		return datetime;
	}
}