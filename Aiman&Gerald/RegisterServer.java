import java.io.FileInputStream;

import java.io.*;
import java.net.*;


public class RegisterServer{

    private static Socket socket;
    private static ObjectInputStream sInput;
    private static ObjectOutputStream sOutput;

    public static void main(String[] args) throws Exception {

        try {
            ServerSocket serverSocket = new ServerSocket(1449);
            System.out.println("Listening on port: 1449");

            //Accept client connection
            boolean go = true;
            PKI serverRSA = Crypto.ksPrivateKey("Server.keystore", "serverrsa");


            while(go){

                //Accept client connection
                socket = serverSocket.accept();
                String clientIp = socket.getLocalSocketAddress().toString();
                System.out.println("Client connected" + clientIp );

                //Create new I/O stream from socket

                sInput = new ObjectInputStream(socket.getInputStream());
                sOutput =  new ObjectOutputStream(socket.getOutputStream());

                //sOutput.writeObject("Success!");
                System.out.println("Input initialized");

                //Read input from Client
                Credentials newUser = (Credentials) sInput.readObject();

                //Decrypting the username
                String name = Crypto.bytesToStr(Crypto.decrypt_RSA(newUser.getUsername(), serverRSA.getPrivate()));
                System.out.println("Client's username: " + name);

                //Send ObjectOutputStream
                sOutput.writeObject("cat");
                System.out.print("Sent CAT.");

                try {
                  sInput.close();
                  sOutput.close();
                } catch (Exception IOException){
                  System.out.print("IO Close error: " + IOException);
                }
            }

            socket.close();
        } catch (Exception e) {
            System.out.print(e);
            e.printStackTrace();
            return;
        }

        /*
        Credentials newUser = (Credentials) sInput.readObject();
        byte[] username = user.getUsername();
        byte[] password = user.getPassword();
        */
        //getting KeyStore Ready
        //PKI serverRSApub = Crypto.ksPublicKey("Server.keystore", "serverrsa");

        //Encryption
        // byte[] username = Crypto.encrypt_RSA(Crypto.strToBytes("aiman"), serverRSApub.getPublic());
        // byte[] password = Crypto.encrypt_RSA(Crypto.strToBytes("1qwe#@!"), serverRSApub.getPublic());
        // //Name Decryption
        // String name = Crypto.bytesToStr(Crypto.decrypt_RSA(username, serverRSA.getPrivate()));
        // String passwd = Crypto.bytesToStr(Crypto.decrypt_RSA(password, serverRSA.getPrivate()));


        // if (file.validateName(name) == 1) {
        //     System.out.println("Invalid Username");
        // } else {
        //
        //     byte[] salt = Crypto.secureRand(32);
        //     byte[] hashedpw = Crypto.pbkdf2(passwd, salt);
        //     String strHashed = Crypto.bytesToBase64(hashedpw);
        //     String strSalt = Crypto.bytesToBase64(salt);
        //     file.credentialWriter(name, strHashed , strSalt);
        //     System.out.println("Username Valid");
        //
        // }

    }
}
