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
package client.startup;


import client.view.NonBlockingClient;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class StartClient {
    
    private static final int PORT = 1997;
    private static int keyOP;

    public static void main(String[] args) throws Exception {
        InetAddress serverIPAddress = InetAddress.getByName("localhost");
        InetSocketAddress serverAddress = new InetSocketAddress(serverIPAddress, PORT);
        Selector selector = Selector.open();
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        socketChannel.connect(serverAddress);
        keyOP = SelectionKey.OP_CONNECT | SelectionKey.OP_READ | SelectionKey.OP_WRITE;
        socketChannel.register(selector, keyOP);

        NonBlockingClient ServerHandler = new NonBlockingClient(selector, socketChannel);
        ServerHandler.connection();
    }
    
}
