
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
	private static final PKI serverECDSA = Crypto.ksPublicKey("ServerKeyStore", "serverECDSA");
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
			display("Failed to connect to server. Server might be down.");
			return false;
		}

		display("Connecting to server at " + socket.getInetAddress() + ":" + socket.getPort() + "...\n");

		//Create I/O Stream
		try {
			sInput  = new ObjectInputStream(socket.getInputStream());
			sOutput = new ObjectOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			display("[Error] Exception creating new Input/output Streams: " + e);
			return false;
		}

		//Get user credentials
		Credentials userAccount = login();

		if (userAccount == null) {
			return false;
		} else {
			try {
				//Send account credentials
				sOutput.writeObject(userAccount);
			} catch (Exception e) {
				display("Unable to login to server. Please try again.");
				return false;
			}
		}

		//Receive reply from server
		try {
			final Message loginStatus = (Message) sInput.readObject();
			final boolean verifySig = Crypto.verify_ECDSA(loginStatus.getMessage(), serverECDSA.getPublic(), loginStatus.getSignature());

			if (!verifySig) {
				display("Invalid username or password. Please try again.");
				disconnect();
				if(cg != null) {
					cg.connectionFailed();
				}
				return false;
			}
		} catch (Exception e) {
			display("Unable to login to server. Please try again.");
			return false;
		}

		//Encrypt connection between Client and Server, handled by encryptConnection method
		try {
			if (!encryptConnection()) {
				sendMessage(new Message(Message.LOGOUT));
				disconnect();
				if(cg != null) {
					cg.connectionFailed();
				}
				display("Connection to server has been terminated due to security reasons.\n");
				return false;
			}
		} catch (Exception e) {
			display("[Error] Failed to establish a secure connection.");
			return false;
		}

		//Create and start a Thread to listen from the server
		new ListenFromServer().start();

		//Connection successful
		return true;
	} //start

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

			if (userName == null || userName.isEmpty()) {
				System.out.println("Username cannot be left empty.");
				return null;
			}

			System.out.println("Logging in...");

			final byte[] encUsername = Crypto.encrypt_RSA(Crypto.strToBytes(userName), serverRSA.getPublic());
			final byte[] encPassword = Crypto.encrypt_RSA(Crypto.strToBytes(password), serverRSA.getPublic());

			//Generate new RSA (For encryption) and ECDSA (For digital signature) Keypairs
			clientRSA = Crypto.generate_RSA();
			clientECDSA = Crypto.generate_ECDSA();

			final byte[] encRSA = Crypto.encrypt_RSA(clientRSA.getPubBytes(), serverRSA.getPublic());
			final byte[] encECDSA = Crypto.encrypt_RSA(clientECDSA.getPubBytes(), serverRSA.getPublic());

			return new Credentials(encUsername, encPassword, encRSA, encECDSA);
		} else {
			//GUI mode
			//Temporary only
			display("GUI Login not yet supported. Please use 'java Client' instead of 'java ClientGUI'");
			return null;
		}
	} //login

	//Exchange session encryption information and encrypt session
	private boolean encryptConnection() {
		try {
			display("Securing connection with server...");
			//To be completed. Client-Server encryption process and handshake in here.
			return false;
		} catch (Exception e) {
			display("[Error] Failed to establish a secure connection.");
			return false;
		}
	} //encryptConnection

	//Print string
	private void display(String msg) {
		if(cg == null) {
			//Console mode
			System.out.println(msg);
		} else {
			//GUI mode
			cg.append(msg + "\n");
		}
	} //display

	//Send message to Server (Console mode)
	void sendMessage(final Message msg) {
		try {
			sOutput.writeObject(msg);
		} catch (IOException e) {
			display("Exception writing to server: " + e);
		}
	} //sendMessage

	//Send message to Server (GUI mode)
	void sendMessageGUI(final String msg) {

		final byte[] plaintext = Crypto.strToBytes(msg);
		final AES ciphertext = Crypto.encrypt_AES(plaintext, sessionKey);
		final byte[] signature = Crypto.sign_ECDSA(plaintext, clientECDSA.getPrivate());

		try {
			sOutput.writeObject(new Message(Message.MESSAGE, ciphertext, signature));
		} catch(IOException e) {
			display("Exception writing to server: " + e);
		}
	} //sendMessageGUI

	//Close I/O Streams and Socket to disconnect Client
	private void disconnect() {
		try {
			if (sInput != null) sInput.close();
		} catch (Exception e) {
			//ggwp
		}
		try {
			if (sOutput != null) sOutput.close();
		} catch (Exception e) {
			//ggwp
		}
		try {
			if (socket != null) socket.close();
		} catch (Exception e) {
			//ggwp
		}

		//Tell GUI client has disconnected
		if (cg != null) {
			cg.connectionFailed();
		}
	} //disconnect

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
		}
		catch(NumberFormatException e) {
			//If entered value is not integer, return and exit
			System.out.println("Invalid port number.\nPlease enter a port number between 0 and 65535.");
			return;
		}

		//Verify port range
		if (portNumber < 0 || portNumber > 65535) {
			System.out.println("Invalid port number.\nPlease enter a port number between 0 and 65535.");
			return;
		}

		//Create a new Client instance (Console mode)
		Client client = new Client(serverAddress, portNumber, null);


		if(!client.start() || sessionKey == null || sessionKey.length == 0) {
			//If Client instance fails to start or session key does not exist, return and exit
			return;
		}

		//Loop forever
		while(true) {
			System.out.print("> ");
			final String inputMsg = scan.nextLine();
			final String msg = inputMsg.trim();

			if (msg.equalsIgnoreCase("/HELP")) {
				//Shows the help menu when message is /LIST
				System.out.println("\n\n[Welcome to PPAP Secure Chat]");
				System.out.println("Below are a few commands to get you started.\n");
				System.out.println("/help to show this help menu.");
				System.out.println("/list to show all online users.");
				System.out.println("/whisper [user] [message] to privately message an online user.");
				System.out.println("/logout to logout from this server.");
				System.out.println("To send a message to everyone, just type and press enter.\n\n");
			} else if (msg.equalsIgnoreCase("/LIST")) {
				//Show who is in when message is /LIST
				client.sendMessage(new Message(Message.WHOISIN));
			} else if ((msg.split("\\s+"))[0].equalsIgnoreCase("/WHISPER")) {
				System.out.println("Whisper feature is under construction");
			} else if (msg.equalsIgnoreCase("/LOGOUT")) {
				//Logout if message is /LOGOUT
				client.sendMessage(new Message(Message.LOGOUT));
				break;
			} else {
				//Sends input as message to everyone
				final byte[] plaintext = Crypto.strToBytes(msg);
				final AES ciphertext = Crypto.encrypt_AES(plaintext, sessionKey);
				final byte[] signature = Crypto.sign_ECDSA(plaintext, clientECDSA.getPrivate());
				client.sendMessage(new Message(Message.MESSAGE, ciphertext, signature));
			}
		} //While loop

		//Close scanner and disconnect Client instance
		scan.close();
		client.disconnect();
	} //Main

	//Listens for messages from Server
	class ListenFromServer extends Thread {

		public void run() {
			while (true) {
				try {
					final Message m = (Message) sInput.readObject();

					final byte[] plaintext = Crypto.decrypt_AES(m.getEncrypted(), sessionKey);
					final boolean verifySig = Crypto.verify_ECDSA(plaintext, serverECDSA.getPublic(), m.getSignature());

					if (verifySig) {
						display(Crypto.bytesToStr(plaintext));

						if(cg == null) {
							//Console mode
							System.out.print("> ");
						}
					} else {
						//Signature verification of message failed
						display("[Error] A message has been received but verification has failed. It might be tampered or corrupted.");
						display("[Error] The chat server has been notified of this incident.");
					}
				} catch (IOException e) {
					display("Server has closed the connection.");
					if (cg != null) {
						cg.connectionFailed();
					}
					break;
				} catch(ClassNotFoundException e2) {
					//Can't happen with a String object, catch for the sake of catching
				}
			} //While loop
			return;
		} //run
	} //Subclass ListenFromServer
} //Class
