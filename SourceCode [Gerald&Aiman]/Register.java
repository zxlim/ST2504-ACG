
import java.net.*;
import java.io.*;
import java.util.*;

/*
**	[ST2504 Applied Cryptography Assignment]
**	[Encrypted Chat Program]
**
**	Aeron Teo (P1500725)
**	Aiman Abdul Rashid (P1529335)
**	Gerald Peh (P1445972)
**	Lim Zhao Xiang (P1529559)
*/

public class Register {
  // for I/O
  private ObjectInputStream sInput;		// to read from the socket
  private ObjectOutputStream sOutput;		// to write on the socket
  private Socket socket;

	// the server, the port and the username
  private String server;
	private int port;

  public static void main(String[] args) {
    // default values
		int portNumber = 1449;
		String serverAddress = "localhost";

		// depending of the number of arguments provided we fall through
		switch(args.length) {
			// > javac Client serverAddress
			case 1:
				serverAddress = args[0];
			// > java Client
			case 0:
				break;
			// invalid number of arguments
			default:
				System.out.println("Usage is: > java Register [serverAddress]\nDefault serverAddress = localhost");
			return;
		}
}

  // create the Client object
  Client client = new Client(serverAddress, portNumber);
  // test if we can start the connection to the Server
  // if it failed nothing we can do
  if(!client.start())
    return;

  // wait for messages from user
  Scanner scan = new Scanner(System.in);
  // loop forever for message from the user
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
	 * To start the dialog
	 */
	public boolean start() {
		// try to connect to the server
		try {
			socket = new Socket(server, port);
		}
		// if it failed not much I can so
		catch(Exception ec) {
			display("Error connecting to server:" + ec);
			return false;
		}

		String msg = "Connection accepted " + socket.getInetAddress() + ":" + socket.getPort();
		display(msg);

		/* Creating both Data Stream */
		try
		{
			sInput  = new ObjectInputStream(socket.getInputStream());
			sOutput = new ObjectOutputStream(socket.getOutputStream());
		}
		catch (IOException eIO) {
			display("Exception creating new Input/output Streams: " + eIO);
			return false;
		}

		// success we inform the caller that it worked
		return true;
	}

  /*
	 * To send a message to the console or the GUI
	 */
	private void display(String msg) {
			System.out.println(msg);      // println in console mode
	}

  private void disconnect() {
		try {
			if(sInput != null) sInput.close();
		}
		catch(Exception e) {} // not much else I can do
		try {
			if(sOutput != null) sOutput.close();
		}
		catch(Exception e) {} // not much else I can do
        try{
			if(socket != null) socket.close();
		}
		catch(Exception e) {} // not much else I can do


	}

}
