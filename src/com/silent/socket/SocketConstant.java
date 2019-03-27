package com.silent.socket;

/**
 * @author xg.zhao
 * @date 2019 03 28 0:25
 */
public class SocketConstant {

    /**
     * 自然结束
     */
    public static final int NATURAL_STOP = 1;

    /**
     * 突然中止程序
     */
    public static final int SUDDEN_STOP = 2;

    /**
     * 关闭socket,再结束程序
     */
    public static final int SOCKET_STOP = 3;

    /**
     * 关闭输出流,再结束程序
     */
    public static final int OUTPUT_STOP = 4;

    /**
     * 关闭serversocket 并中止程序
     */
    public static final int SERVERSOCKET_STOP = 5;
}
