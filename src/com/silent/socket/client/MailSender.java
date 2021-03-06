package com.silent.socket.client;

import static com.silent.socket.constant.SMTPCommand.AUTH_LOGIN;
import static com.silent.socket.constant.SMTPCommand.DATA;
import static com.silent.socket.constant.SMTPCommand.HELO;
import static com.silent.socket.constant.SMTPCommand.MAIL_FROM;
import static com.silent.socket.constant.SMTPCommand.QUIT;
import static com.silent.socket.constant.SMTPCommand.RCPT_TO;
import static com.silent.socket.util.SocketUtils.getReader;
import static com.silent.socket.util.SocketUtils.getWriter;
import static com.silent.socket.util.SocketUtils.sendAndReceive;
import static java.lang.System.out;

import com.silent.socket.entity.MailMessage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import sun.misc.BASE64Encoder;

/**
 * 邮件发送者
 *
 * @author xg.zhao
 * @date 2019 03 28 22:08
 */
public class MailSender {

    private String smtpServer = "smtp.mxhichina.com";

    private int port = 25;

    public static void main(String[] args) {
        MailMessage msg = new MailMessage("username", "password", "hello", "hi,I'm tom", "username", "password");
        new MailSender().sendMail(msg);
    }

    public void sendMail(MailMessage message) {
        Socket socket = null;
        try {
            socket = new Socket(smtpServer, port);
            BufferedReader reader = getReader(socket);
            PrintWriter writer = getWriter(socket);
            String username = new BASE64Encoder().encode(message.getUserName().getBytes());
            String password = new BASE64Encoder().encode(message.getPassword().getBytes());
            out.println("username:" + username);
            out.println("password:" + password);
            //            本地主机名
            String hostName = InetAddress.getLocalHost().getHostAddress();
            //            测试响应数据
            out.println(sendAndReceive(null, reader, writer));
            //            发送主机名
            out.println(sendAndReceive(HELO + " " + hostName, reader, writer));
            //            准备验证用户信息
            out.println(sendAndReceive(AUTH_LOGIN, reader, writer));
            //            输入用户名
            out.println(sendAndReceive(username, reader, writer));
            //            输入密码
            out.println(sendAndReceive(password, reader, writer));
            //            发送发件人邮箱
            out.println(sendAndReceive(MAIL_FROM + ":<" + message.getFrom() + ">", reader, writer));
            //            发送收件人邮箱
            out.println(sendAndReceive(RCPT_TO + ":<" + message.getTo() + ">", reader, writer));
            //            准备发送邮件内容
            out.println(sendAndReceive(DATA, reader, writer));
            //            发送邮件内容
            writer.println(message.getData());
            //            邮件发送完毕
            out.println(sendAndReceive(".", reader, writer));
            //            结束通信
            out.println(sendAndReceive(QUIT, reader, writer));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
