//import java.io.FileInputStream;

import java.io.*;
import java.net.*;


public class RegisterServer{

    private static Socket socket;
    private static ObjectInputStream sInput;
    private static ObjectOutputStream sOutput;
    private static int port = 1449;

    public static void main(String[] args) throws Exception {

        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Listening on port: " + port);

            // Retrieving server private key
            PKI serverRSA = Crypto.ksPrivateKey("Server.keystore", "serverrsa");

            System.out.println("Enter Ctrl-C to stop the server.");

            // Start of accepting connections loop
            while(true){

              System.out.println("\nServer accepting registration clients now..");
                // Accept client connection
                socket = serverSocket.accept();
                String clientIP = socket.getLocalSocketAddress().toString();
                System.out.println("\n\tClient connected" + clientIP);

                // Create new I/O stream from socket
                sInput = new ObjectInputStream(socket.getInputStream());
                sOutput =  new ObjectOutputStream(socket.getOutputStream());

                // Read input from Client
                Credentials newUser = (Credentials) sInput.readObject();

                // Decrypting of username and password
                String username = Crypto.bytesToStr(Crypto.decrypt_RSA(newUser.getUsername(), serverRSA.getPrivate()));
                String password = Crypto.bytesToStr(Crypto.decrypt_RSA(newUser.getPassword(), serverRSA.getPrivate()));

                if (file.validateName(username) == 1) {
                    System.out.println("\n\tInvalid Username: [" + username + "] already exists.");
                    System.out.println("\tFailed to Register. Rejecting user registration.");

                    // Sending client "Failure"
                    sOutput.writeObject("Failure");
                } else {
                    System.out.println("\n\tUsername valid. Registering [" + username + "].");
                    byte[] salt = Crypto.secureRand(32);
                    byte[] hashedpw = Crypto.pbkdf2(password, salt);
                    String strHashed = Crypto.bytesToBase64(hashedpw);
                    String strSalt = Crypto.bytesToBase64(salt);
                    file.credentialWriter(username, strHashed , strSalt);
                    System.out.println("\n\t" + username + " registration completed.");

                    // Sending client "Success"
                    sOutput.writeObject("Success");
                }

            }

            //System.out.println("Server disconnected.");
            // sOutput.close();
            // sInput.close();
            // socket.close();

        } catch (Exception e) {
            return;
        }



        //Encryption
        // byte[] username = Crypto.encrypt_RSA(Crypto.strToBytes("aiman"), serverRSApub.getPublic());
        // byte[] password = Crypto.encrypt_RSA(Crypto.strToBytes("1qwe#@!"), serverRSApub.getPublic());
        // //Name Decryption
        // String name = Crypto.bytesToStr(Crypto.decrypt_RSA(username, serverRSA.getPrivate()));
        // String passwd = Crypto.bytesToStr(Crypto.decrypt_RSA(password, serverRSA.getPrivate()));



    }
}
