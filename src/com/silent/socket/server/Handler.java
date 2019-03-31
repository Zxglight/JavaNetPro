package com.silent.socket.server;

import com.silent.socket.util.SocketUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * @author xg.zhao
 * @date 2019 03 31 13:11
 */
public class Handler implements Runnable {

    private Socket socket;

    public Handler(Socket socket) {
        this.socket = socket;
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        try {
            BufferedReader reader = SocketUtils.getReader(socket);
            PrintWriter writer = SocketUtils.getWriter(socket);
            String msg = null;
            while ((msg = reader.readLine()) != null) {
                System.out.println(msg);
                writer.println(msg);
                if (msg.equals("bye")) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
