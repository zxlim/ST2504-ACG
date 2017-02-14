import java.io.FileInputStream;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;

public class test {
  public static void main(String[] argv) throws Exception {
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

      System.out.println(publicKey);

      // Return a key pair
      //new KeyPair(publicKey, (PrivateKey) key);
    }
  }
}
