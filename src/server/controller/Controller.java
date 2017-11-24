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
package server.controller;

import java.io.IOException;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.model.fileReader;
import server.model.HangmanHandler;

public class Controller {

    public void initHangman(SocketChannel socketChannel, Socket socket, HashMap clients) {
        try {
            HangmanHandler handler = new HangmanHandler(socketChannel, socket);
            handler.run("whatever");
            clients.put(socketChannel.getRemoteAddress(), handler);
        } catch (IOException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void contHangman(String msg, HashMap clients, SocketChannel socketChannel) {
        try {
            HangmanHandler handler = (HangmanHandler) clients.get(socketChannel.getRemoteAddress());
            handler.run(msg);
            clients.put(socketChannel.getRemoteAddress(), handler);
        } catch (IOException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
