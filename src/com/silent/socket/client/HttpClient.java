package com.silent.socket.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

/**
 * 通过socket 发送http请求
 *
 * @author xg.zhao
 * @date 2019 03 27 22:45
 */
public class HttpClient {

    String host = "www.cnblogs.com";

    int port = 80;

    Socket socket;

    public static void main(String[] args) throws IOException {
        HttpClient client = new HttpClient();
        client.createSocket();
        client.communicate();
    }

    public void createSocket() throws IOException {
        this.socket = new Socket(host, port);
    }

    public void communicate() throws IOException {
        StringBuffer sb = new StringBuffer("GET " + "/bigwang1126/p/10600397.html" + " HTTP/1.1\r\n");
        sb.append("Host: www.cnblogs.com\r\n");
        sb.append("Accept: */*\r\n");
        sb.append("Accept-Language: zh-cn\r\n");
        sb.append("Accept-Encoding: gzip,deflate\r\n");
        sb.append("User-Agent: Mozilla/4.0(compatible;MSIE 6.0;Windows NT 5.0)\r\n");
        sb.append("Connection: Keep-Alive\r\n\r\n");

        //        发送http请求
        OutputStream socketOut = socket.getOutputStream();
        socketOut.write(sb.toString().getBytes());
        socket.shutdownOutput();

        //        接收响应结果
        InputStream socketIn = socket.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socketIn));
        String str;
        while ((str = bufferedReader.readLine()) != null) {
            System.out.println(str);
        }
        socket.close();
    }
}
