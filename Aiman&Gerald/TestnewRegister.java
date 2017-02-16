
import javax.swing.JOptionPane;
import java.net.*;
import java.io.*;
import java.security.Key;
import java.lang.*;


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

public class TestnewRegister {

  // Server address and port
  private static String registServerAddress = "localhost";
  private static int registServerPort = 1449;

  // For user details
  private static String username;
  private static String password;
  private static Credentials newUser;

  // for I/O
  private static ObjectInputStream sInput;		// to read from the socket
  private static ObjectOutputStream sOutput;		// to write on the socket
  private static Socket socket;

    public static void main(String[] args){

    // For username and password loop
    boolean usernameInput = false;
    boolean passwordInput = false;

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

    // Asking for Registration Server IP
    registServerAddress = JOptionPane.showInputDialog(null,"Please input the registration server IP address.","Server IP Address",JOptionPane.OK_CANCEL_OPTION);

    if (registServerAddress.equals("") || registServerAddress == null){
        registServerAddress = "localhost";
    }

    System.out.print("Connecting to Registration Server IP " + registServerAddress + "\n");

    // Username input loop
    do {
        username = JOptionPane.showInputDialog(null,"Please enter your new username.\nNOTE \t : \t are not allowed.");

        if (username.equals("") || username == null){
          username = "Gerald";
          usernameInput = true;
          //JOptionPane.showMessageDialog(null,"You did not enter a username. Please try again.");
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
        JOptionPane.showMessageDialog(null,"User details successfully completed.\nUser account creation for user [" + username + "] will commence.");
        passwordInput = true;
      } else {
        JOptionPane.showMessageDialog(null,"Your passwords did not match! Please try again.");
      }
    } else {
        JOptionPane.showMessageDialog(null,"Your password did not meet the requirements. Please try again.");
    }

    } while (!passwordInput);

    System.out.println("User details successfully completed.\nProcessing details now.. please wait.");

        PKI serverRSA = Crypto.ksPublicKey("Client.keystore", "serverrsa");

        // Converting String to Bytes
        byte[] usernameInBytes = Crypto.strToBytes(username);
        byte[] passwordInBytes = Crypto.strToBytes(password);

        // Encrypt using public RSA
        byte[] usernameEncrypted = Crypto.encrypt_RSA(usernameInBytes, serverRSA.getPublic());
        byte[] passwordEncrypted = Crypto.encrypt_RSA(passwordInBytes, serverRSA.getPublic());

        // Creating Credentials Object and Setting details for new user
        newUser = new Credentials(usernameEncrypted, passwordEncrypted);

        System.out.println("\n\nUser details processing done.\nEncrypted username: " + newUser.getUsername() + "\nEncrypted password: " + newUser.getPassword() + "");

        // Create socket to connect to server
        try {
          socket = new Socket(registServerAddress, registServerPort);
        } catch (Exception eSocket){
          System.out.print("\nError with socket: " + eSocket);
          System.exit(0);
        }

        System.out.print("Connection to server socket success.\n");
        String msg = "Connection accepted @ " + socket.getInetAddress() + ":" + socket.getPort() + "\n";
        System.out.print(msg);


//        Send to server > newUser
        boolean result = sendToServer(newUser);

        if (!result) {
          System.out.print("\nDispatch failed. Please check server connection.\n");
          JOptionPane.showMessageDialog(null,"Dispatch failed. Please check server connection");
          System.exit(0);
        } else {
          System.out.print("\nDispatch to server success.\n");
          JOptionPane.showMessageDialog(null,"Dispatch to server success.");
        }

        // Retrieve result from server
        boolean checkResult = checkValid();

        if (!checkResult){
          System.out.print("\nValidation failed. Your username is taken. Please use another username.\n");
          JOptionPane.showMessageDialog(null,"Your username is taken. Please use another username.\n");
        } else {
          System.out.print("\nValidation passed.\n");
          JOptionPane.showMessageDialog(null,"User registration complete. You can now log in to the chat.\n");
        }

        result = disconnect();

        if (!result){
          System.out.print("Disconnection from server failed.\n");
        } else {
          System.out.print("Disconnection success.\n");
        }

    } catch (Exception e) {
      //System.out.print("Error occured while processing user details! [" + e + "]\nProgram will exit.");
      System.out.print( e + "\nProgram will exit.");
      System.exit(0);
    }

  } // End of main()

    public static void start(){


    }


    public static boolean sendToServer(Credentials newUser){
      try {

        // Create output stream to write to server
        try {
          System.out.print("Initialising Output stream...");
          sOutput = new ObjectOutputStream(socket.getOutputStream());
          //sInput = new ObjectInputStream(socket.getInputStream());
        } catch (Exception eStream) {
          System.out.print("\nError with streams: " + eStream);
          System.exit(0);
        }

        System.out.print("\nInitialisation of Output stream success.");

        // Sending object "newUser" to server
        sOutput.writeObject(newUser);

        // Close the output stream
        //sOutput.close();

        return true;

      } catch (Exception e){
        System.out.print("\nError sending user details to server: " + e);
        return false;
      }
    } // End of sendToServer()


    public static boolean checkValid(){
      try {
      Thread.sleep(1000);
      } catch (Exception eSleep){
        System.out.print("Error in sleep: " + eSleep);
      }

      System.out.print("Checking for validity...\n");
      String checkReturn = "";
      try {
        // Create input stream
        try {
          System.out.print("Initialising Input stream...");
        //sOutput = new ObjectOutputStream(socket.getOutputStream());
        sInput = new ObjectInputStream(socket.getInputStream());
        } catch (Exception eStream) {
          System.out.print("\nError with streams: " + eStream);
          System.exit(0);
        }
      checkReturn = (String) sInput.readObject();
      System.out.print("\nInputStream read.");
      System.out.print(checkReturn);
      //sInput.close();

          if (checkReturn.equals("Success")){
            return true;
          } else {
            return false;
          }


      } catch (Exception e){
        System.out.print("\ncheckValid() method error: " + e);
        return false;
      }
    } // End of checkValid()


    public static boolean disconnect(){
      try {
        socket.close();
        return true;
      }
      catch(Exception e){
        System.out.print("Error disconnecting from server: " + e);
        return false;
      }

    } // End of disconnect()


  } // End of newRegister class
