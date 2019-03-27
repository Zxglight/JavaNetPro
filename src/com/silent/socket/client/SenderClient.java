package com.silent.socket.client;

import com.silent.socket.SocketUtils;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * @author xg.zhao
 * @date 2019 03 28 0:19
 */
public class SenderClient {

    public static void main(String[] args) throws IOException, InterruptedException {
        Socket socket = new Socket("localhost", 8000);
        PrintWriter writer = SocketUtils.getWriter(socket);
        writer.write("hello");
        writer.write("everyone");
        Thread.sleep(60000);
        socket.close();
    }
}
