package com.silent.socket.client;

import static com.silent.socket.SocketConstant.NATURAL_STOP;
import static com.silent.socket.SocketConstant.OUTPUT_STOP;
import static com.silent.socket.SocketConstant.SOCKET_STOP;
import static com.silent.socket.SocketConstant.SUDDEN_STOP;
import static com.silent.socket.SocketUtils.getWriter;
import static java.lang.System.exit;
import static java.lang.System.out;

import java.io.IOException;
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

    private String host = "localhost";

    private int port = 8000;

    private Socket socket;

    public Sender() throws IOException {
        this.socket = new Socket(host, port);
    }

    public static void main(String[] args) throws Exception {
        new Sender().send();
    }

    public void send() throws Exception {
        socket.setTcpNoDelay(true);
        PrintWriter writer = getWriter(socket);
        for (int i = 0; i < 20; i++) {
            String msg = "hello_" + i + "\r\n";
            writer.write(msg);
            out.println("send:" + msg);
            Thread.sleep(500);
            if (i == 15) {
                switch (stopWay) {
                    case SUDDEN_STOP:
                        out.println("突然中止连接!");
                        exit(0);
                        break;
                    case OUTPUT_STOP:
                        socket.shutdownOutput();
                        out.println("关闭输出流并中止程序!");
                        break;
                    case SOCKET_STOP:
                        out.println("关闭socket并中止程序!");
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
