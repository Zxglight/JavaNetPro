package com.silent.socket.thread;

import java.util.LinkedList;

/**
 * @author xg.zhao
 * @date 2019 03 31 13:20
 */
public class ThreadPool extends ThreadGroup {

    /**
     * 线程池id
     */
    private static int threadPoolId;

    /**
     * 表示线程池关闭状态
     */
    private boolean isClosed = false;

    /**
     * 工作队列
     */
    private LinkedList<Runnable> workQueue;

    /**
     * 线程id
     */
    private int threadId;

    /**
     * 创建线程池
     *
     * @param poolSize 指定线程池中的工作线程数量
     * @author xg.zhao 2019/3/31 13:29
     */
    public ThreadPool(int poolSize) {
        super("ThreadPool-" + (threadPoolId++));
        setDaemon(true);
        //        创建工作队列
        workQueue = new LinkedList<>();
        for (int i = 0; i < poolSize; i++) {
            //            创建并启动工作线程
            new WorkThread().start();
        }
    }

    /**
     * Constructs a new thread group. The parent of this new group is the thread group of the currently running thread.
     * <p>
     * The <code>checkAccess</code> method of the parent thread group is called with no arguments; this may result in a security exception.
     *
     * @param name the name of the new thread group.
     * @throws SecurityException if the current thread cannot create a thread in the specified thread group.
     * @see ThreadGroup#checkAccess()
     * @since JDK1.0
     */
    public ThreadPool(String name) {
        super(name);
    }

    /**
     * Creates a new thread group. The parent of this new group is the specified thread group.
     * <p>
     * The <code>checkAccess</code> method of the parent thread group is called with no arguments; this may result in a security exception.
     *
     * @param parent the parent thread group.
     * @param name the name of the new thread group.
     * @throws NullPointerException if the thread group argument is
     * <code>null</code>.
     * @throws SecurityException if the current thread cannot create a thread in the specified thread group.
     * @see SecurityException
     * @see ThreadGroup#checkAccess()
     * @since JDK1.0
     */
    public ThreadPool(ThreadGroup parent, String name) {
        super(parent, name);
    }

    /**
     * 向工作队列中加入一个新任务,由工作线程去执行该任务
     *
     * @param task 被加入的任务
     * @author xg.zhao 2019/3/31 13:31
     */
    public synchronized void execute(Runnable task) {
        //        如果线程池被关闭则抛出异常
        if (isClosed) {
            throw new IllegalStateException();
        }
        if (task != null) {
            workQueue.add(task);
            //            唤醒getTask()等待任务的工作线程
            notify();
        }
    }

    protected synchronized Runnable getTask() throws InterruptedException {
        while (workQueue.size() == 0) {
            if (isClosed) {
                return null;
            }
            wait();
        }
        return workQueue.removeFirst();
    }

    /**
     * 立即关闭线程池 并清空线程队列
     *
     * @author xg.zhao 2019/3/31 13:43
     */
    public synchronized void close() {
        if (!isClosed) {
            isClosed = true;
            //            清空工作队列
            workQueue.clear();
            //            中断所有的工作线程
            interrupt();
        }
    }

    /**
     * 等待工作线程把所有任务执行完
     *
     * @author xg.zhao 2019/3/31 13:37
     */
    public void join() {
        synchronized (this) {
            isClosed = true;
            //        唤醒还在getTask()方法中等待任务的工作线程
            notifyAll();
        }
        Thread[] threads = new Thread[activeCount()];
        //        enumerate()方法继承自ThreadGroup类,获得线程组中当前所有活着的工作线程
        int count = enumerate(threads);
        for (int i = 0; i < count; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private class WorkThread extends Thread {

        /**
         * Allocates a new {@code Thread} object. This constructor has the same effect as {@linkplain #Thread(ThreadGroup, Runnable, String) Thread}
         * {@code (null, null, gname)}, where {@code gname} is a newly generated name. Automatically generated names are of the form {@code
         * "Thread-"+}<i>n</i>, where <i>n</i> is an integer.
         */
        public WorkThread() {
            //            加入到当前ThreadPool线程组中
            super(ThreadPool.this, "WorkThread-" + (threadId++));
        }

        @Override
        public void run() {
            //            isInterrupted()方法继承自Thread类 判断线程是否被中断
            while (!isInterrupted()) {
                Runnable task = null;
                try {
                    task = getTask();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //                如果getTask()返回null或者线程执行getTask()时被中断,则结束此线程
                if (task == null) {
                    return;
                }
                //                运行任务
                try {
                    task.run();
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }
    }
}

