package com.silent.socket.server.mutithread4;

import com.silent.socket.server.Handler;
import com.silent.socket.util.SocketUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 使用JDK自带的线程池
 *
 * @author xg.zhao
 * @date 2019 03 31 13:06
 */
public class EchoServer {

    /**
     * 单个CPU线程池中线程数量
     */
    private final int POOL_SIZE = 4;

    /**
     * 服务器监听的端口
     */
    private int port = 2000;

    private ServerSocket serverSocket;

    private ExecutorService executorService;

    /**
     * 服务器管理端口
     */
    private int portForControl = 8001;

    private ServerSocket serverSocketForControl;

    /**
     * 服务是否关闭
     */
    private boolean isShutdown = false;

    /**
     * 服务器控制线程
     */
    private Thread controlThread = new Thread() {

        @Override
        public void start() {
            this.setDaemon(true);
            super.start();
        }

        @Override
        public void run() {
            while (!isShutdown) {
                Socket controlSocket = null;
                try {
                    controlSocket = serverSocketForControl.accept();
                    BufferedReader reader = SocketUtils.getReader(controlSocket);
                    String command = reader.readLine();
                    System.out.println("接收到指令:" + command);
                    if ("shutdown".equals(command)) {
                        long beginTime = System.currentTimeMillis();
                        PrintWriter writer = SocketUtils.getWriter(controlSocket);
                        writer.write("服务器正在关闭\r\n");
                        System.out.println("服务器正在关闭");
                        isShutdown = true;
                        //                        请求关闭线程池
                        //                        线程池不再接收新的任务,但是会继续执行完工作队列中现有的任务
                        executorService.shutdown();
                        //                        等待关闭线程池,每次等待的超时时间为30s
                        while (!executorService.isTerminated()) {
                            executorService.awaitTermination(30, TimeUnit.SECONDS);
                        }
                        serverSocket.close();
                        long endTime = System.currentTimeMillis();
                        writer.write(("服务器已经关闭,关闭服务器用了:" + (endTime - beginTime) + "毫秒"));
                        System.out.println("服务器已经关闭,关闭服务器用了:" + (endTime - beginTime) + "毫秒");
                        controlSocket.close();
                        serverSocketForControl.close();
                    } else {
                        controlSocket.getOutputStream().write("找不到该指令\r\n".getBytes());
                        controlSocket.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    public EchoServer() throws IOException {
        serverSocket = new ServerSocket(port);
        serverSocket.setSoTimeout(60000);
        serverSocketForControl = new ServerSocket(portForControl);
        //        创建线程池
        //        Runtime的availableProcessors()方法返回当前系统的CPU的数量
        executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * POOL_SIZE);
        //        启动控制线程
        controlThread.start();
        System.out.println("服务器启动");
    }

    public static void main(String[] args) throws IOException {
        new EchoServer().service();
    }

    public void service() {
        while (!isShutdown) {
            Socket socket = null;
            try {
                socket = serverSocket.accept();
                socket.setSoTimeout(60000);
                executorService.execute(new Handler(socket));
                //                为每个连接分配一个线程
                //                Thread workThread = new Thread(new Handler(socket));
                //                workThread.start();
            } catch (SocketTimeoutException e) {

            } catch (RejectedExecutionException e) {
                try {
                    if (socket != null) {
                        socket.close();
                    }
                } catch (IOException e1) {
                    return;
                }
            } catch (SocketException e) {
                //                如果是由于在执行 serverSocket.accept()方法时
                //                serverSocket 被 control线程关闭而导致的异常,就退出service()方法
                if (e.getMessage().indexOf("socket closed") != -1) {
                    return;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
