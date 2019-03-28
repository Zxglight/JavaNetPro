package com.silent.socket.server;

import static com.silent.socket.constant.SocketConstant.NATURAL_STOP;
import static com.silent.socket.constant.SocketConstant.OUTPUT_STOP;
import static com.silent.socket.constant.SocketConstant.SERVERSOCKET_STOP;
import static com.silent.socket.constant.SocketConstant.SOCKET_STOP;
import static com.silent.socket.constant.SocketConstant.SUDDEN_STOP;
import static com.silent.socket.util.SocketUtils.getReader;
import static java.lang.System.out;

import java.io.BufferedReader;
import java.io.IOException;
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

    private String host = "localhost";

    private int port = 8000;

    private ServerSocket serverSocket;

    public Receiver() throws IOException {
        serverSocket = new ServerSocket(port);
        out.println("服务器已经启动");
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        new Receiver().receive();
    }

    public void receive() throws IOException, InterruptedException {
        Socket socket = serverSocket.accept();
        BufferedReader reader = getReader(socket);
        for (int i = 0; i < 20; i++) {
            String s = reader.readLine();
            out.println("receive:" + s);
            Thread.sleep(1000);
            if (i == 2) {
                switch (stopWay) {
                    case SUDDEN_STOP:
                        out.println("突然终止连接!");
                        System.exit(0);
                        break;
                    case SOCKET_STOP:
                        out.println("关闭socket并终止程序!");
                        socket.close();
                        break;
                    case OUTPUT_STOP:
                        socket.shutdownOutput();
                        out.println("关闭输出流并终止程序!");
                        break;
                    case SERVERSOCKET_STOP:
                        serverSocket.close();
                        out.println("关闭server socket并终止程序");
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
