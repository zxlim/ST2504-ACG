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

	//ClientGUI variables
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
		messageBox.append("  " + str + "\n");
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
	public void actionPerformed(ActionEvent event) {
		Object objEvent = event.getSource();

		if (objEvent == login) {
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
			return;
		}

		if (objEvent == logout) {
			client.sendMessage(new Message(Message.LOGOUT));
			displayDialog("You have logged out from the chat server.");
			connectionFailed();
		}

		if (objEvent == whoIsIn) {
			client.sendMessage(new Message(Message.WHOISIN));
			return;
		}

		if (objEvent == help) {
			append("\n\n  [Help Menu]");
			append("Below are a few tips to get you started.");
			append("Press the Help button to show this help menu.");
			append("Press the List Users button to show all online users.");
			append("Press the Logout button to logout from this server.");
			append("Use '/whisper [user] [message]' or '/w [user] [message]' to privately message an online user.");
			append("To send a message to everyone, just type and press enter or the send message button.\n");
			return;
		}

		if (objEvent == sendMsg || connected) {
			final String msg = messageField.getText().trim();
			if (msg == null || msg.isEmpty()) {
				return;
			} else {
				client.sendMessageGUI(msg);
				messageField.setText("");
				return;
			}
		}
	}

	//Main method
	public static void main(String[] args) {
		String addr = "", port = "", username = "", password = "";
		int portNumber = -1;

		try {
			//Main Login Panel
			JPanel loginGUI = new JPanel(new GridLayout(3,1));
			JLabel welcomeLabel = new JLabel("Please enter Chat Server details and User Credentials", SwingConstants.CENTER);

			//Server Connection Panel
			JPanel connectPanel = new JPanel(new GridLayout(2,2));

			JLabel addrLabel = new JLabel("Server Address");
			JTextField addrInput = new JTextField();
			JLabel portLabel = new JLabel("Server Port");
			JTextField portInput = new JTextField();
			connectPanel.add(addrLabel);
			connectPanel.add(addrInput);
			connectPanel.add(portLabel);
			connectPanel.add(portInput);

			//User Credentials Panel
			JPanel loginPanel = new JPanel(new GridLayout(2,2));

			JLabel userLabel = new JLabel("Username");
			JTextField userInput = new JTextField();
			JLabel pwLabel = new JLabel("Password");
			JPasswordField pwInput = new JPasswordField();
			loginPanel.add(userLabel);
			loginPanel.add(userInput);
			loginPanel.add(pwLabel);
			loginPanel.add(pwInput);

			loginGUI.add(welcomeLabel);
			loginGUI.add(connectPanel);
			loginGUI.add(loginPanel);

			int result = JOptionPane.showConfirmDialog(null, loginGUI, "PPAP Secure Chat", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

			if (result == JOptionPane.YES_OPTION) {
				addr = addrInput.getText().trim();
				port = portInput.getText().trim();
				username = userInput.getText().trim();
				password = new String(pwInput.getPassword());

				if (addr == null || addr.isEmpty() || port == null || port.isEmpty()) {
					JOptionPane.showMessageDialog(null, "Server address and/ or port number cannot be left blank.", "PPAP Secure Chat", JOptionPane.WARNING_MESSAGE);
					return;
				}

				try {
					//Parse String port into integer
					portNumber = Integer.parseInt(port);
				} catch (NumberFormatException e) {
					JOptionPane.showMessageDialog(null, "Invalid port number.\nPlease enter a port number between 0 and 65535.", "PPAP Secure Chat", JOptionPane.WARNING_MESSAGE);
					return;
				}

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
