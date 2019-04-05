package com.silent.socket.channel;

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

    /**
     * 处理写就绪事件
     *
     * @param key 事件句柄
     * @author xg.zhao 2019/4/5 16:15
     */
    private void send(SelectionKey key) throws IOException {
        //        获得与SelectionKey关联的ByteBuffer
        ByteBuffer buffer = (ByteBuffer) key.attachment();
        //        获得与SelectionKey关联的SocketChannel
        SocketChannel socketChannel = (SocketChannel) key.channel();
        //        把极限设为位置,把位置设为0
        buffer.flip();
        //        按照GBK编码,把buffer中的字节转换为字符串
        String data = charset.decode(buffer).toString();
        //        如果还没有读到一行数据 直接返回
        if (data.indexOf("\r\n") == -1) {
            return;
        }
        //        截取一行数据
        String outputData = data.substring(0, data.indexOf("\n") + 1);
        System.out.println(outputData);
        //        把输出的字符串按照GBK编码 转换为字节 把它放在outputBuffer中
        ByteBuffer outputBuffer = charset.encode(outputData);
        //        输出outputBuffer中的所有字节
        while (outputBuffer.hasRemaining()) {
            socketChannel.write(outputBuffer);
        }
        //        把outputData字符串按照GBK编码 转换为字节 把它放在ByteBuffer中
        ByteBuffer temp = charset.encode(outputData);
        //        把buffer的位置设为temp的极限
        buffer.position(temp.limit());
        //        删除buffer中已经处理的数据
        buffer.compact();
        //        如果已经输出了字符串"bye\r\n" 就使SelectionKey失效 并关闭 SocketChannel
        if (outputData.equals("bye\r\n")) {
            key.cancel();
            socketChannel.close();
            System.out.println("关闭与客户的连接");
        }
    }

    /**
     * 读就绪事件处理
     *
     * @param key 事件句柄
     * @author xg.zhao 2019/4/5 16:14
     */
    public void receive(SelectionKey key) throws IOException {
        //        获得与SelectionKey关联的附件
        ByteBuffer byteBuffer = (ByteBuffer) key.attachment();
        //        获得与SelectionKey关联的SocketCh
        SocketChannel socketChannel = (SocketChannel) key.channel();
        //        创建一个ByteBuffer 用于存放读到的数据
        ByteBuffer readBuff = ByteBuffer.allocate(32);
        socketChannel.read(readBuff);
        readBuff.flip();
        //        把byteBuffer的极限设为容量
        byteBuffer.limit(byteBuffer.capacity());
        //        把readBuff中的内容拷贝到buffer中 假定buffer的容量足够大 不会出现缓冲区溢出的异常
        byteBuffer.put(readBuff);
    }


}
