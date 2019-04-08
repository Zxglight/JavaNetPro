package com.silent.socket.channel.thread2;

import static com.silent.socket.util.SocketUtils.GBK_CHARSET;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * 非阻塞的client
 *
 * @author xg.zhao
 * @date 2019 04 08 19:18
 */
public class EchoClient {

    private SocketChannel socketChannel;

    private ByteBuffer sendBuffer = ByteBuffer.allocate(1024);

    private ByteBuffer receiveBuffer = ByteBuffer.allocate(1024);

    private Selector selector;

    public EchoClient() throws IOException {
        socketChannel = SocketChannel.open();
        InetAddress ia = InetAddress.getLocalHost();
        InetSocketAddress inetSocketAddress = new InetSocketAddress(ia, 8000);
        //        默认为阻塞模式连接服务器
        socketChannel.connect(inetSocketAddress);
        //        设置为非阻塞模式
        socketChannel.configureBlocking(false);
        System.out.println("与服务器连接建立成功");
        selector = Selector.open();
    }

    public static void main(String[] args) throws IOException {
        final EchoClient client = new EchoClient();
        Thread receiver = new Thread(() -> client.receiveFromUser());
        receiver.start();
        client.talk();
    }

    /**
     * 接收用户从控制台输入的数据 把它放到sendBuffer中
     *
     * @author xg.zhao 2019/4/8 19:44
     */
    public void receiveFromUser() {
        try {
            BufferedReader localReader = new BufferedReader(new InputStreamReader(System.in));
            String msg = null;
            while ((msg = localReader.readLine()) != null) {
                synchronized (sendBuffer) {
                    sendBuffer.put(GBK_CHARSET.encode(msg + "\r\n"));
                }
                if ("bye".equals(msg)) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 接收和发送数据
     *
     * @author xg.zhao 2019/4/8 19:44
     */
    public void talk() throws IOException {
        socketChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
        while (selector.select() > 0) {
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
                    if (key != null) {
                        key.cancel();
                        key.channel().close();
                    }
                }
            }
        }
    }

    public void send(SelectionKey key) throws IOException {
        //        发送sendBuffer中的数据
        SocketChannel socketChannel = (SocketChannel) key.channel();
        synchronized (sendBuffer) {
            //            把极限设置为位置,把位置设为零
            sendBuffer.flip();
            //            发送数据
            socketChannel.write(sendBuffer);
            //            删除已经发送的数据
            sendBuffer.compact();
        }
    }

    public void receive(SelectionKey key) throws IOException {
        //        接收EchoServer发送的数据 把它放到receiveBuffer中
        //        如果receiveBuffer中有一行数据 就打印这行数据 然后把它从receiveBuffer中删除
        SocketChannel socketChannel = (SocketChannel) key.channel();
        socketChannel.read(receiveBuffer);
        receiveBuffer.flip();
        String receiveDate = GBK_CHARSET.decode(receiveBuffer).toString();
        int index = receiveDate.indexOf("\n");
        if (index != -1) {
            return;
        }
        String outputData = receiveDate.substring(0, index + 1);
        System.out.println(outputData);
        if ("bye\r\n".equals(outputData)) {
            key.cancel();
            socketChannel.close();
            System.out.println("关闭与服务器的连接");
            selector.close();
            //            结束程序
            System.exit(0);
        }
        ByteBuffer temp = GBK_CHARSET.encode(outputData);
        receiveBuffer.position(temp.limit());
        receiveBuffer.compact();
    }


}
