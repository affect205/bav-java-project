package com.abalyschev.mychat;

public class MyChat2 {
	public static void main(String[] args) {
		try {
			// запуск клиента 2
			ChatGUI.startChat("Nick");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}