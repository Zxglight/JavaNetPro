package com.silent.socket.client;

import java.io.IOException;
import java.net.Socket;

/**
 * @author xg.zhao
 * @date 2019 03 30 23:50
 */
public class Client {

    public static void main(String[] args) throws IOException, InterruptedException {
        final int length = 2;
        String host = "localhost";
        int port = 8001;
        Socket[] sockets = new Socket[length];
        for (int i = 0; i < length; i++) {
            sockets[i] = new Socket(host, port);
            sockets[i].getOutputStream().write("shutdown".getBytes());
            System.out.println("第" + (i + 1) + "次连接成功");
        }
        Thread.sleep(3000);
        for (int i = 0; i < length; i++) {
            sockets[i].close();
        }
    }
}
