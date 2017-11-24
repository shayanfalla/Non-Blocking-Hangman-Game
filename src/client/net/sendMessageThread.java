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
package client.net;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

public class sendMessageThread extends Thread {

    private String msg;
    private SocketChannel socketChannel;

    public sendMessageThread(SocketChannel socketChannel, String msg) {
        this.socketChannel = socketChannel;
        this.msg = msg;
    }

    //Sends the message entered by the client
    @Override
    public void run() {
        ByteBuffer buffer = ByteBuffer.wrap(msg.getBytes());
        try {
            socketChannel.write(buffer);
        } catch (IOException ex) {
            Logger.getLogger(sendMessageThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
