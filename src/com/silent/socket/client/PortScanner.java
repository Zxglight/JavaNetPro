package com.silent.socket.client;

import java.io.IOException;
import java.net.Socket;

/**
 * 扫描一台主机的部分端口(1-1024) 是否开启 socket服务
 */
public class PortScanner {

    public static void main(String args[]) {
        String host = "localhost";
        if (args.length > 0) {
            host = args[0];
        }
        new PortScanner().scan(host);
    }

    public void scan(String host) {
        Socket socket = null;
        for (int port = 1; port <= 1024; port++) {
            try {
                socket = new Socket(host, port);
                System.out.println("There is a Server on port" + port);
            } catch (IOException e) {
                System.out.println("Can't connect to port" + port);
            } finally {
                try {
                    if (null != socket) {
                        socket.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}