/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.net;

import java.io.*;
import java.net.*;
import java.nio.channels.*;

/**
 *
 * @author Shayan
 */
public class TestMain {

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
        ClientHandler ch = new ClientHandler(serverSocketChannel, selector);
        ch.processConnections();
    }
}
