
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ClientGUI extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	// will first hold "Username:", later on "Enter message"
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
	// the Client object
	private Client client;
	// the default port number
	private int defaultPort;
	private String defaultHost;

	// Constructor connection receiving a socket number
	ClientGUI(String host, int port) {

		super("PPAP Secure Chat");
		defaultPort = port;
		defaultHost = host;

		// The NorthPanel with:
		JPanel northPanel = new JPanel(new GridLayout(3,1));
		// the server name anmd the port number
		JPanel serverAndPort = new JPanel(new GridLayout(1,5, 1, 3));
		// the two JTextField with default value for server address and port number
		tfServer = new JTextField(host);
		tfPort = new JTextField("" + port);
		tfPort.setHorizontalAlignment(SwingConstants.RIGHT);

		serverAndPort.add(new JLabel("Server Address:  "));
		serverAndPort.add(tfServer);
		serverAndPort.add(new JLabel("Port Number:  "));
		serverAndPort.add(tfPort);
		serverAndPort.add(new JLabel(""));
		// adds the Server an port field to the GUI
		northPanel.add(serverAndPort);

		// the Label and the TextField
		label = new JLabel("Enter your username below", SwingConstants.CENTER);
		northPanel.add(label);
		tf = new JTextField("Anonymous");
		tf.setBackground(Color.WHITE);
		northPanel.add(tf);
		add(northPanel, BorderLayout.NORTH);

		// The CenterPanel which is the chat room
		ta = new JTextArea("Welcome to the Chat room\n", 80, 80);
		JPanel centerPanel = new JPanel(new GridLayout(1,1));
		centerPanel.add(new JScrollPane(ta));
		ta.setEditable(false);
		add(centerPanel, BorderLayout.CENTER);

		// the 3 buttons
		login = new JButton("Login");
		login.addActionListener(this);
		logout = new JButton("Logout");
		logout.addActionListener(this);
		logout.setEnabled(false);		// you have to login before being able to logout
		whoIsIn = new JButton("Who is in");
		whoIsIn.addActionListener(this);
		whoIsIn.setEnabled(false);		// you have to login before being able to Who is in

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
	// called by the GUI is the connection failed
	// we reset our buttons, label, textfield
	void connectionFailed() {
		login.setEnabled(true);
		logout.setEnabled(false);
		whoIsIn.setEnabled(false);
		label.setText("Enter your username below");
		tf.setText("Anonymous");
		// reset port number and host name as a construction time
		tfPort.setText("" + defaultPort);
		tfServer.setText(defaultHost);
		// let the user change them
		tfServer.setEditable(false);
		tfPort.setEditable(false);
		// don't react to a <CR> after the username
		tf.removeActionListener(this);
		connected = false;
	}

	//Button Actions
	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();
		// if it is the Logout button
		if(o == logout) {
			client.sendMessage(new Message(Message.LOGOUT));
			return;
		}
		// if it the who is in button
		if(o == whoIsIn) {
			client.sendMessage(new Message(Message.WHOISIN));
			return;
		}

		// ok it is coming from the JTextField
		if(connected) {
			// just have to send the message
			//client.sendMessage(new Message(Message.MESSAGE, tf.getText()));
			tf.setText("");
			return;
		}

		//Temporary disabled GUI login to prevent compile errors
		if(o == login) {
			JOptionPane.showMessageDialog(null, "GUI Login not yet supported. Please use 'java Client' instead of 'java ClientGUI'");
			return;
			/*// ok it is a connection request
			String username = tf.getText().trim();
			// empty username ignore it
			if(username.length() == 0)
			return;
			// empty serverAddress ignore it
			String server = tfServer.getText().trim();
			if(server.length() == 0)
			return;
			// empty or invalid port numer, ignore it
			String portNumber = tfPort.getText().trim();
			if(portNumber.length() == 0)
			return;
			int port = 0;
			try {
				port = Integer.parseInt(portNumber);
			}
			catch(Exception en) {
				return;   // nothing I can do if port number is not valid
			}

			// try creating a new Client with GUI
			client = new Client(server, port, username, this);
			// test if we can start the Client
			if(!client.start())
			return;
			tf.setText("");
			label.setText("Enter your message below");
			connected = true;

			// disable login button
			login.setEnabled(false);
			// enable the 2 buttons
			logout.setEnabled(true);
			whoIsIn.setEnabled(true);
			// disable the Server and Port JTextField
			tfServer.setEditable(true);
			tfPort.setEditable(true);
			// Action listener for when the user enter a message
			tf.addActionListener(this);*/
		}

	}

	//Main method
	public static void main(String[] args) {
		final boolean check = login();
		if (check) {
			System.out.println("OK");
			new ClientGUI("localhost", 1500);
		} else {
			System.out.println("Fail");
		}
	}

	//Login Frame
	private static boolean login() {
		try {
			// JFrame frame = new JFrame();
			// frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			// frame.setLocationRelativeTo( null );
			// frame.setVisible( true );
			// frame.setSize(500, 400);

			//Login Panel
			JPanel loginPanel = new JPanel(new GridLayout(4,2));

			JLabel userLabel = new JLabel("Username\t");
			JTextField userInput = new JTextField(15);
			JLabel pwLabel = new JLabel("Password\t");
			JPasswordField pwInput = new JPasswordField(15);
			JLabel addrLabel = new JLabel("Server Address\t");
			JTextField addrInput = new JTextField(15);
			JLabel portLabel = new JLabel("Server Port\t");
			JTextField portInput = new JTextField(15);
			loginPanel.add(userLabel);
			loginPanel.add(userInput);
			loginPanel.add(pwLabel);
			loginPanel.add(pwInput);
			loginPanel.add(addrLabel);
			loginPanel.add(addrInput);
			loginPanel.add(portLabel);
			loginPanel.add(portInput);

			int result = JOptionPane.showConfirmDialog(null, loginPanel, "PPAP Secure Chat", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

			if (result == JOptionPane.YES_OPTION) {
				System.out.println("[DEBUG] " + userInput.getText() + " : " + new String(pwInput.getPassword()));
				return true;
			} else {
				return false;
			}
		} catch (NullPointerException e) {
			return false;
		}
	}
}
