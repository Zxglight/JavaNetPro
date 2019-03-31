package com.silent.socket.server.mutithread3;

import com.silent.socket.server.Handler;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 使用JDK自带的线程池
 *
 * @author xg.zhao
 * @date 2019 03 31 13:06
 */
public class EchoServer {

    private final int POOL_SIZE = 4;

    private int port = 2000;

    private ServerSocket serverSocket;

    private ExecutorService executorService;

    public EchoServer() throws IOException {
        serverSocket = new ServerSocket(port);
        //        创建线程池
        //        Runtime的availableProcessors()方法返回当前系统的CPU的数量
        executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * POOL_SIZE);
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
                executorService.execute(new Handler(socket));
                //                为每个连接分配一个线程
                //                Thread workThread = new Thread(new Handler(socket));
                //                workThread.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
