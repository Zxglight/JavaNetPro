package com.silent.socket.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author xg.zhao
 * @date 2019 03 31 13:06
 */
public class EchoServer {

    private int port = 8000;

    private ServerSocket serverSocket;

    public EchoServer() throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("服务器启动");
    }

    public static void main(String[] args) throws IOException {
        new EchoServer().service();
    }

    public void service() {
        while (true) {
            Socket socket;
            try {
                socket = serverSocket.accept();
                //                为每个连接分配一个线程
                Thread workThread = new Thread(new Handler(socket));
                workThread.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
