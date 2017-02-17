//import java.io.FileInputStream;

import java.io.*;
import java.util.Scanner;
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

            //Start scanner instance
            Scanner scan = new Scanner(System.in);




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
                /*
                System.out.println("\nDo you want to continue? Enter YES/NO to continue/disconnect.");

                while (awaitingOption){
                String continueAcception = scan.nextLine();

                if (continueAcception.equalsIgnoreCase("yes")){
                System.out.print("\nContinuing connection...");
                acceptingConnections = true;
                awaitingOption = false;
            } else if (continueAcception.equalsIgnoreCase("no")){
            System.out.print("\nDisconnecting...");
            acceptingConnections = false;
            awaitingOption = false;
        } else {
        System.out.print("\nEnter YES or NO to continue accepting connections or disconnect.");
    }

}
*/
}
/*
System.out.println("\nServer disconnected.");
sOutput.close();
sInput.close();
socket.close();
*/
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
