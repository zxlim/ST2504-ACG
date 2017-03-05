import java.io.*;
import java.net.*;

public class SocketServer {

    private static Socket socket;
    private static ObjectInputStream sInput;

    public static void main(String[] args) {
        try {
            //Start a ServerSocket instance to listen on port
            int port = 2001;
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Listening on port " + port);

            boolean go = true;

            while(go) {

                //Accept client connection
                socket = serverSocket.accept();
                System.out.println("Client connected");

                //Create new input stream from socket
                sInput = new ObjectInputStream(socket.getInputStream());

                //Read input from Client
                String msg = (String) sInput.readObject();

                System.out.println("Message from Client: " + msg);

                if (msg.equalsIgnoreCase("/LOGOUT")) {
                    go = false;
                    break;
                }
            }

            sInput.close();
            socket.close();

            //Return
            return;
        } catch (Exception e) {
            //ggwp
        }
    }
}
