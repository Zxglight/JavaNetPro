package com.silent.socket.channel.thread1;

import static com.silent.socket.util.SocketUtils.receive;
import static com.silent.socket.util.SocketUtils.send;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

/**
 * 使用非阻塞模式重写echo server
 * @author xg.zhao
 * @date 2019 04 05 14:07
 */
public class EchoServer {

    private ServerSocketChannel serverSocketChannel;

    private Selector selector;

    private int port = 8080;

    private Charset charset = Charset.forName("GBK");


    public EchoServer() throws IOException {
        //        创建Selector对象
        selector = Selector.open();
        //        创建一个ServerSocketChannel对象
        serverSocketChannel = ServerSocketChannel.open();
        //        使得在同一个主机上关闭了服务器程序,紧接着再启动该服务器程序时,可以顺利绑定到相同的端口
        serverSocketChannel.socket().setReuseAddress(true);
        //        是ServerSo工作于非阻塞模式
        serverSocketChannel.configureBlocking(false);
        //        把服务器进程与一个本地端口绑定
        serverSocketChannel.socket().bind(new InetSocketAddress(port));
    }

    public static void main(String[] args) throws IOException {
        new EchoServer().service();
    }

    public void service() throws IOException {
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        while (selector.select() > 0) {
            //            获得Selector的selected-keys集合
            Set<SelectionKey> readyKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = readyKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = null;
                try {
                    //                处理SelectionKey
                    key = iterator.next();
                    iterator.remove();
                    if (key.isAcceptable()) {
                        //                    处理接收连接就绪事件
                        //                        获得与SelectionKey关联的ServerSocketChannel
                        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
                        //                        获得与客户连接的SocketChannel
                        SocketChannel socketChannel = serverSocketChannel.accept();
                        System.out.println("接收到客户连接,来自:" + socketChannel.socket().getInetAddress() + ":" + socketChannel.socket().getPort());
                        //                        把SocketChannel设置为非阻塞模式
                        socketChannel.configureBlocking(false);
                        //                        创建一个用于存放用户发送来的数据的缓冲区
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        //                        SocketChannel项Selector注册读就绪事件和写就绪事件
                        socketChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE, buffer);
                    }
                    if (key.isReadable()) {
                        //                    处理读就绪事件
                        receive(key);
                    }
                    if (key.isWritable()) {
                        //                    处理写就绪事件
                        send(key);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    if (null != key) {
                        key.cancel();
                        key.channel().close();
                    }
                }
            }
        }
    }


}
