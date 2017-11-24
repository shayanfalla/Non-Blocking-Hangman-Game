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

import java.io.*;
import java.net.*;
import java.nio.channels.*;

public class ServerMain {

    private static ServerSocketChannel serverSocketChannel;
    private static final int PORT = 1997;
    private static Selector selector;

    public static void main(String[] args) {
        ServerSocket serverSocket;
        try {
            System.out.println("Server Starting...\n");

            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocket = serverSocketChannel.socket();
            InetSocketAddress netAddress = new InetSocketAddress(PORT);
            serverSocket.bind(netAddress);
            selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        NonBlockingClientHandler ch = new NonBlockingClientHandler(serverSocketChannel, selector);
        ch.processConnections();
    }
}
