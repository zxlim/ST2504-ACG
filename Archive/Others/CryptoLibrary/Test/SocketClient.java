import java.io.*;
import java.net.*;
import java.util.Scanner;

public class SocketClient {
    public static void main(String[] args) {
        try {
            //Create socket to connect to server
            Socket socket = new Socket("localhost", 2001);

            //Create output stream to write to server
            ObjectOutputStream sOutput = new ObjectOutputStream(socket.getOutputStream());

            //Start scanner instance
            Scanner scan = new Scanner(System.in);

            while(true) {
                System.out.print("Message: ");
                String input = scan.nextLine();

                //Send input to server
                sOutput.writeObject(input);

                if (input.equalsIgnoreCase("/LOGOUT")) {
                    break;
                }
            }

            scan.close();
            sOutput.close();
            socket.close();

            //Return
            return;
        } catch (Exception e) {
            //ggwp
        }
    }
}
