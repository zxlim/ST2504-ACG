import java.io.FileInputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;

public class RegisterServer{
    public static void main(String[] args) throws Exception {
        /*
        user = Credentials;
        byte[] username = user.getUsername();
        byte[] password = user.getPassword();
        byte[] RSApub = user.getRSApub();
        byte[] ECDSApub = user.getECDSApub;
        */
        PublicKey publicKey = null;
        PrivateKey privateKey = null;

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
            publicKey = cert.getPublicKey();

            //Get private key
            privateKey = (PrivateKey)keystore.getKey(alias, "1qwer$#@!".toCharArray());

        }

        //Encryption
        byte[] username = Crypto.encrypt_RSA(Crypto.strToBytes("aiman"), publicKey);
        byte[] password = Crypto.encrypt_RSA(Crypto.strToBytes("1234"), publicKey);
        System.out.println(username);
        //Name Decryption
        String name = Crypto.bytesToStr(Crypto.decrypt_RSA(username, privateKey));
        String passwd = Crypto.bytesToStr(Crypto.decrypt_RSA(password, privateKey));
        System.out.println(name + passwd);


    }
}
