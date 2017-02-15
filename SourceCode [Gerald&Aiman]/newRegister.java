
import javax.swing.JOptionPane;
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
    int option =  JOptionPane.showConfirmDialog(null, "This program is used to REGISTER new users ONLY.\nPlease exit this program if you are an existing user or already have an account.\nHit CANCEL to exit.\n\n","Create an account",JOptionPane.OK_CANCEL_OPTION);

try {
    // Checking to continue registering new user
    if (option == JOptionPane.CANCEL_OPTION){
      System.exit(0);
    }

    String username = JOptionPane.showInputDialog(null,"Please enter your new username.");

    if (username.equals("") || username == null){
      JOptionPane.showMessageDialog(null,"Error! You did not enter a username. Please try again.");
      System.exit(0);
    }

    String password = JOptionPane.showInputDialog(null,"Please enter your new password.");

    String passwordCheck = JOptionPane.showInputDialog(null,"Please enter your new password again.");

    if (password.equals(passwordCheck)){
      JOptionPane.showMessageDialog(null,"Your account \'" + username +  "\' has been successfully created!");
    } else {
      JOptionPane.showMessageDialog(null,"Your passwords did not match! Please try again.");
    }

  } catch(Exception e){
    System.out.print("Error occured!\n[" + e + "]\nProgram will exit.");
    System.exit(0);
  }

  //reading from keystore
  FileInputStream is = new FileInputStream("ServerKeyStore");

  KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
  keystore.load(is, "1qwer$#@!".toCharArray());

  String alias = "server";

  Key key = keystore.getKey(alias, "1qwer$#@!".toCharArray());

  if (key instanceof PrivateKey) {
    
      // Get certificate of public key
      Certificate cert = keystore.getCertificate(alias);

      // Get public key
      PublicKey publicKey = cert.getPublicKey();

      //converting String to Bytes
      byte[] text = Crypto.strToBytes("hello");

      //encrypt using public RSA
      byte[] ciphertext = Crypto.encrypt_RSA(text, publicKey);

      System.out.println(ciphertext);

}
}
}
