import javax.crypto.Cipher;

public class JCE_Test {
  public static void main(String[] args) {

    if (args.length != 0) {
      System.out.println("Usage: java JCE_Test");
    } else {
      try {
        final String version = System.getProperty("java.version");
        final int maxKeyLen = Cipher.getMaxAllowedKeyLength("AES");
        System.out.println("Java Runtime version on system: " + version + "\n");
        System.out.println("Checking Java Cryptographic Extension Installation...");
        Thread.sleep(1000);
        if (maxKeyLen <= 128) {
              System.out.println("Java Cryptographic Extension (JCE) not installed or corrupted.\nDownload JCE for Java 8 at: http://bit.ly/1RCPSqx");
        } else {
              System.out.println("Java Cryptographic Extension installed successfully!");
        }
      } catch (Exception ex){
        System.out.println("Exception caught:");
        System.out.println(ex);
        ex.printStackTrace();
      }
    }
  } //main
} //class
