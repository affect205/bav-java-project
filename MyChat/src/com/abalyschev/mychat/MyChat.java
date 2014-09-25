package com.abalyschev.mychat;

public class MyChat {
	public static void main(String[] args) {
		try {
			// запуск клиента 1
			ChatGUI.startChat("AlexDiaz");
			// запуск клиента 2
			//ChatGUI.startChat("Nick");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}