package com.silent.socket.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 消息接收者
 *
 * @author xg.zhao
 * @date 2019 03 27 23:38
 */
public class Receiver {

    /**
     * 结束通信的方式
     */
    private static int stopWay = 1;

    /**
     * 自然结束
     */
    private final int NATURAL_STOP = 1;

    /**
     * 突然中止程序
     */
    private final int SUDDEN_STOP = 2;

    /**
     * 关闭socket,再结束程序
     */
    private final int SOCKET_STOP = 3;

    /**
     * 关闭输出流,再结束程序
     */
    private final int OUTPUT_STOP = 4;

    /**
     * 关闭serversocket 并中止程序
     */
    private final int SERVERSOCKET_STOP = 5;

    private String host = "localhost";

    private int port = 8000;

    private ServerSocket serverSocket;

    public Receiver() throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("服务器已经启动");
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        new Receiver().receive();
    }

    private BufferedReader getReader(Socket socket) throws IOException {
        return new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public void receive() throws IOException, InterruptedException {
        Socket socket = serverSocket.accept();
        BufferedReader reader = getReader(socket);
        for (int i = 0; i < 20; i++) {
            String s = reader.readLine();
            System.out.println("receive:" + s);
            Thread.sleep(1000);
            if (i == 2) {
                switch (stopWay) {
                    case SUDDEN_STOP:
                        System.out.println("突然终止连接!");
                        System.exit(0);
                        break;
                    case SOCKET_STOP:
                        System.out.println("关闭socket并终止程序!");
                        socket.close();
                        break;
                    case OUTPUT_STOP:
                        socket.shutdownOutput();
                        System.out.println("关闭输出流并终止程序!");
                        break;
                    case SERVERSOCKET_STOP:
                        serverSocket.close();
                        System.out.println("关闭server socket并终止程序");
                        break;
                    case NATURAL_STOP:
                    default:
                        socket.close();
                        break;
                }
            }
        }
    }
}
