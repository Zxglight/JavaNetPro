package com.silent.socket.constant;

/**
 * @author xg.zhao
 * @date 2019 03 28 0:25
 */
public interface SocketConstant {

    /**
     * 自然结束
     */
    int NATURAL_STOP = 1;

    /**
     * 突然中止程序
     */
    int SUDDEN_STOP = 2;

    /**
     * 关闭socket,再结束程序
     */
    int SOCKET_STOP = 3;

    /**
     * 关闭输出流,再结束程序
     */
    int OUTPUT_STOP = 4;

    /**
     * 关闭serversocket 并中止程序
     */
    int SERVERSOCKET_STOP = 5;
}
