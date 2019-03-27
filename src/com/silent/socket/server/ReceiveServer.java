package com.silent.socket.server;

import static java.lang.System.out;

import com.silent.socket.SocketUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * @author xg.zhao
 * @date 2019 03 28 0:14
 */
public class ReceiveServer {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8000);
        Socket accept = serverSocket.accept();
        //        设置接收数据超时时间 ms
        accept.setSoTimeout(2000);
        BufferedReader reader = SocketUtils.getReader(accept);
        String s;
        try {
            while ((s = reader.readLine()) != null) {
                out.println(s);
            }
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            out.println("socket 等待超时");
        } finally {
            serverSocket.close();
        }
    }
}
