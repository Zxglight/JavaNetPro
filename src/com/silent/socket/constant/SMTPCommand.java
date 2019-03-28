package com.silent.socket.constant;

/**
 * SMTP 指令
 *
 * @author xg.zhao
 * @date 2019 03 28 22:23
 */
public interface SMTPCommand {

    /**
     * 指明邮件发送者的主机地址
     */
    String HELO = "HELO";

    String EHLO = "EHLO";

    /**
     * 指明邮件发送者的邮件地址
     */
    String MAIL_FROM = "MAIL FROM";

    /**
     * 指明邮件接收者的邮件地址
     */
    String RCPT_TO = "RCPT TO";

    /**
     * 发送的邮件内容
     */
    String DATA = "DATA";

    /**
     * 结束通信
     */
    String QUIT = "QUIT";

    /**
     * 查询服务器支持的命令
     */
    String HELP = "HELP";

    /**
     * 用户认证
     */
    String AUTH_LOGIN = "AUTH LOGIN";


}
