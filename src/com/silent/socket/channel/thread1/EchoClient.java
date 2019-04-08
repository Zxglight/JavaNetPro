package com.silent.socket.channel.thread1;

import com.silent.socket.util.SocketUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

/**
 * 阻塞的client
 *
 * @author xg.zhao
 * @date 2019 04 07 14:24
 */
public class EchoClient {

    private SocketChannel socketChannel;

    public static void main(String[] args) throws IOException {
        new EchoClient().talk();
    }

    public EchoClient() throws IOException {
        socketChannel = SocketChannel.open();
        InetAddress localHost = InetAddress.getLocalHost();
        InetSocketAddress inetSocketAddress = new InetSocketAddress(localHost, 8000);
        //        连接服务器
        socketChannel.connect(inetSocketAddress);
        System.out.println("与服务器的连接建立成功");
    }

    public void talk() {
        try {
            BufferedReader reader = SocketUtils.getReader(socketChannel.socket());
            PrintWriter writer = SocketUtils.getWriter(socketChannel.socket());
            BufferedReader localReader = new BufferedReader(new InputStreamReader(System.in));
            String msg = null;
            while ((msg = localReader.readLine()) != null) {
                writer.println(msg);
                System.out.println(reader.readLine());
                if ("bye".equals(msg)) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socketChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
