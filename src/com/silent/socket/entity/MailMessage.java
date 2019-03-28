package com.silent.socket.entity;

/**
 * 消息主体
 *
 * @author xg.zhao
 * @date 2019 03 28 22:10
 */
public class MailMessage {

    /**
     * 邮件发送方
     */
    private String from;

    /**
     * 邮件接收方
     */
    private String to;

    /**
     * 邮件标题
     */
    private String subject;

    /**
     * 邮件正文
     */
    private String content;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 密码
     */
    private String password;

    /**
     * 邮件内容,包括邮件标题和正文
     */
    private String data;

    public MailMessage(String from, String to, String subject, String content,String userName,String password) {
        this.from = from;
        this.to = to;
        this.subject = subject;
        this.content = content;
        this.data = "Subject:" + subject + "\r\n" + content;
        this.userName = userName;
        this.password = password;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
