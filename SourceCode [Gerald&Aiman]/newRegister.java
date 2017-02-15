
import javax.swing.JOptionPane;
import java.io.FileInputStream;
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


  public static void main(String[] args){
    int option =  JOptionPane.showConfirmDialog(null, "This program is used to register new users only.\nPlease exit this program if you are an existing user or already have an account.\n\t\tHit CANCEL to exit.\n\n","Create an account",JOptionPane.OK_CANCEL_OPTION);
    boolean usernameInput = false;
    boolean passwordInput = false;
    String username;
    String password;
    byte[] usernameRSA;
    byte[] passwordRSA;

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
        JOptionPane.showMessageDialog(null,"User details successfully completed.\nCreating user account for user [" + username + "] \n\nPlease wait..");
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
        Credentials newUser = Crendentials.setUsername(usernameEncrypted);
        newUser = Crendentials.setPassword(passwordEncrypted);

        System.out.println("\n\nUser details processing done.\nEncrypted username: " + usernameEncrypted + "\nEncrypted password: " + passwordEncrypted + "");

      }

    } catch (Exception e) {
      System.out.print("Error occured while processing user details! [" + e + "]\nProgram will exit.");
      System.exit(0);
    }

    }
  }
