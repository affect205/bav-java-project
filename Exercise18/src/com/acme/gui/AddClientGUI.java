package com.acme.gui;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.text.ParseException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.acme.mock.BankClient;

public class AddClientGUI extends JFrame {

	private static BankClient bankclient = new BankClient();
	
	private static JButton sendBtn		= new JButton("SEND");;
	private static JLabel titleLb		= new JLabel("New client profile");;
	private static JLabel nameLb		= new JLabel("Login:");
	private static JLabel genderLb 		= new JLabel("Gender:"); 
	private static JLabel accountLb 	= new JLabel("Account:");
	private static JLabel balanceLb 	= new JLabel("Balance:");
	private static JLabel overdraftLb 	= new JLabel("Overdraft:");
	private static JLabel outputLb		= new JLabel();
	private static JTextField name 		= new JTextField();
	private static JTextField balance 	= new JTextField();
	private static JTextField overdraft = new JTextField();
	private static JPanel dataPnl		= new JPanel();
	private static JPanel rootPnl		= new JPanel();
	private static JComboBox gender		= new JComboBox(new String[] { "Male", "Female" });
	private static JList account		= new JList(new String[] { "Checking", "Saving" });
	
	public AddClientGUI(final String name) {
		super(name);
		setResizable(false);
	}

	/**
	 * Create the GUI and show it. For thread safety, this method is invoked
	 * from the event dispatch thread.
	 */
	private static void createAndShowGUI() {

		AddClientGUI frame = new AddClientGUI("Bank Application Client");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// control elements	properties		
		titleLb.setFont(new Font("Arial", Font.LAYOUT_LEFT_TO_RIGHT, 18));
		titleLb.setHorizontalAlignment(SwingConstants.LEFT);
		
		nameLb.setFont(new Font("Arial", Font.LAYOUT_LEFT_TO_RIGHT, 14));
		genderLb.setFont(new Font("Arial", Font.LAYOUT_LEFT_TO_RIGHT, 14));
		accountLb.setFont(new Font("Arial", Font.LAYOUT_LEFT_TO_RIGHT, 14));
		balanceLb.setFont(new Font("Arial", Font.LAYOUT_LEFT_TO_RIGHT, 14));
		overdraftLb.setFont(new Font("Arial", Font.LAYOUT_LEFT_TO_RIGHT, 14));
		
		gender.setSelectedIndex(0);
		account.setSelectedIndex(0);
		account.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		// layouts
		GridLayout dataLt	= new GridLayout(6, 2);
		dataLt.setVgap(10);
		dataLt.setHgap(10);
		BoxLayout rootLt	= new BoxLayout(rootPnl, BoxLayout.Y_AXIS);
		
		
		// add components to data panel
		dataPnl.setLayout(dataLt);
		dataPnl.add(titleLb);
		dataPnl.add(sendBtn);
		dataPnl.add(nameLb);
		dataPnl.add(name);
		dataPnl.add(genderLb);
		dataPnl.add(gender);
		dataPnl.add(accountLb);
		dataPnl.add(account);
		dataPnl.add(balanceLb);
		dataPnl.add(balance);
		dataPnl.add(overdraftLb);
		dataPnl.add(overdraft);
	
		// add components to root panel
		rootPnl.setLayout(rootLt);
		rootPnl.add(dataPnl);
		rootPnl.add(outputLb);
		
		// add event listeners
		sendBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				
				// validate data
				if ( validateData() == true ) {
					
					System.out.println("Msg: correct data...");
					
					StringBuilder feed = new StringBuilder();
					feed.append(name.getText());
					feed.append("|");
					feed.append(gender.getSelectedItem().toString());
					feed.append("|");
					feed.append(account.getSelectedValue().toString());
					feed.append("|");
					feed.append(balance.getText());
					feed.append("|");
					feed.append(overdraft.getText());
					
					// send data to bank server
					bankclient.run("-add", feed.toString());
				}

				System.out.println("Send data...");
			}
		});
		
		// set root frame
		frame.getContentPane().add(rootPnl);
		frame.pack();
		//frame.setSize(480, 300);
		frame.setVisible(true);
	}
	
	protected static boolean validateData() {
		
		if ( name.getText().trim().equals("") || balance.getText().trim().equals("") 
				|| overdraft.getText().trim().equals("") ) {
			// bad format data
			System.out.println("Err: empty text field...");
			return false;
		}
		
		try {
			NumberFormat.getInstance().parse(balance.getText());
			NumberFormat.getInstance().parse(overdraft.getText());
		} catch(ParseException e) {
			// bad format data
			System.out.println("Err: wrong number format...");
			return false;
		}
		
		return true;
	}

	public static void main(final String[] args) {
		/* Use an appropriate Look and Feel */
		try {
			//UIManager
			//		.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
		} catch (UnsupportedLookAndFeelException ex) {
			ex.printStackTrace();
		} catch (IllegalAccessException ex) {
			ex.printStackTrace();
		} catch (InstantiationException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}

		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}
}
