
import javax.swing.JOptionPane;
import java.net.*;
import java.io.*;
import java.security.Key;
//import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
/*
**	[ST2504 Applied Cryptography Assignment]
**	[Encrypted Chat Program]
**
**	Aeron Teo (P1500725)
**	Aiman Abdul Rashid (P1529335)
**	Gerald Peh (P1445972)
**	Lim Zhao Xiang (P1529559)
*/

public class newRegister {

  // Server address and port
  private String registServerAddress = "localhost";
  private int registServerPort = 1449;

  // for I/O
  //private ObjectInputStream sInput;		// to read from the socket
  //private ObjectOutputStream sOutput;		// to write on the socket
  private Socket socket;

    public static void main(String[] args){

    // For username and password loop
    boolean usernameInput = false;
    boolean passwordInput = false;

    // For user details
    String username;
    String password;

    // Encrypted user details
    byte[] usernameRSA;
    byte[] passwordRSA;



    // Start of registration
    int option =  JOptionPane.showConfirmDialog(null, "This program is used to register new users only.\nPlease exit this program if you are an existing user or already have an account.\n\t\tHit CANCEL to exit.\n\n","Create an account",JOptionPane.OK_CANCEL_OPTION);

    try {
        // Checking to continue registering new user
        if (option == JOptionPane.CANCEL_OPTION){
          System.exit(0);
        }

    // Username input loop
    do {
        username = JOptionPane.showInputDialog(null,"Please enter your new username.\nNOTE \t : \t are not allowed.");

        if (username.equals("") || username == null){
          JOptionPane.showMessageDialog(null,"You did not enter a username. Please try again.");
        } else if (username.contains(":")) {
          JOptionPane.showMessageDialog(null,"A \":\" has been detected in your username. Please try again.");
        } else {
          usernameInput = true;
        }

      } while (!usernameInput);

    // Password input loop
    do {
      password = JOptionPane.showInputDialog(null,"Please enter your new password.\nNOTE that passwords have to meet the following requirements:\n - The password should contain at least one uppercase and lowercase character\n - The password should have at least one digit (0-9)\n - The password should be at least six characters long");

    // Password complexity check
      // The password should be more than 6 characters long
      // The password should contain at least one uppercase and one lowercase character
      // The password should contain at least one digit
      if ((password.length() >= 6) &&
          (password.matches(".*[a-z]+.*")) &&
          (password.matches(".*[A-Z]+.*")) &&
          (password.matches(".*[0-9]+.*"))) {

      // Input for password check
      String passwordCheck = JOptionPane.showInputDialog(null,"Please enter your new password again.");

      // Checking that both passwords that user entered matches
      if (password.equals(passwordCheck)){
        JOptionPane.showMessageDialog(null,"User details successfully completed.\n\n\nCreating user account for user [" + username + "]...");
        passwordInput = true;
      } else {
        JOptionPane.showMessageDialog(null,"Your passwords did not match! Please try again.");
      }
    } else {
        JOptionPane.showMessageDialog(null,"Your password did not meet the requirements. Please try again.");
    }

    } while (!passwordInput);

    System.out.println("User details successfully completed.\nProcessing details now.. please wait.");

    // Reading from keystore
    FileInputStream is = new FileInputStream("ServerKeyStore");

    KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
    keystore.load(is, "1qwer$#@!".toCharArray());

    String alias = "serverrsa";

    Key key = keystore.getKey(alias, "1qwer$#@!".toCharArray());

    if (key instanceof PrivateKey) {

        // Get certificate of public key
        Certificate cert = keystore.getCertificate(alias);

        // Get servers' public key
        PublicKey serverPubKey = cert.getPublicKey();

        // Converting String to Bytes
        byte[] usernameInBytes = Crypto.strToBytes(username);
        byte[] passwordInBytes = Crypto.strToBytes(password);

        // Encrypt using public RSA
        byte[] usernameEncrypted = Crypto.encrypt_RSA(usernameInBytes, serverPubKey);
        byte[] passwordEncrypted = Crypto.encrypt_RSA(passwordInBytes, serverPubKey);

        // Creating Credentials Object and Setting details for new user
        Credentials newUser = new Credentials(usernameEncrypted, passwordEncrypted);

        System.out.println("\n\nUser details processing done.\nEncrypted username: " + newUser.getUsername() + "\nEncrypted password: " + newUser.getPassword() + "");

      }

    } catch (Exception e) {
      System.out.print("Error occured while processing user details! [" + e + "]\nProgram will exit.");
      System.exit(0);
    }

    connectToServer();

    }

    public void connectToServer() {
      try {
  			socket = new Socket(registServerAddress, registServerPort);
  		}
  		// if it failed not much I can so
  		catch(Exception e) {
  			System.out.print("Error connecting to server:" + e);
  			//return false;
  		}

      String msg = "Connection accepted " + socket.getInetAddress() + ":" + socket.getPort();
      System.out.print(msg);
      try {
        socket.close();
      }
      catch(Exception e){
        System.out.print("Error disconnecting from server: " + e);
      //return false;
      }
    //return true
    }

    // private void disconnect() {
  	// 	try {
  	// 		if(sInput != null) sInput.close();
  	// 	}
  	// 	catch(Exception e) {} // not much else I can do
  	// 	try {
  	// 		if(sOutput != null) sOutput.close();
  	// 	}
  	// 	catch(Exception e) {} // not much else I can do
    //       try{
  	// 		if(socket != null) socket.close();
  	// 	}
  	// 	catch(Exception e) {} // not much else I can do
  	// }

  }
