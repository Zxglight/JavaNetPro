package com.silent.socket.server;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * @author xg.zhao
 * @date 2019 03 27 22:25
 */
public class SimpleServer {

    public static void main(String[] args) throws IOException, InterruptedException {
//        创建一个socket服务 监听端口 8000 队列长度为2
        ServerSocket serverSocket = new ServerSocket(8000,2);
//        休眠10s
        Thread.sleep(10000);
    }
}
