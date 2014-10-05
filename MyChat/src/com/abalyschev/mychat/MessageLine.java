/**
 * @author alexbalu-alpha7@mail.ru
 */
package com.abalyschev.mychat;

import java.awt.GridLayout;

import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class MessageLine {

	private JLabel timeLb;
	private JLabel authorLoginLb;
	private JLabel authorImgLb;
	private JTextArea messageTA;
	
	public MessageLine(JPanel root) {
		
		timeLb 			= new JLabel("10:12:02");
		authorLoginLb	= new JLabel("AlexDiaz");
		authorImgLb		= new JLabel("Icon");
		
		messageTA		= new JTextArea(10, 30);
		messageTA.setWrapStyleWord(true);
		messageTA.setEditable(false);
		
		messageTA.setText("....................................................");
		
		JPanel headerPnl	= new JPanel();
		GridLayout headerLt	= new GridLayout(2, 1);
		headerPnl.setLayout(headerLt);
		
		headerPnl.add(new JLabel());
		headerPnl.add(timeLb);
		headerPnl.add(authorLoginLb);
		
	}
}