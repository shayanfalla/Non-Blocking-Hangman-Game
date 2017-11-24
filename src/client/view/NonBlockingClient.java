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
package client.view;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import client.controller.Controller;

public class NonBlockingClient {

    private BufferedReader userInputReader = null;
    private Selector selector;
    private SocketChannel socketChannel;
    public boolean doneStatus;
    private Controller control;

    public NonBlockingClient(Selector selector, SocketChannel socketChannel) {
        this.userInputReader = new BufferedReader(new InputStreamReader(System.in));
        this.selector = selector;
        this.socketChannel = socketChannel;
        this.control = new Controller(socketChannel);
    }

    public void connection() throws Exception {
        Thread.sleep(20);
        if (selector.select() > 0) {
            intialRead(selector.selectedKeys());
        }
        //Client server communication
        while (true) {
            if (selector.select() > 0) {
                doneStatus = ReadWriteServer(selector.selectedKeys());
                if (doneStatus) {
                    break;
                }
            }
        }
        socketChannel.close();
    }

    //Receives the initial message before the user can write anything.
    public void intialRead(Set readySet) throws Exception {
        Iterator iterator = readySet.iterator();
        while (iterator.hasNext()) {
            SelectionKey key = (SelectionKey) iterator.next();
            iterator.remove();
            if (key.isReadable()) {
                DisplayThread dt = new DisplayThread(key);
                dt.start();
            }
        }
    }

    //This is were the selector is broken down (events are gathered), 
    //and the READ / WRITE is interpreted
    public boolean ReadWriteServer(Set readySet) throws Exception {
        Iterator iterator = readySet.iterator();
        while (iterator.hasNext()) {
            SelectionKey key = (SelectionKey) iterator.next();
            iterator.remove();
            if (key.isConnectable()) {
                boolean connected = processConnect(key);
                if (!connected) {
                    return true; // Exit
                }
            }
            if (key.isReadable()) {
                DisplayThread dt = new DisplayThread(key);
                dt.start();
            }
            if (key.isWritable()) {
                String msg = userInputReader.readLine();
                if (msg.equalsIgnoreCase("-") || msg.equalsIgnoreCase("no")) {
                    return true; // Exit
                }
                // sends message to server
                control.sendMessage(msg);
                //KeyboardThread kt = new KeyboardThread(socketChannel);
                //kt.start();
                Thread.sleep(10);
            }
        }
        return false; // Continue
    }

    //Checks if there is a connection
    public boolean processConnect(SelectionKey key) throws Exception {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        while (socketChannel.isConnectionPending()) {
            socketChannel.finishConnect();
        }
        return true;
    }
}
