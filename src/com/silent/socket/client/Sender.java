package com.silent.socket.client;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * 消息发送方 测试发送与接收消息
 *
 * @author xg.zhao
 * @date 2019 03 27 23:18
 */
public class Sender {

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

    private String host = "localhost";

    private int port = 8000;

    private Socket socket;

    public Sender() throws IOException {
        this.socket = new Socket(host, port);
    }

    public static void main(String[] args) throws Exception {
        new Sender().send();
    }

    PrintWriter getWriter(Socket socket) throws IOException {
        return new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    public void send() throws Exception {
        PrintWriter writer = getWriter(socket);
        for (int i = 0; i < 20; i++) {
            String msg = "hello_" + i + "\r\n";
            writer.write(msg);
            System.out.println("send:" + msg);
            Thread.sleep(500);
            if (i == 15) {
                switch (stopWay) {
                    case SUDDEN_STOP:
                        System.out.println("突然中止连接!");
                        System.exit(0);
                        break;
                    case OUTPUT_STOP:
                        socket.shutdownOutput();
                        System.out.println("关闭输出流并中止程序!");
                        break;
                    case SOCKET_STOP:
                        System.out.println("关闭socket并中止程序!");
                    case NATURAL_STOP:
                    default:
                        writer.flush();
                        socket.close();
                        break;
                }
            }
        }
    }
}
