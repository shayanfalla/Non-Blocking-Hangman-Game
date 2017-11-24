/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.view;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DisplayThread extends Thread {

    private String msg;
    private SelectionKey key;

    public DisplayThread(SelectionKey key) {
        this.key = key;
    }

    @Override
    public void run() {
        try {
            SocketChannel socketChannel = (SocketChannel) key.channel();
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            socketChannel.read(buffer);
            buffer.flip();
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);
            msg = new String(bytes);
            System.out.println(msg);
        } catch (IOException ex) {
            Logger.getLogger(DisplayThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
