/*
**	[ST2504 Applied Cryptography Assignment]
**	[Encrypted Chat Program]
**
**	Aeron Teo (P1500725)
**	Aiman Abdul Rashid (P1529335)
**	Gerald Peh (P1445972)
**	Lim Zhao Xiang (P1529559)
*/

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class Server {
	//Client ID
	private static int clientID;
	//ArrayList to keep the list of connected Clients
	private ArrayList<ClientThread> clientList;

	//Server instance variables
	private ServerGUI sg;
	private SimpleDateFormat sdf;
	private int port;

	private boolean keepGoing;

	//Encryption and Handshake variables
	private static PKI serverRSA = Crypto.ksPrivateKey("Server.keystore", "serverRSA");
	private static PKI serverECDSA = Crypto.ksPrivateKey("Server.keystore", "serverECDSA");

	//Constructor
	public Server(final int port, final ServerGUI sg) {
		this.port = port;
		this.sg = sg;

		sdf = new SimpleDateFormat("HH:mm");
		clientList = new ArrayList<ClientThread>();
	}

	public void start() {
		keepGoing = true;
		display("Starting PPAP Secure Chat Server...");

		try {
			ServerSocket serverSocket = new ServerSocket(port);

			//Listen for client connections on socket
			while(keepGoing) {
				display("Server listening on port " + port + ".");

				Socket socket = serverSocket.accept();

				if (!keepGoing) {
					break;
				}

				ClientThread clientThread = new ClientThread(socket);
				clientList.add(clientThread);
				clientThread.start();
			}

			//Stop the server
			try {
				serverSocket.close();

				//Purge session key and server keypairs on server stop
				serverRSA = null;
				serverECDSA = null;

				for(int i = 0; i < clientList.size(); ++i) {
					final ClientThread clientThread = clientList.get(i);
					try {
						clientThread.sInput.close();
						clientThread.sOutput.close();
						clientThread.socket.close();
					} catch (IOException e) {
						//ggwp ppap
					}
				}
			} catch (Exception e) {
				display("[Error] Exception occured when terminating server.");
			}
		} catch (IOException e) {
			String msg = sdf.format(new Date()) + " Exception on new ServerSocket: " + e + "\n";
			display(msg);
		}
	} //start

	//Stop the server (GUI mode)
	protected void stop() {
		//Purge session key and server keypairs on server stop
		serverRSA = null;
		serverECDSA = null;

		keepGoing = false;
		try {
			new Socket("localhost", port);
		} catch(Exception e) {
			//ggwp ppap
		}
	} //stop

	//Print string
	private void display(final String msg) {
		String time = sdf.format(new Date()) + " " + msg;
		if (sg == null) {
			System.out.println(time);
		} else {
			sg.appendEvent(time + "\n");
		}
	} //display

	//Broadcast message to everyone
	private synchronized void broadcast(final String message) {

		//Add timestamp to message
		String time = sdf.format(new Date());
		// this is a plaintext.
		String messageLf = time + " " + message + "\n";


		if (sg == null) {
			//Console mode
			System.out.print(messageLf);
		} else {
			//GUI mode
			sg.appendRoom(messageLf);
		}

		for (int i = clientList.size(); --i >= 0;) {
			ClientThread clientThread = clientList.get(i);

			final byte[] clientSessionKey = clientThread.sessionKey;
			//encrypt here.
			byte[] signat = Crypto.sign_ECDSA(Crypto.strToBytes(messageLf),serverECDSA.getPrivate());
			Message messageLfM = new Message(Message.MESSAGE,Crypto.encrypt_AES(Crypto.strToBytes(messageLf),clientSessionKey),signat);

			if (!clientThread.writeMsgObject(messageLfM)) {
				clientList.remove(i);
				display("Disconnected Client " + clientThread.username + " removed from list.");
			}
		}
	} //broadcast

	private void whisper(final String[] msg) {

		for (int i = clientList.size(); --i >= 0;) {
			final ClientThread clientThread = clientList.get(i);

			if (clientThread.username.equals(msg[0])) {
				//Add timestamp to message
				final String time = sdf.format(new Date());
				final String decodedMessage = Crypto.base64ToStr(msg[1]);
				final String message = time + " " + decodedMessage + "\n";

				final byte[] clientSessionKey = clientThread.sessionKey;

				final byte[] signature = Crypto.sign_ECDSA(Crypto.strToBytes(message),serverECDSA.getPrivate());
				final Message whisperMsg = new Message(Message.WHISPER, Crypto.encrypt_AES(Crypto.strToBytes(message), clientSessionKey), signature);

				if (!clientThread.writeMsgObject(whisperMsg)) {
					clientList.remove(i);
					display("[Error] Failed to send a whisper message to " + clientThread.username + ".");
					display("Disconnected Client " + clientThread.username + " removed from list.");
				} else {
					display("A whisper message has been sent successfully.");
				}
				break;
			}
		}
	} //whisper

	//Client logout
	synchronized void remove(final int id) {
		//Go through ArrayList until ID is found
		for (int i = 0; i < clientList.size(); ++i) {
			ClientThread clientThread = clientList.get(i);

			if (clientThread.id == id) {
				clientList.remove(i);
				return;
			}
		}
	} //remove

	//Main method
	public static void main(String[] args) {
		int portNumber = -1;

		if (args.length != 0) {
			//Remind user there is no need for arguments
			System.out.println("Usage: java Server\nNo arguments are required\n");
		}

		//Get Server Port
		final Scanner scan = new Scanner(System.in);
		System.out.print("Server Port: ");
		final String portStr = scan.nextLine();
		scan.close();

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

		//Create a new Server instance (Console mode)
		Server server = new Server(portNumber, null);

		//Start the Server instance
		server.start();
	} //main

	//Client thread for every client instance
	class ClientThread extends Thread {
		//Socket and I/O Stream
		Socket socket;
		ObjectInputStream sInput;
		ObjectOutputStream sOutput;

		int id;
		String username;
		Message m;
		String date;

		byte[] sessionKey;
		PKI clientRSA;
		PKI clientECDSA;

		//Constructor
		ClientThread(final Socket socket) {
			id = ++clientID;
			this.socket = socket;

			display("A client is attempting to connect to the chat server.");

			try {
				//Create I/O Stream
				sOutput = new ObjectOutputStream(socket.getOutputStream());
				sInput  = new ObjectInputStream(socket.getInputStream());
			} catch (IOException e) {
				display("Exception creating new Input/output Streams: " + e);
				return;
			}
			date = (new Date().toString()) + "\n";
		}

		//Client Authentication
		private boolean clientLogin() {
			try {
				//Receive Credentials Object from Client instance
				final Credentials encrypted = (Credentials) sInput.readObject();

				final String clientName = Crypto.bytesToStr(Crypto.decrypt_RSA(encrypted.getUsername(), serverRSA.getPrivate()));
				final String clientPass = Crypto.bytesToStr(Crypto.decrypt_RSA(encrypted.getPassword(), serverRSA.getPrivate()));

				//Authenticate user credentials
				final InputStreamReader file = new InputStreamReader(new FileInputStream("passwd"), "UTF-8");
				final BufferedReader read = new BufferedReader(file);
				String line;

				while ((line = read.readLine()) != null) {
					final String[] passwd = line.split(":");

					final String passwdName = passwd[0];

					if (clientName.equals(passwdName)) {

						final String passwdPW = passwd[1];
						final byte[] passwdSalt = Crypto.base64ToBytes(passwd[2]);

						final String pwHash = Crypto.bytesToBase64(Crypto.pbkdf2(clientPass, passwdSalt));

						if (pwHash.equals(passwdPW)) {
							line = null;
							username = passwdName;
							clientRSA = Crypto.bytesToPublicRSA(Crypto.decrypt_RSA(encrypted.getRsaPub(),serverRSA.getPrivate()));
							clientECDSA = Crypto.bytesToPublicECDSA(Crypto.decrypt_RSA(encrypted.getEcdsaPub(),serverRSA.getPrivate()));

							final byte[] signature = Crypto.sign_ECDSA(Crypto.strToBytes("True"), serverECDSA.getPrivate());
							sOutput.writeObject(new Message(Message.LOGIN, Crypto.strToBytes("True"), signature));

							display(username + " has connected to the chat server.");

							file.close();
							return true;
						} else {
							line = null;

							final byte[] signature = Crypto.sign_ECDSA(Crypto.strToBytes("False"), serverECDSA.getPrivate());
							sOutput.writeObject(new Message(Message.LOGIN, Crypto.strToBytes("False"), signature));

							display(clientName + " failed to authenticate.\n");

							file.close();
							return false;
						}
					} //If
				} //While loop

				file.close();
				return false;
			} catch (Exception e) {
				display("[Error] Exception authenticating user: " + e);
				return false;
			}
		} //nameAuthentication

		public void run() {
			boolean keepGoing = true;

			if (!clientLogin()) {
				keepGoing = false;
				remove(id);
				close();
				return;
			}

			if (!encryptConnection()) {
				display("[Error] Failed to secure connection with " + username + ".");
				display("[Error] Terminated connection with client " + username + ".\n");
				keepGoing = false;
			}

			while(keepGoing) {
				try {
					m = (Message) sInput.readObject();
				} catch (IOException e) {
					display("[Error] " + username + " Exception reading Streams: " + e);
					break;
				} catch(ClassNotFoundException e2) {
					break;
				}


				switch(m.getType()) {
					case Message.MESSAGE:
					final byte[] plaintext = Crypto.decrypt_AES(m.getEncrypted(), sessionKey);
					final boolean verifySig = Crypto.verify_ECDSA(plaintext, clientECDSA.getPublic(), m.getSignature());

					if (verifySig) {
						broadcast("[" + username + "] " + Crypto.bytesToStr(plaintext));
					} else {
						display("[Error] Verification of " + username + "\'s digital Signature failed.\n");
					}
					break;
					case Message.WHISPER:
					display("Whisper message received. Forwarding...");
					final String whisperDecrypted = Crypto.bytesToStr(Crypto.decrypt_AES(m.getEncrypted(), sessionKey));
					final boolean verifySigature = Crypto.verify_ECDSA(Crypto.strToBytes(whisperDecrypted), clientECDSA.getPublic(), m.getSignature());

					if (verifySigature) {
						whisper(whisperDecrypted.split(":"));
					} else {
						display("[Error] Verification of " + username + "\'s digital Signature failed.\n");
					}
					break;
					case Message.WHOISIN:
					String listUsers = "List of the users connected at " + sdf.format(new Date()) + "\n";
					for (int i = 0; i < clientList.size(); ++i) {
						ClientThread clientThread = clientList.get(i);
						listUsers += "[" + (i+1) + "] " + clientThread.username + " since " + clientThread.date;
					}
					writeMsg(Message.WHOISIN, listUsers);
					break;
					case Message.LOGOUT:
					display(username + " logged out of the chat server.");
					keepGoing = false;
					break;
					case Message.SECURITYLOGOUT:
					display("[Error]" + username + " disconnected due to security reasons.");
					keepGoing = false;
					break;
				} //Switch
			} //While loop

			remove(id);
			close();
			clientRSA = null;
			clientECDSA = null;
			sessionKey = null;
			broadcast(username + " has logged out from the chat server.");
		} //run

		//Close sockets and I/O Stream
		private void close() {
			try {
				if(sOutput != null) {
					sOutput.close();
				}
			} catch(Exception e) {
				//ggwp ppap
			}
			try {
				if(sInput != null) {
					sInput.close();
				}
			} catch(Exception e) {
				//ggwp ppap
			};
			try {
				if(socket != null) {
					socket.close();
				}
			} catch (Exception e) {
				//ggwp ppap
			}
		} //close

		//Send a message to client
		private boolean writeMsg(final int type, final String msg) {
			if (!socket.isConnected()) {
				close();
				return false;
			}

			final byte[] signature = Crypto.sign_ECDSA(Crypto.strToBytes(msg),serverECDSA.getPrivate());
			final Message send = new Message(type, Crypto.encrypt_AES(Crypto.strToBytes(msg), sessionKey), signature);

			try {
				sOutput.writeObject(send);
			} catch (IOException e) {
				display("[Error] Exception sending message to " + username);
				display(e.toString());
			}
			return true;
		} //writeMsg

		//To write a message using Message object
		private boolean writeMsgObject(final Message msg) {
			if (!socket.isConnected()) {
				close();
				return false;
			}

			try {
				sOutput.writeObject(msg);
			} catch (IOException e) {
				display("[Error] Exception sending message to " + username);
				display(e.toString());
			}
			return true;
		} //writeMsgObject

		private boolean encryptConnection() {
			try {

				display("Securing connection with " + username + "...");
				/*Time for handshake*/
				Message session = (Message) sInput.readObject();
				//System.out.println("Handshake started");
				sessionKey = Crypto.decrypt_RSA(session.getMessage(),serverRSA.getPrivate());
				final boolean verifySession = Crypto.verify_ECDSA(sessionKey,clientECDSA.getPublic(),session.getSignature());

				/*sending a message*/
				if (verifySession) {
					//send aes
					display("Session key received and verified.");
					display("Exchanging handshake...");
					AES encryptedAES = Crypto.encrypt_AES(Crypto.strToBytes("PPAP Secured"),sessionKey);
					//sign original
					byte[] sig = Crypto.sign_ECDSA(Crypto.strToBytes("PPAP Secured"),serverECDSA.getPrivate());
					Message checkVerified = new Message(Message.HANDSHAKE,encryptedAES,sig);
					sOutput.writeObject(checkVerified);

					Message sessionRound2 = (Message) sInput.readObject();
					byte[] decryptMsg = Crypto.decrypt_AES(sessionRound2.getEncrypted(),sessionKey);

					if (Crypto.bytesToStr(decryptMsg).equals("ppapClient-OK!")){
						AES encryptedAES2 = Crypto.encrypt_AES(Crypto.strToBytes("HANDSHAKE_Established"),sessionKey);
						byte[] sig2 = Crypto.sign_ECDSA(Crypto.strToBytes("HANDSHAKE_Established"),serverECDSA.getPrivate());
						Message checkVerified2 = new Message(Message.HANDSHAKE, encryptedAES2,sig2);
						sOutput.writeObject(checkVerified2);
						display("Connection with " + username + " secured with AES-" + (sessionKey.length * 8) + ".\n");
						return true;
					} else {
						return false;
					}
				} else {
					return false;
				}
			} catch (Exception e) {
				display("[Error] Failed to establish a secure connection.\n");
				return false;
			}
		} //encryptConnection();
	} //Subclass ClientThread
} //class
