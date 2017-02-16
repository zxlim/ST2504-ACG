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
	// will hold "Enter message"
	private JLabel label;
	// to hold the Username and later on the messages
	private JTextField tf;
	// to hold the server address an the port number
	private JTextField tfServer, tfPort;
	// to Logout and get the list of the users
	private JButton login, logout, whoIsIn;
	// for the chat room
	private JTextArea ta;
	// if it is for connection
	private boolean connected;
	//Client instance
	private Client client;

	private String serverAddress, nameGUI, pwGUI;
	private int serverPort;

	//Constructor
	ClientGUI(final String serverAddress, final int serverPort, final String nameGUI, final String pwGUI) {

		super("PPAP Secure Chat");

		this.serverAddress = serverAddress;
		this.serverPort = serverPort;
		this.nameGUI = nameGUI;
		this.pwGUI = pwGUI;

		JPanel northPanel = new JPanel(new GridLayout(3,1));
		JPanel serverAndPort = new JPanel(new GridLayout(1, 4, 1, 2));

		serverAndPort.add(new JLabel("Server Address: " + serverAddress));
		serverAndPort.add(new JLabel("Port Number: " + serverPort));
		northPanel.add(serverAndPort);

		// the Label and the TextField
		label = new JLabel("Enter your message below", SwingConstants.CENTER);
		northPanel.add(label);
		tf = new JTextField("");
		tf.setEditable(false);
		tf.setBackground(Color.WHITE);
		label.setVisible(false);
		tf.setVisible(false);
		northPanel.add(tf);
		add(northPanel, BorderLayout.NORTH);

		// The CenterPanel which is the chat room
		ta = new JTextArea("Welcome to the Chat room\n", 80, 80);
		JPanel centerPanel = new JPanel(new GridLayout(1,1));
		centerPanel.add(new JScrollPane(ta));
		ta.setEditable(false);
		add(centerPanel, BorderLayout.CENTER);

		// the 3 buttons
		login = new JButton("Join Chat");
		login.addActionListener(this);
		logout = new JButton("Logout");
		logout.addActionListener(this);
		logout.setEnabled(false);
		whoIsIn = new JButton("List Users");
		whoIsIn.addActionListener(this);
		whoIsIn.setEnabled(false);

		JPanel southPanel = new JPanel();
		southPanel.add(login);
		southPanel.add(logout);
		southPanel.add(whoIsIn);
		add(southPanel, BorderLayout.SOUTH);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(600, 600);
		setVisible(true);
		tf.requestFocus();

	}

	// called by the Client to append text in the TextArea
	void append(String str) {
		ta.append(str);
		ta.setCaretPosition(ta.getText().length() - 1);
	}

	void displayDialog(String msg) {
		JOptionPane.showMessageDialog(null, msg, "PPAP Secure Chat", JOptionPane.WARNING_MESSAGE);
	}

	// called by the GUI is the connection failed
	// we reset our buttons, label, textfield
	void connectionFailed() {
		login.setEnabled(true);
		logout.setEnabled(false);
		whoIsIn.setEnabled(false);
		tf.removeActionListener(this);
		connected = false;
		displayDialog("You have disconnected from the chat server.");
		System.exit(0);
	}

	//Button Actions
	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();

		if(o == logout) {
			client.sendMessage(new Message(Message.LOGOUT));
			connectionFailed();
		}

		if(o == whoIsIn) {
			client.sendMessage(new Message(Message.WHOISIN));
			return;
		}

		if (connected) {
			client.sendMessageGUI(tf.getText());
			tf.setText("");
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

			tf.setEditable(true);
			connected = true;
			label.setVisible(true);
			tf.setVisible(true);
			login.setEnabled(false);
			logout.setEnabled(true);
			whoIsIn.setEnabled(true);

			tf.addActionListener(this);

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
