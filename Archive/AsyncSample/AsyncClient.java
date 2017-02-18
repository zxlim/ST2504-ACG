/*
** Working proof-of-concept of Non-Blocking I/O Chat Server listening on multiple ports.
** AsyncClient
** zx
*/

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class AsyncClient {

    public static void main(String[] args) throws IOException, InterruptedException {
        String host = "localhost";
        int port = -1;

        if (args.length != 2) {
            display("Usage: java AsyncClient [IP Address] [Port]");
            return;
        }

        host = args[0];

        try {
            port = Integer.parseInt(args[1]);
        } catch (Exception ex) {
            display("Invalid port number.");
            display("Usage: java AsyncClient [IP Address] [Port]");
            return;
        }

        if (port < 0 || port > 65535) {
            display("Invalid port number.");
            display("Usage: java AsyncClient [IP Address] [Port]");
            return;
        }

        display("Connecting to server...");

        InetSocketAddress serverAddr = new InetSocketAddress(host, port);
        SocketChannel clientSocket = SocketChannel.open(serverAddr);

        display("Connected to Server on " + host + ":" + port);
        display("Type 'exit' to disconnect.");
        Scanner scan = new Scanner(System.in);

        while (true) {
            System.out.print("Message: ");
            String msg = scan.nextLine();
            byte[] message = msg.getBytes();
            ByteBuffer buffer = ByteBuffer.wrap(message);
            clientSocket.write(buffer);

            buffer.clear();

            if (msg.equalsIgnoreCase("exit")) {
                break;
            } else {
                display("Sent message: " + msg);
            }

            Thread.sleep(200); // 0.2 Second delay
        } // while(true)

        scan.close();
        clientSocket.close();
        display("Disconnected from server.");

    } // main

    private static void display(String buf) {
        System.out.println(buf);
    }
}
