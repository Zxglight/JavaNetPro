package com.silent.socket.test;

import com.silent.socket.thread.ThreadPool;

/**
 * @author xg.zhao
 * @date 2019 03 31 13:47
 */
public class ThreadPoolTester {

    /**
     * 任务数量
     */
    private static int numTasks = 50;

    /**
     * 线程数量
     */
    private static int poolSize = 10;

    public static void main(String[] args) {
        ThreadPool threadPool = new ThreadPool(poolSize);
//        运行任务
        for (int i = 0; i < numTasks; i++) {
            threadPool.execute(createTask(i));
        }
        threadPool.join();
    }

    private static Runnable createTask(final int taskId) {
        return ()->{
            System.out.println("task " + taskId + ": start");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                System.out.println("Task "+taskId+": end");
            }
        };
    }

}
