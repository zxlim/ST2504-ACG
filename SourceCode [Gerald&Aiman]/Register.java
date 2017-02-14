
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
  private String serverAddress;
	private int portNumber;

 String newUsername;
 String newPassword;

  Register(String server, int port) {
		// which calls the common constructor with the GUI set to null
		this.serverAddress = server;
    this.portNumber = port;
	}

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
				System.out.println("Error! Incorrect arguments.\n > Usage is: > java Register [serverAddress]\n > Default serverAddress = localhost");
			return;
		}


  // create the Client object
  Register client = new Register(serverAddress, portNumber);
  // test if we can start the connection to the Server
  // if it failed nothing we can do
  if(!client.start())
    return;

  // wait for messages from user
  Scanner scan = new Scanner(System.in);
  // loop forever for message from the user

    System.out.print(" This program is used to REGISTER new users ONLY.\n Please exit this program if you are an existing user or already have an account.\n Enter LOGOUT to exit.\n\n");
    //prompting for their new username
    System.out.print("Enter your new username > ");
    // read message from user
    String msg = scan.nextLine();
    // logout if message is LOGOUT
    if(msg.equalsIgnoreCase("LOGOUT")) {
      client.sendMessage(new ChatMessage(ChatMessage.LOGOUT, ""));
    }
    else {
      //client.sendMessage(new ChatMessage(ChatMessage.MESSAGE, msg));
      newUsername = msg;
    }
    //prompting for their new password
    System.out.print("Enter your new password > ");
    // read message from user
    msg = scan.nextLine();
    // logout if message is LOGOUT
    if(msg.equalsIgnoreCase("LOGOUT")) {
      client.sendMessage(new ChatMessage(ChatMessage.LOGOUT, ""));
    }
    else {
      newPassword = msg;
    }
    //prompting for their new password AGAIN
    System.out.print("Enter your new password again > ");
    // read message from user
    msg = scan.nextLine();
    // logout if message is LOGOUT
    if(msg.equalsIgnoreCase("LOGOUT")) {
      client.sendMessage(new ChatMessage(ChatMessage.LOGOUT, ""));
    }
    else if (msg.equals(newPassword)) {
      System.out.print("Registration complete!\n Your account details are as follows:\n Username: " + newUsername + "\n Password: " + newPassword);
      System.out.print("Program will now exit.");
    }
    else {
      System.out.print("Error!\n > Your passwords do not match!\n Please try again!");
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
			socket = new Socket(serverAddress, portNumber);
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

  void sendMessage(ChatMessage msg) {
		try {
			sOutput.writeObject(msg);
		}
		catch(IOException e) {
			display("Exception writing to server: " + e);
		}
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
