package com.silent.socket.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * @author xg.zhao
 * @date 2019 03 28 0:15
 */
public class SocketUtils {


    public static BufferedReader getReader(Socket socket) throws IOException {
        return new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public static PrintWriter getWriter(Socket socket) throws IOException {
        return new PrintWriter(new OutputStreamWriter(socket.getOutputStream()),true);
    }

    public static String sendAndReceive(String str, BufferedReader reader, PrintWriter writer) throws IOException {
        if (null != str) {
            writer.println(str);
        }
        return reader.readLine();
    }

}
