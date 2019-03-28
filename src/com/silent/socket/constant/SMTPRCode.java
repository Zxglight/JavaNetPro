package com.silent.socket.constant;

/**
 * SMTP 应答码
 *
 * @author xg.zhao
 * @date 2019 03 28 22:27
 */
public interface SMTPRCode {

    /**
     * 帮助信息
     */
    int HELP = 214;

    /**
     * 服务就绪
     */
    int SERVICE_READY = 220;

    /**
     * 服务关闭
     */
    int SERVICE_CLOSE = 221;

    /**
     * 邮件操作完成
     */
    int OPERATION_COMPLETE = 250;

    /**
     * 开始输入邮件内容,以"."结束
     */
    int START_TYPEING = 354;

    /**
     * 服务未就绪,关闭传输通道
     */
    int SNR_CLOSE = 421;

    /**
     * 命令参数格式错误
     */
    int PARAM_FORMAT_ERR = 501;

    /**
     * 命令不支持
     */
    int COMMAND_NOT_SUPPORT = 502;

    /**
     * 错误的命令序列
     */
    int BAD_COMMAND_SEQUENCE = 503;

    /**
     * 命令参数不支持
     */
    int PARAM_NOT_SUPPORT = 504;

}
