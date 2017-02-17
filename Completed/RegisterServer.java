/*
**	[ST2504 Applied Cryptography Assignment]
**	[Encrypted Chat Program]
**
**	Aeron Teo (P1500725)
**	Aiman Abdul Rashid (P1529335)
**	Gerald Peh (P1445972)
**	Lim Zhao Xiang (P1529559)
*/

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


            boolean acceptingConnections = true;
            boolean awaitingOption = true;

            // Start of accepting connections loop
            while(acceptingConnections){
                // Retrieving server private key
                System.out.println("Listening on port: " + port);
                PKI serverRSA = Crypto.ksPrivateKey("Server.keystore", "serverrsa");

                System.out.println("Enter Ctrl-C to stop the server.");

                awaitingOption = true;
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

                if (File.validateName(username) == 1) {
                    System.out.println("\n\tInvalid Username: [" + username + "] already exists.");
                    System.out.println("\tFailed to Register. Rejecting user registration.\n");

                    // Sending client "Failure"
                    sOutput.writeObject("Failure");
                } else {
                    System.out.println("\n\tUsername valid. Registering [" + username + "].");
                    byte[] salt = Crypto.secureRand(32);
                    byte[] hashedpw = Crypto.pbkdf2(password, salt);
                    String strHashed = Crypto.bytesToBase64(hashedpw);
                    String strSalt = Crypto.bytesToBase64(salt);
                    File.credentialWriter(username, strHashed , strSalt);
                    System.out.println("\n\t[" + username + "] registration completed.\n");

                    // Sending client "Success"
                    sOutput.writeObject("Success");
                }
            }
        } catch (Exception e) {
            return;
        }
    }
}
