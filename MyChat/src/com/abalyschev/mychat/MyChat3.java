package com.abalyschev.mychat;

public class MyChat3 {
	public static void main(String[] args) {
		try {
			// запуск клиента 2
			ChatGUI.startChat("Piter");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}