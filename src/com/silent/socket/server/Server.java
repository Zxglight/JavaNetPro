package com.silent.socket.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author xg.zhao
 * @date 2019 03 30 23:53
 */
public class Server {

    private int port = 8000;
    private ServerSocket serverSocket;

    public Server() throws IOException {
        serverSocket = new ServerSocket(port, 3);
        System.out.println("服务器启动");
    }

    public void service(){
        while (true) {
            Socket socket = null;
            try {
                socket = serverSocket.accept();
                System.out.println(socket.getInetAddress().getAddress() + ":"+socket.getPort());
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if (socket!=null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server();
        server.service();
    }
}
