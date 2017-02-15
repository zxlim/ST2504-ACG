import java.io.FileInputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;

import java.io.*;

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
        String alias = "serverrsa";
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
        byte[] username = Crypto.encrypt_RSA(Crypto.strToBytes("jasong"), publicKey);
        byte[] password = Crypto.encrypt_RSA(Crypto.strToBytes("1qwe#@!"), publicKey);
        //Name Decryption
        String name = Crypto.bytesToStr(Crypto.decrypt_RSA(username, privateKey));
        String passwd = Crypto.bytesToStr(Crypto.decrypt_RSA(password, privateKey));
        //System.out.println(passwd);

        if (file.validateName(name) == 1) {
            System.out.println("Invalid Username");
        } else {

            byte[] salt = Crypto.secureRand(32);
            byte[] hashedpw = Crypto.pbkdf2(passwd, salt);
            String strHashed = Crypto.bytesToBase64(hashedpw);
            String strSalt = Crypto.bytesToBase64(salt);
            file.credentialWriter(name, strHashed , strSalt);
            System.out.println("Username Valid");
        }

    }
}
