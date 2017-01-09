import javax.crypto.Cipher;

public class JCE_Test {
  public static void main(String[] args) {
    try {
      System.out.println("Checking maximum AES Key Length...");
      Thread.sleep(2000);
      int maxKeyLen = Cipher.getMaxAllowedKeyLength("AES");
      System.out.println("Maximum keylength for AES ciphers: " + maxKeyLen);
      if (maxKeyLen <= 128) {
            System.out.println("Java Cryptographic Extension (JCE) not installed or corrupted.");
            System.out.println("Download JCE for Java 8 at: http://bit.ly/1RCPSqx");
      } else {
            System.out.println("Java Cryptographic Extension installed successfully!");
      }
    } catch (Exception ex){
      System.out.println("Ohmygawd an error occured !!!\n");
      System.out.println(ex);
      ex.printStackTrace();
    }
  }
}
