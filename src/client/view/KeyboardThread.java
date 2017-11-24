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

import client.controller.Controller;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.channels.SocketChannel;

public class KeyboardThread extends Thread {

    private final BufferedReader userInputReader;
    private final SocketChannel socketChannel;
    private final Controller control;

    public KeyboardThread(SocketChannel socketChannel) {
        this.userInputReader = new BufferedReader(new InputStreamReader(System.in));
        this.control = new Controller(socketChannel);
        this.socketChannel = socketChannel;
    }

    @Override
    public void run() {
        try {
            String msg = userInputReader.readLine();
            if (msg.equalsIgnoreCase("-") || msg.equalsIgnoreCase("no")) {
                socketChannel.close();
                System.exit(0);
            }
            // sends message to server
            control.sendMessage(msg);
        } catch (IOException ex) {
        }
    }
}
