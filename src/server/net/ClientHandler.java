/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.net;

import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientHandler {

    private final ServerSocketChannel serverSocketChannel;
    private final Selector selector;
    private final HashMap clients;

    public ClientHandler(ServerSocketChannel serverSocketChannel, Selector selector) {
        this.serverSocketChannel = serverSocketChannel;
        this.selector = selector;
        this.clients = new HashMap();
    }

    public void processConnections() {
        do {
            try {
                // Returnes the number of events 
                // that have occured or is beeing monitored
                int numKeys = selector.select();
                if (numKeys > 0) {
                    //Set up the selected set of keys that have been found
                    Set eventKeys = selector.selectedKeys();
                    //Set up the iterator of the selected set
                    Iterator IterateKeys = eventKeys.iterator();
                    while (IterateKeys.hasNext()) {
                        //Selected the specific key
                        SelectionKey key = (SelectionKey) IterateKeys.next();
                        //Returns the set of operations as a set of bit patterns
                        int keyOps = key.readyOps();
                        //checks which operation it is
                        if ((keyOps & SelectionKey.OP_ACCEPT) == SelectionKey.OP_ACCEPT) {
                            acceptConnection(key);
                            continue;
                        }
                        if ((keyOps & SelectionKey.OP_READ) == SelectionKey.OP_READ) {
                            acceptData(key);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } while (true);
    }

    private void acceptConnection(SelectionKey key) throws IOException {
        SocketChannel socketChannel = serverSocketChannel.accept();
        //Setting to non-blocking socket
        socketChannel.configureBlocking(false);
        Socket socket = socketChannel.socket();
        System.out.println("Connection with " + socket + " has been established.\n");
        socketChannel.register(selector, SelectionKey.OP_READ);
        selector.selectedKeys().remove(key);

        HangmanHandler handler = new HangmanHandler(socketChannel, socket);
        handler.run("whatever");
        clients.put(socketChannel.getRemoteAddress(), handler);
        System.out.println(clients.entrySet());

    }

    private void acceptData(SelectionKey key) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(2048);
        SocketChannel socketChannel = (SocketChannel) key.channel();
        buffer.clear();
        int numBytes = socketChannel.read(buffer);
        Socket socket = socketChannel.socket();
        if (numBytes == -1) { //Closed connection or error --> -1
            clients.remove(socketChannel.getRemoteAddress());
            key.cancel();
            System.out.println("\nCcnnection with " + socket + " has been terminated.\n");
            closeSocket(socket);
        } else {
            readMessage(buffer, socketChannel);
        }
        selector.selectedKeys().remove(key);
    }

    private void closeSocket(Socket socket) {
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            System.out.println("Unable to close socket!");
        }
    }

    //Converts the message received from the client to String (from bytes)
    //Then calls the clients current hangman session
    private void readMessage(ByteBuffer buffer, SocketChannel socketChannel) {
        buffer.flip();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        try {
            HangmanHandler handler = (HangmanHandler) clients.get(socketChannel.getRemoteAddress());
            handler.run(new String(bytes));
            clients.put(socketChannel.getRemoteAddress(), handler);
        } catch (IOException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
