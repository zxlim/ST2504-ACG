/*
**	[ST2504 Applied Cryptography Assignment]
**	[Encrypted Chat Program]
**
**	Aeron Teo (P1500725)
**	Aiman Abdul Rashid (P1529335)
**	Gerald Peh (P1445972)
**	Lim Zhao Xiang (P1529559)
*/

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ClientGUI extends JFrame implements ActionListener{

	protected static final long serialVersionUID = 1112122200L;
	//Label for message input box
	private JLabel messageLabel;
	private JTextField messageField;

	//Chat GUI Buttons
	private JButton login, logout, whoIsIn, sendMsg, help;
	//Chat GUI message display box
	private JTextArea messageBox;

	//Label to store username
	private JLabel userDetails;

	//Client instance
	private Client client;
	//Client instance connection
	private boolean connected;

	private String serverAddress, nameGUI, pwGUI;
	private int serverPort;

	//Constructor
	ClientGUI(final String serverAddress, final int serverPort, final String nameGUI, final String pwGUI) {

		super("PPAP Secure Chat");

		this.serverAddress = serverAddress;
		this.serverPort = serverPort;
		this.nameGUI = nameGUI;
		this.pwGUI = pwGUI;

		//Different parts of the Chat GUI
		JPanel topPanel = new JPanel(new GridLayout(5,1));
		JPanel centerPanel = new JPanel(new GridLayout(1,1));
		JPanel bottomPanel = new JPanel(new GridLayout(4,1));
		topPanel.setBackground(Color.LIGHT_GRAY);
		centerPanel.setBackground(Color.LIGHT_GRAY);
		bottomPanel.setBackground(Color.LIGHT_GRAY);

		//Panel for Server Info
		JPanel serverInfoPanel = new JPanel(new GridLayout(1, 2));
		serverInfoPanel.setBackground(Color.LIGHT_GRAY);
		serverInfoPanel.add(new JLabel("Server Address: " + serverAddress, SwingConstants.CENTER));
		serverInfoPanel.add(new JLabel("Port Number: " + serverPort, SwingConstants.CENTER));

		//User details label
		userDetails = new JLabel("Logging in as user: " + nameGUI, SwingConstants.CENTER);

		//Panel for buttons
		JPanel buttonPanel = new JPanel(new GridLayout(1, 3));
		buttonPanel.setBackground(Color.LIGHT_GRAY);
		login = new JButton("Join Chat");
		login.addActionListener(this);
		logout = new JButton("Logout");
		logout.addActionListener(this);
		logout.setEnabled(false);
		whoIsIn = new JButton("List Users");
		whoIsIn.addActionListener(this);
		whoIsIn.setEnabled(false);

		buttonPanel.add(login);
		buttonPanel.add(logout);
		buttonPanel.add(whoIsIn);

		//Add the different parts to topPanel
		topPanel.add(new JLabel("\n", SwingConstants.CENTER));
		topPanel.add(serverInfoPanel);
		topPanel.add(userDetails);
		topPanel.add(new JLabel("\n", SwingConstants.CENTER));
		topPanel.add(buttonPanel);

		//Main Chat GUI with message box
		messageBox = new JTextArea("  Ready to join chat.\n\n", 70, 70);
		messageBox.setEditable(false);
		messageBox.setLineWrap(true);
		messageBox.setBackground(Color.WHITE);
		JScrollPane messageBoxScroll = new JScrollPane(messageBox);
		messageBoxScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		centerPanel.add(messageBoxScroll);

		//Message box
		messageLabel = new JLabel("Enter your message below", SwingConstants.CENTER);
		messageLabel.setVisible(false);
		messageField = new JTextField("");
		messageField.setEditable(false);
		messageField.setBackground(Color.WHITE);
		messageField.setVisible(false);

		//Button to send chat message
		JPanel btmBtnPanel = new JPanel(new GridLayout(1, 2));
		btmBtnPanel.setBackground(Color.LIGHT_GRAY);
		sendMsg = new JButton("Send Message");
		sendMsg.addActionListener(this);
		sendMsg.setEnabled(false);
		sendMsg.setVisible(false);
		help = new JButton("Help");
		help.addActionListener(this);
		help.setEnabled(false);
		help.setVisible(false);
		btmBtnPanel.add(sendMsg);
		btmBtnPanel.add(help);

		bottomPanel.add(messageLabel);
		bottomPanel.add(messageField);
		bottomPanel.add(btmBtnPanel);
		bottomPanel.add(new JLabel("\n", SwingConstants.CENTER));

		//Add the different panels to frame
		add(topPanel, BorderLayout.NORTH);
		add(centerPanel, BorderLayout.CENTER);
		add(bottomPanel, BorderLayout.SOUTH);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(600, 600);
		setVisible(true);
		login.requestFocus();
	}

	// called by the Client to append text in the TextArea
	void append(String str) {
		messageBox.append("  " + str);
		messageBox.setCaretPosition(messageBox.getText().length() - 1);
	}

	void displayDialog(String msg) {
		JOptionPane.showMessageDialog(null, msg, "PPAP Secure Chat", JOptionPane.WARNING_MESSAGE);
	}

	// called by the GUI is the connection failed
	// we reset our buttons, label, textfield
	void connectionFailed() {
		//login.setEnabled(true);
		logout.setEnabled(false);
		whoIsIn.setEnabled(false);
		sendMsg.setEnabled(false);
		help.setEnabled(false);
		messageField.removeActionListener(this);
		connected = false;
		System.exit(0);
	}

	//Button Actions
	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();

		if (o == logout) {
			client.sendMessage(new Message(Message.LOGOUT));
			displayDialog("You have logged out from the chat server.");
			connectionFailed();
		}

		if (o == whoIsIn) {
			client.sendMessage(new Message(Message.WHOISIN));
			return;
		}

		if (o == sendMsg && connected) {
			client.sendMessageGUI(messageField.getText());
			messageField.setText("");
			return;
		}

		if (o == help) {
			append("\n\n  [Help Menu]");
			append("\n  Below are a few tips to get you started.\n");
			append("\n  Press the Help button to show this help menu.");
			append("\n  Press the List Users button to show all online users.");
			append("\n  Press the Logout button to logout from this server.");
			append("\n  Use '/whisper [user] [message]' to privately message an online user.");
			append("\n  To send a message to everyone, just type and press enter or the send message button.\n\n");
			return;
		}

		if (connected) {
			client.sendMessageGUI(messageField.getText());
			messageField.setText("");
			return;
		}

		if(o == login) {
			append("Logging in...\n");

			try {
				client = new Client(serverAddress, serverPort, this, nameGUI, pwGUI);

				if (!client.start()) {
					System.exit(0);
				}
			} catch (Exception ex) {
				System.out.println(ex);
				ex.printStackTrace();
				System.exit(0);
			}

			userDetails.setText("Logged in as user: " + nameGUI);
			messageField.setEditable(true);
			messageLabel.setVisible(true);
			messageField.setVisible(true);
			sendMsg.setVisible(true);
			help.setVisible(true);
			login.setEnabled(false);
			logout.setEnabled(true);
			whoIsIn.setEnabled(true);
			sendMsg.setEnabled(true);
			help.setEnabled(true);
			messageField.requestFocus();
			messageField.addActionListener(this);

			connected = true;
			pwGUI = null;
		}
	}

	//Main method
	public static void main(String[] args) {
		String addr = "", port = "", username = "", password = "";
		int portNumber = -1;

		try {
			//Server Connection Panel
			JPanel connectPanel = new JPanel(new GridLayout(2,2));

			JLabel addrLabel = new JLabel("Server Address\t");
			JTextField addrInput = new JTextField(15);
			JLabel portLabel = new JLabel("Server Port\t");
			JTextField portInput = new JTextField(15);
			connectPanel.add(addrLabel);
			connectPanel.add(addrInput);
			connectPanel.add(portLabel);
			connectPanel.add(portInput);

			int connectResult = JOptionPane.showConfirmDialog(null, connectPanel, "PPAP Secure Chat", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

			if (connectResult == JOptionPane.YES_OPTION) {
				addr = addrInput.getText().trim();

				if (addr == null || addr.isEmpty()) {
					JOptionPane.showMessageDialog(null, "Server Address cannot be left blank.", "PPAP Secure Chat", JOptionPane.WARNING_MESSAGE);
					return;
				}
				try {
					portNumber = Integer.parseInt(portInput.getText());
				} catch (NumberFormatException e) {
					JOptionPane.showMessageDialog(null, "Invalid port number.\nPlease enter a port number between 0 and 65535.", "PPAP Secure Chat", JOptionPane.WARNING_MESSAGE);
					return;
				}
			} else {
				return;
			}

			//Login Panel
			JPanel loginPanel = new JPanel(new GridLayout(2,2));

			JLabel userLabel = new JLabel("Username\t");
			JTextField userInput = new JTextField(15);
			JLabel pwLabel = new JLabel("Password\t");
			JPasswordField pwInput = new JPasswordField(15);
			loginPanel.add(userLabel);
			loginPanel.add(userInput);
			loginPanel.add(pwLabel);
			loginPanel.add(pwInput);

			int loginResult = JOptionPane.showConfirmDialog(null, loginPanel, "PPAP Secure Chat", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

			if (loginResult == JOptionPane.YES_OPTION) {
				username = userInput.getText();
				password = new String(pwInput.getPassword());

				if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
					JOptionPane.showMessageDialog(null, "Username and/ or password cannot be left blank.", "PPAP Secure Chat", JOptionPane.WARNING_MESSAGE);
					return;
				}
			} else {
				return;
			}
		} catch (NullPointerException e) {
			return;
		}

		new ClientGUI(addr, portNumber, username, password);
	}
}
