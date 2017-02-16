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
import java..;

public class RegistrationServer {

    private static ServerSocketChannel serverSocket;

    public static void main(String[] args) throws IOException, InterruptedException {

        final PKI serverRSA = Crypto.ksPrivateKey("Server.keystore", "serverRSA");

        int[] ports = {8885};
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
                    display("A client with address " + clientSocket.getLocalAddress() + " is attempting to register.");

                } else if (sKey.isReadable()) {

                    SocketChannel clientSocket = (SocketChannel) sKey.channel();
                    final int clientPort = clientSocket.socket().getLocalPort();

                    if (clientPort == 8885) {

                        ByteBuffer clientBuffer = ByteBuffer.allocate(256);
                        clientSocket.read(clientBuffer);

                        ObjectInputStream sInput = new ObjectInputStream(clientSocket().socket().getInputStream());

                        Credentials userReg = (Credentials) sInput.readObject();


                    }

                    serverIterator.remove();
                }
            } // while(serverIterator.hasNext())
        } // while(true)
    } // main

    private static void display(final String buf) {
        System.out.println(buf);
    }
} // class
