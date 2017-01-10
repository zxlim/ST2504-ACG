import javax.crypto.Cipher;

public class JCE_Test {
  public static void main(String[] args) {
    try {
      System.out.println("Checking maximum AES key length...");
      Thread.sleep(1500);
      final int maxKeyLen = Cipher.getMaxAllowedKeyLength("AES");
      System.out.println("Maximum AES key length: " + maxKeyLen);
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
