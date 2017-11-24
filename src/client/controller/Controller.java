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
package client.controller;

import client.net.sendMessageThread;
import java.nio.channels.SocketChannel;

public class Controller {

    private SocketChannel socketChannel;

    public Controller(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    public void sendMessage(String msg) {
        sendMessageThread kt = new sendMessageThread(socketChannel, msg);
        kt.start();
    }

}
