import java.io.FileInputStream;
import java.security.Key;
//import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import Crypto;

public class test {
    public static void main(String[] argv) throws Exception {

        //getting KeyStore Ready
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

            //Get private key
            PrivateKey privateKey = (PrivateKey)keystore.getKey(alias, "1qwer$#@!".toCharArray());

            byte[] text = Crypto.strToBytes("hello");
            //encrypt using public RSA
            byte[] ciphertext = Crypto.encrypt_RSA(text, publicKey);

            System.out.println(ciphertext);

            //decrypt using public RSA
            byte[] deciphertext = Crypto.decrypt_RSA(ciphertext, privateKey);
            String deciphered = Crypto.bytesToStr(deciphertext);

            System.out.println("\n"+ deciphered);

            // Return a key pair
            //KeyPair keyPair = new KeyPair(publicKey, (PrivateKey) key);
            //PrivateKey privateKey = keyPair.getPrivate();

        }
    }
}
