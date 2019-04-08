package com.silent.socket.channel.thread2;

import static com.silent.socket.util.SocketUtils.receive;
import static com.silent.socket.util.SocketUtils.send;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * 阻塞模式和非阻塞模式的混合使用
 *
 * @author xg.zhao
 * @date 2019 04 07 11:43
 */
public class EchoServer {

    private Selector selector;

    private ServerSocketChannel serverSocketChannel;

    private int port = 8000;

    private Object gate = new Object();

    public EchoServer() throws IOException {
        selector = Selector.open();
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().setReuseAddress(true);
        serverSocketChannel.socket().bind(new InetSocketAddress(port));
        System.out.println("服务器启动");
    }

    public static void main(String[] args) throws IOException {
        EchoServer echoServer = new EchoServer();
        //        开启一个线程用来接收客户连接
        Thread accept = new Thread(() -> echoServer.accept());
        accept.start();
        //        启动服务
        echoServer.service();
    }

    /**
     * 接收客户连接
     */
    public void accept() {
        for (; ; ) {
            try {
                //                接收客户连接
                SocketChannel socketChannel = serverSocketChannel.accept();
                //                设置为非阻塞模式
                socketChannel.configureBlocking(false);
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                synchronized (gate) {
                    selector.wakeup();
                    socketChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE, buffer);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 服务 用来处理数据读写
     */
    public void service() throws IOException {
        for (; ; ) {
            synchronized (gate) {
                int n = selector.select();
                if (n == 0) {
                    continue;
                }
                Set<SelectionKey> readyKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = readyKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = null;
                    try {
                        key = iterator.next();
                        iterator.remove();
                        if (key.isReadable()) {
                            receive(key);
                        }
                        if (key.isWritable()) {
                            send(key);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        try {
                            if (key != null) {
                                key.cancel();
                                key.channel().close();
                            }
                        } catch (Exception ex) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}
