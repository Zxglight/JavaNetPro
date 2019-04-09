package com.silent.socket.http;

import com.silent.socket.server.Handler;
import com.silent.socket.util.SocketUtils;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 实现一个简单的HTTP服务器
 * @author xg.zhao
 * @date 2019 04 09 22:45
 */
public class SimpleHttpServer {


    private int port = 80;
    private ServerSocketChannel serverSocketChannel = null;

    private ExecutorService executorService;

    private static final int POOL_MUTIPLE = 4;

    public SimpleHttpServer() throws IOException {

        executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * POOL_MUTIPLE);

        serverSocketChannel = ServerSocketChannel.open();

        serverSocketChannel.socket().setReuseAddress(true);
        serverSocketChannel.socket().bind(new InetSocketAddress(port));
        System.out.println("server start success");
    }

    public void service(){
        while (true) {
            SocketChannel socketChannel = null;
            try {
                socketChannel = serverSocketChannel.accept();
                executorService.execute(new Handler(socketChannel));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        new SimpleHttpServer().service();
    }

    class Handler implements Runnable{

        private SocketChannel socketChannel;

        public Handler(SocketChannel socketChannel) {
            this.socketChannel = socketChannel;
        }


        /**
         * When an object implementing interface <code>Runnable</code> is used to create a thread, starting the thread causes the object's
         * <code>run</code> method to be called in that separately executing
         * thread.
         * <p>
         * The general contract of the method <code>run</code> is that it may take any action whatsoever.
         *
         * @see Thread#run()
         */
        @Override
        public void run() {
            handler(socketChannel);
        }

        public void handler(SocketChannel socketChannel) {
            try {
                Socket socket = socketChannel.socket();
                System.out.println("reception connect success");
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                socketChannel.read(buffer);
                buffer.flip();
                String request = SocketUtils.GBK_CHARSET.decode(buffer).toString();
                System.out.println(request);
//                generate success response
                StringBuffer sb = new StringBuffer("HTTP/1.1 200 OK\r\n");
                sb.append("Content-Type:text/html\r\n");
                socketChannel.write(SocketUtils.GBK_CHARSET.encode(sb.toString()));
                FileInputStream in;
//                get request first line
                String firstLineOfRequest = request.substring(0, request.indexOf("\r\n"));
                if (firstLineOfRequest.indexOf("login.html") != -1) {
                    in = new FileInputStream("login.html");
                } else {
                    in = new FileInputStream("hello.html");
                }
                FileChannel fileChannel = in.getChannel();
//                send response content
                fileChannel.transferTo(0, fileChannel.size(), socketChannel);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (socketChannel!=null) {
                    try {
                        socketChannel.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
