/*
** Working proof-of-concept of Non-Blocking I/O Chat Server listening on multiple ports.
** AsyncServer
** zx
*/

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class AsyncServer {

    private static ServerSocketChannel serverSocket;

    public static void main(String[] args) throws IOException, InterruptedException {

        int[] ports = {8885, 8886, 8887};
        display("Press 'Ctrl + C' to stop server.");

        Selector selector = Selector.open();

        for (int port : ports){

            serverSocket = ServerSocketChannel.open();
            InetSocketAddress serverAddr = new InetSocketAddress(port);

            serverSocket.bind(serverAddr);
            serverSocket.configureBlocking(false);

            int ops = serverSocket.validOps();
            SelectionKey selectKey = serverSocket.register(selector, ops, null);
            display("Waiting for connection on port " + port);

        }

        while(selector.isOpen()) {

            selector.select();

            Set<SelectionKey> serverKeys = selector.selectedKeys();
            Iterator<SelectionKey> serverIterator = serverKeys.iterator();

            while (serverIterator.hasNext()) {

                SelectionKey sKey = serverIterator.next();

                if (sKey.isAcceptable()) {

                    SocketChannel clientSocket = ((ServerSocketChannel) sKey.channel()).accept();
                    clientSocket.configureBlocking(false);

                    clientSocket.register(selector, SelectionKey.OP_READ);
                    display("Connection Accepted: " + clientSocket.getLocalAddress());

                } else if (sKey.isReadable()) {

                    SocketChannel clientSocket = (SocketChannel) sKey.channel();
                    final int clientPort = clientSocket.socket().getLocalPort();

                    if (clientPort == 8885) {

                        ByteBuffer clientBuffer = ByteBuffer.allocate(256);
                        clientSocket.read(clientBuffer);
                        String result = new String(clientBuffer.array()).trim();

                        if (result.equalsIgnoreCase("exit")) {
                            clientSocket.close();
                            display("Disconnected a client. Server is still running.");
                        } else {
                            display("Message received: " + result);
                        }

                    } else if (clientPort == 8886 || clientPort == 8887) {

                        ByteBuffer clientBuffer = ByteBuffer.allocate(256);
                        clientSocket.read(clientBuffer);
                        String result = new String(clientBuffer.array()).trim();

                        if (result.equalsIgnoreCase("exit")) {
                            clientSocket.close();
                            display("Disconnected a client. Server is still running.");
                        } else {
                            display("This is port " + clientPort + "! Use port 8885 to send messages.");
                        }
                    }
                }

                serverIterator.remove();

            } // while(serverIterator.hasNext())
        } // while(true)
    } // main

    private static void display(final String buf) {
        System.out.println(buf);
    }
} // class
