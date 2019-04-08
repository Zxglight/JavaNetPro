package com.silent.socket.channel.thread2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * @author xg.zhao
 * @date 2019 04 08 20:39
 */
public class PingClient {

    /**
     * 控制connector线程
     */
    boolean shutdown = false;

    private Selector selector;

    /**
     * 存放用户新提交的任务
     */
    private LinkedList targets = new LinkedList();

    /**
     * 存放已经完成的需要打印的任务
     */
    private LinkedList finishedTargets = new LinkedList();

    public PingClient() throws IOException {
        selector = Selector.open();
        Connector connector = new Connector();
        Printer printer = new Printer();
        connector.start();
        printer.start();
        receiveTarget();
    }

    public static void main(String[] args) throws IOException {
        new PingClient();
    }

    public void addTarget(Target target) {
        //        向targets队列中加入一个任务 主线程会调用该方法
        SocketChannel socketChannel = null;
        try {
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
            socketChannel.connect(target.address);
            target.channel = socketChannel;
            target.connectStart = System.currentTimeMillis();
            synchronized (targets) {
                targets.add(target);
            }
            selector.wakeup();
        } catch (IOException e) {
            e.printStackTrace();
            if (null != socketChannel) {
                try {
                    socketChannel.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                target.failure = e;
                addFinishedTarget(target);
            }
        }
    }

    public void addFinishedTarget(Target target) {
        //        向finishedTargets队列中加入一个任务 主线程和Connector线程会调用该方法
        synchronized (finishedTargets) {
            finishedTargets.notify();
            finishedTargets.add(target);
        }
    }

    public void printFinishedTargets() {
        //        打印finishedTargets队列中的任务 Printer线程会调用该方法
        try {
            while (true) {
                Target target = null;
                synchronized (finishedTargets) {
                    while (0 == finishedTargets.size()) {
                        finishedTargets.wait();
                    }
                    target = (Target) finishedTargets.removeFirst();
                }
                target.show();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void registerTargets() {
        //        取出targets队列中的任务 向Selector注册链接就绪事件 Connector线程会调用该方法
        synchronized (targets) {
            while (targets.size() > 0) {
                Target target = (Target) targets.removeFirst();
                try {
                    target.channel.register(selector, SelectionKey.OP_CONNECT, target);
                } catch (ClosedChannelException e) {
                    e.printStackTrace();
                    try {
                        target.channel.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    target.failure = e;
                    addFinishedTarget(target);
                }
            }
        }
    }

    public void processSelectedKeys() throws IOException {
        //        处理连接就绪事件 connector线程会调用该方法
        for (Iterator<SelectionKey> it = selector.selectedKeys().iterator(); it.hasNext(); ) {
            SelectionKey selectionKey = it.next();
            it.remove();
            Target target = (Target) selectionKey.attachment();
            SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
            try {
                if (socketChannel.finishConnect()) {
                    selectionKey.cancel();
                    target.connectFinish = System.currentTimeMillis();
                    socketChannel.close();
                    addFinishedTarget(target);
                }
            } catch (IOException e) {
                e.printStackTrace();
                socketChannel.close();
                target.failure = e;
                addFinishedTarget(target);
            }
        }
    }

    public void receiveTarget() {
        //        接收用户输入的域名 向targets队列中加入任务 主线程会调用该方法
        try {
            BufferedReader localReader = new BufferedReader(new InputStreamReader(System.in));
            String msg = null;
            while ((msg = localReader.readLine()) != null) {
                if (!"bye".equals(msg)) {
                    Target target = new Target(msg);
                    addTarget(target);
                } else {
                    shutdown = true;
                    //                    使Connector线程从Selector的select()方法中退出
                    selector.wakeup();
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class Printer extends Thread {

        /**
         * Allocates a new {@code Thread} object. This constructor has the same effect as {@linkplain #Thread(ThreadGroup, Runnable, String) Thread}
         * {@code (null, null, gname)}, where {@code gname} is a newly generated name. Automatically generated names are of the form {@code
         * "Thread-"+}<i>n</i>, where <i>n</i> is an integer.
         */
        public Printer() {
            //            设置为后台线程
            setDaemon(true);
        }

        /**
         * If this thread was constructed using a separate
         * <code>Runnable</code> run object, then that
         * <code>Runnable</code> object's <code>run</code> method is called;
         * otherwise, this method does nothing and returns.
         * <p>
         * Subclasses of <code>Thread</code> should override this method.
         *
         * @see #start()
         * @see #stop()
         * @see #Thread(ThreadGroup, Runnable, String)
         */
        @Override
        public void run() {
            printFinishedTargets();
        }
    }

    public class Connector extends Thread {

        /**
         * If this thread was constructed using a separate
         * <code>Runnable</code> run object, then that
         * <code>Runnable</code> object's <code>run</code> method is called;
         * otherwise, this method does nothing and returns.
         * <p>
         * Subclasses of <code>Thread</code> should override this method.
         *
         * @see #start()
         * @see #stop()
         * @see #Thread(ThreadGroup, Runnable, String)
         */
        @Override
        public void run() {
            while (!shutdown) {
                try {
                    registerTargets();
                    if (selector.select() > 0) {
                        processSelectedKeys();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                selector.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

/**
 * 表示一项任务
 *
 * @author xg.zhao 2019/4/8 20:39
 */
class Target {

    InetSocketAddress address;

    SocketChannel channel;

    Exception failure;

    /**
     * 开始连接时的时间
     */
    long connectStart;

    /**
     * 连接成功的时间
     */
    long connectFinish = 0;

    /**
     * 该任务是否已经打印
     */
    boolean shown = false;

    public Target(String host) {
        try {
            address = new InetSocketAddress(InetAddress.getByName(host), 80);
        } catch (UnknownHostException e) {
            failure = e;
        }
    }

    void show() {
        String result;
        if (connectFinish != 0) {
            result = Long.toString(connectFinish - connectFinish) + "ms";
        } else if (null != failure) {
            result = failure.toString();
        } else {
            result = "Timed out";
        }
        System.out.println(address + " : " + result);
        shown = true;
    }
}
