package com.silent.socket.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

/**
 * @author xg.zhao
 * @date 2019 03 28 0:15
 */
public class SocketUtils {

    public static final Charset GBK_CHARSET = Charset.forName("GBK");

    public static BufferedReader getReader(Socket socket) throws IOException {
        return new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public static PrintWriter getWriter(Socket socket) throws IOException {
        return new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
    }

    public static String sendAndReceive(String str, BufferedReader reader, PrintWriter writer) throws IOException {
        if (null != str) {
            writer.println(str);
        }
        return reader.readLine();
    }

    /**
     * 处理写就绪事件
     *
     * @param key 事件句柄
     * @author xg.zhao 2019/4/5 16:15
     */
    public static void send(SelectionKey key) throws IOException {
        //        获得与SelectionKey关联的ByteBuffer
        ByteBuffer buffer = (ByteBuffer) key.attachment();
        //        获得与SelectionKey关联的SocketChannel
        SocketChannel socketChannel = (SocketChannel) key.channel();
        //        把极限设为位置,把位置设为0
        buffer.flip();
        //        按照GBK编码,把buffer中的字节转换为字符串
        String data = GBK_CHARSET.decode(buffer).toString();
        //        如果还没有读到一行数据 直接返回
        if (data.indexOf("\r\n") == -1) {
            return;
        }
        //        截取一行数据
        String outputData = data.substring(0, data.indexOf("\n") + 1);
        System.out.println(outputData);
        //        把输出的字符串按照GBK编码 转换为字节 把它放在outputBuffer中
        ByteBuffer outputBuffer = GBK_CHARSET.encode(outputData);
        //        输出outputBuffer中的所有字节
        while (outputBuffer.hasRemaining()) {
            socketChannel.write(outputBuffer);
        }
        //        把outputData字符串按照GBK编码 转换为字节 把它放在ByteBuffer中
        ByteBuffer temp = GBK_CHARSET.encode(outputData);
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
    public static void receive(SelectionKey key) throws IOException {
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
        System.out.println("接收到数据:"+readBuff.toString());
    }

}
