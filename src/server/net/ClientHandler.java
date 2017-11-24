/*
 * Copyright (C) 2017 Shayan Fallahian shayanf@kth.se
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package server.net;

import server.controller.Controller;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class ClientHandler {

    private final ServerSocketChannel serverSocketChannel;
    private final Selector selector;
    private final HashMap clients;
    private final Controller control;

    public ClientHandler(ServerSocketChannel serverSocketChannel, Selector selector) {
        this.serverSocketChannel = serverSocketChannel;
        this.selector = selector;
        this.clients = new HashMap();
        this.control = new Controller();
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

        //Initializing the gamesession
        //and storing it
        control.initHangman(socketChannel, socket, clients);

    }

    private void acceptData(SelectionKey key) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(2048);
        SocketChannel socketChannel = (SocketChannel) key.channel();
        buffer.clear();
        int numBytes = socketChannel.read(buffer);
        Socket socket = socketChannel.socket();
        if (numBytes == -1) { //Closed connection or error --> -1
            //Remove client game session when connection is terminated
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
        control.contHangman(new String(bytes), clients, socketChannel);
    }
}
