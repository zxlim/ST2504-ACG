
import java.net.*;
import java.io.*;
import java.util.*;

/*
* The Client that can be run both as a console or a GUI
*/
public class Client  {

	//Socket and I/O Stream
	private Socket socket;
	private ObjectInputStream sInput;
	private ObjectOutputStream sOutput;

	//GUI instance
	private ClientGUI cg;

	//Username, Server and Port
	private String username, server;
	private int port;

	//Constructor for Command-line
	Client(String server, int port) {
		this(server, port, null);
	}

	//Constructor for GUI
	Client(String server, int port, ClientGUI cg) {
		this.server = server;
		this.port = port;
		//this.username = username;
		// save if we are in GUI mode or not
		this.cg = cg;
	}

	//Main method
	public static void main(String[] args) {
		//Initialize settings
		String userName = "";
		String serverAddress = "";
		int portNumber = -1;

		if (args.length != 2) {
			System.out.println("Usage: java Client [IP Address] [Port]");
		} else {
			serverAddress = args[0];
			try {
				portNumber = Integer.parseInt(args[1]);
			} catch(Exception e) {
				System.out.println("Invalid port number.");
				System.out.println("Usage: java Client [IP Address] [Port]");
				return;
			}

			if (portNumber < 0 || portNumber > 65535) {
				System.out.println("Invalid port number.");
				System.out.println("Usage: java Client [IP Address] [Port]");
				return;
			}
		}

		//Create a instance of Client
		Client client = new Client(serverAddress, portNumber);

		//Connect and login Client instance to Server
		if(!client.start()){
			return;
		}

		//Initialize Scanner for command-line chat
		Scanner scan = new Scanner(System.in);

		//Loop forever until break
		while(true) {
			System.out.print("Message: ");
			String msg = scan.nextLine();

			//Disconnect Client if message is 'disconnect'
			if(msg.equalsIgnoreCase("DISCONNECT")) {
				client.sendMessage(new ChatMessage(ChatMessage.LOGOUT, ""));
				//Break out of the loop to disconnect
				break;
			} else if(msg.equalsIgnoreCase("WHOISIN")) {
				//Display list of connected users if message is 'whoisin'
				client.sendMessage(new ChatMessage(ChatMessage.WHOISIN, ""));
			} else {
				//Send message to server for broadcast
				client.sendMessage(new ChatMessage(ChatMessage.MESSAGE, msg));
			}
		}

		//Disconnect Client instance
		client.disconnect();
	}

	//Start Client instance (Called by main method)
	//Returns boolean (True/ False) depending on whether connection to Server is successful
	public boolean start() {

		//Attempt to connect to server
		try {
			socket = new Socket(server, port);
		} catch (Exception e) {
			display("Error connectiong to server: " + e);
			return false;
		}

		String msg = "Connection accepted " + socket.getInetAddress() + ":" + socket.getPort();
		display(msg);

		//Create I/O Stream
		try {
			sInput  = new ObjectInputStream(socket.getInputStream());
			sOutput = new ObjectOutputStream(socket.getOutputStream());
		} catch (IOException eIO) {
			display("Exception creating new Input/output Streams: " + eIO);
			return false;
		}

		//Create and start a new thread to listen from Server
		new ListenFromServer().start();

		//Attempt to login to Server
		try {

			//sOutput.writeObject(username);
		} catch (IOException e) {
			display("Exception doing login: " + e);
			disconnect();
			return false;
		}

		//Successfully connect to Server
		return true;
	}

	//Print messages to either command-line or GUI
	private void display(String msg) {
		if(cg == null) {
			//Command-line Mode
			System.out.println(msg);
		} else {
			//GUI Mode
			cg.append(msg + "\n");
		}
	}

	//Send message to Server
	void sendMessage(ChatMessage msg) {
		try {
			sOutput.writeObject(msg);
		}
		catch(IOException e) {
			display("Exception writing to server: " + e);
		}
	}

	//Disconnect Client instance
	private void disconnect() {
		try {
			if(sInput != null) sInput.close();
		} catch(Exception e) {
			display("Exception closing input stream: " + e);
		}
		try {
			if(sOutput != null) sOutput.close();
		} catch(Exception e) {
			display("Exception closing ouput stream: " + e);
		}
		try {
			if(socket != null) {
				socket.close();
			}
		} catch(Exception e) {
			display("Exception closing socket: " + e);
		}
		if(cg != null) {
			//If GUI exist, pass disconnect info to GUI
			cg.connectionFailed();
		}
	}

	class ListenFromServer extends Thread {

		public void run() {

			while(true) {
				try {
					String msg = (String) sInput.readObject();

					if(cg == null) {
						//Command-line Mode
						System.out.println(msg);
						System.out.print("> ");
					} else {
						//GUI Mode
						cg.append(msg);
					}
				} catch(IOException e) {
					display("Server has close the connection: " + e);
					if(cg != null)
					cg.connectionFailed();
					break;
				} catch (ClassNotFoundException e2) {
					//Catch for the sake of catching; Exception wouldn't happen
				}
			}
		}
	}
}
