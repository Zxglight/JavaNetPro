package com.silent.socket.client;

import java.io.IOException;
import java.net.Socket;

/**
 * @author xg.zhao
 * @date 2019 03 27 22:27
 */
public class SimpleClient {

    public static void main(String[] args) throws IOException {
        String localhost = "localhost";
        Socket socket1 = new Socket(localhost, 8000);
        System.out.println("socket1 连接成功");
        Socket socket2 = new Socket(localhost, 8000);
        System.out.println("socket2 连接成功");
        Socket socket3 = new Socket(localhost, 8000);
        System.out.println("socket3 连接成功");
    }
}
