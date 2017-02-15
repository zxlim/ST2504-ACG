
import java.net.*;
import java.io.*;
import java.util.*;

public class Client  {

	//Socket and I/O Stream
	private Socket socket;
	private ObjectInputStream sInput;
	private ObjectOutputStream sOutput;

	//Initialize Scanner instance
	private static final Scanner scan = new Scanner(System.in);

	//GUI
	private ClientGUI cg;

	//Server Address and Port
	private String server;
	private int port;

	//Encryption/ Handshake variables
	private static PKI clientRSA;
	private static PKI clientECDSA;
	//Temporary keystore - Client will have its own keystore soon
	private static final PKI serverRSA = Crypto.ksPublicKey("ServerKeyStore", "serverRSA");
	//private static final PKI serverECDSA = Crypto.ksPublicKey("Client", "serverECDSA");
	private static byte[] sessionKey;

	//Constructor
	Client(String server, int port, ClientGUI cg) {
		this.server = server;
		this.port = port;
		this.cg = cg;
	}

	//Attempt to connect to Server
	public boolean start() {

		try {
			socket = new Socket(server, port);
		} catch (Exception e) {
			display("Error connectiong to server: " + e);
			return false;
		}

		String msg = "[DEBUG] Connection accepted " + socket.getInetAddress() + ":" + socket.getPort();
		display(msg);

		//Create I/O Stream
		try {
			sInput  = new ObjectInputStream(socket.getInputStream());
			sOutput = new ObjectOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			display("Exception creating new Input/output Streams: " + e);
			return false;
		}

		//Get user credentials
		Credentials userAccount = login();

		if (userAccount == null) {
			return false;
		}

		//Send account credentials
		//sOutput.writeObject(userAccount);
		//Receive reply from server
		//final boolean loginSuccess = (boolean) sInput.readObject();

		//Encrypt connection between Client and Server
		try {
			if (!encryptConnection()) {
				sOutput.writeObject("Unsecure"); //Temporary only
				sendMessage(new ChatMessage(ChatMessage.LOGOUT, ""));
				disconnect();
				if(cg != null) {
					cg.connectionFailed();
				}
				display("Connection to server has been terminated.\n");
				return false;
			}
		} catch (Exception e) {
			display("[Error] Exception while establishing secure connection:\n" + e);
			return false;
		}

		//Create and start a Thread to listen from the server
		new ListenFromServer().start();

		//Connection successful
		return true;
	}

	//Login
	private Credentials login() {
		if (cg == null) {
			//Console mode
			//User input credentials
			System.out.println("Please login to continue.\n");
			System.out.print("Username: ");
			final String userInput = scan.nextLine();
			final String userName = userInput.trim(); //Remove any leading or trailing whitespace

			final String password = new String(System.console().readPassword("Password: "));

			System.out.println("[DEBUG] Password is: " + password);

			if (userName == null || userName.isEmpty()) {
				System.out.println("Username cannot be left empty.");
				return null;
			}

			final byte[] encUsername = Crypto.encrypt_RSA(Crypto.strToBytes(userName), serverRSA.getPublic());
			final byte[] encPassword = Crypto.encrypt_RSA(Crypto.strToBytes(password), serverRSA.getPublic());

			//Generate new RSA (For encryption) and ECDSA (For digital signature) Keypairs
			clientRSA = Crypto.generate_RSA();
			clientECDSA = Crypto.generate_ECDSA();

			final byte[] encRSA = Crypto.encrypt_RSA(clientRSA.getPubBytes(), serverRSA.getPublic());
			final byte[] encECDSA = Crypto.encrypt_RSA(clientECDSA.getPubBytes(), serverRSA.getPublic());

			return new Credentials(encUsername, encPassword, encRSA, encECDSA);
		} else {
			//Temporary only
			display("GUI Login not yet supported. Please use 'java Client' instead of 'java ClientGUI'");
			return null;
		}
	}

	private boolean encryptConnection() {
		try {
			display("Securing connection with server...");
			//To be completed. Client-Server encryption process and handshake in here.
			return false;
		} catch (Exception e) {
			display("[Error] Exception while establishing secure connection:\n" + e);
			return false;
		}
	}

	//Print string
	private void display(String msg) {
		if(cg == null) {
			//Console
			System.out.println(msg);
		} else {
			//GUI
			cg.append(msg + "\n");
		}
	}

	//Send message to Server
	void sendMessage(ChatMessage msg) {
		try {
			sOutput.writeObject(msg);
		} catch(IOException e) {
			display("Exception writing to server: " + e);
		}
	}

	//Close I/O Streams and Socket to disconnect Client
	private void disconnect() {
		try {
			if(sInput != null) sInput.close();
		} catch(Exception e) {
			//
		}
		try {
			if(sOutput != null) sOutput.close();
		} catch(Exception e) {
			//
		}
		try{
			if(socket != null) socket.close();
		} catch(Exception e) {
			//
		}

		//Tell GUI connection is closed
		if(cg != null) {
			cg.connectionFailed();
		}
	}

	//Main method
	public static void main(String[] args) {
		int portNumber = -1;

		if (args.length != 0) {
			//Remind user there is no need for arguments
			System.out.println("Usage: java Client\nNo arguments are required\n");
		}

		//Get Server IP Address
		System.out.print("Server IP Address: ");
		final String serverAddress = scan.nextLine();
		//Get Server Port
		System.out.print("Server Port: ");
		final String portStr = scan.nextLine();

		try {
			//Parse port typed by user as integer
			portNumber = Integer.parseInt(portStr);

			//Verify port range
			if (portNumber < 0 || portNumber > 65535) {
				System.out.println("Invalid port number.\nPlease enter a port number between 0 and 65535.");
				return;
			}
		}
		catch(Exception e) {
			//If entered value is not integer
			System.out.println("Invalid port number.\nPlease enter a port number between 0 and 65535.");
			return;
		}

		//Create a new Client instance
		Client client = new Client(serverAddress, portNumber, null);


		if(!client.start()) {
			return;
		}

		//Loop forever
		while(true) {
			System.out.print("> ");
			// read message from user
			String msg = scan.nextLine();
			// logout if message is LOGOUT
			if(msg.equalsIgnoreCase("LOGOUT")) {
				client.sendMessage(new ChatMessage(ChatMessage.LOGOUT, ""));
				// break to do the disconnect
				break;
			}
			// message WhoIsIn
			else if(msg.equalsIgnoreCase("WHOISIN")) {
				client.sendMessage(new ChatMessage(ChatMessage.WHOISIN, ""));
			}
			else {				// default to ordinary message
				client.sendMessage(new ChatMessage(ChatMessage.MESSAGE, msg));
			}
		}
		// done disconnect
		client.disconnect();
	}

	/*
	* a class that waits for the message from the server and append them to the JTextArea
	* if we have a GUI or simply System.out.println() it in console mode
	*/
	class ListenFromServer extends Thread {

		public void run() {

			while (true) {
				try {
					String msg = (String) sInput.readObject();
					// if console mode print the message and add back the prompt
					if(cg == null) {
						System.out.println(msg);
						System.out.print("> ");
					}
					else {
						cg.append(msg);
					}
				} catch (IOException e) {
					display("Server has close the connection: " + e);
					if (cg != null) {
						cg.connectionFailed();
					}
					break;
				} catch(ClassNotFoundException e2) {
					// can't happen with a String object but need the catch anyhow
				}
			}

			return;
		}
	}
}
