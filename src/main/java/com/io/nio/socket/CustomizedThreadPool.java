package com.io.nio.socket;

import java.util.concurrent.*;

/**
 * @author heshuai
 * @title: CustomizedThreadPool
 * @description: 自定义线程池
 * @date 2021年07月17日 10:13
 */
public class CustomizedThreadPool {

    private final static ExecutorService SOCKED_THREAD_POOL = new  ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(),Runtime.getRuntime().availableProcessors()*2,500, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(), Executors.defaultThreadFactory(),new ThreadPoolExecutor.AbortPolicy());

    private final static ExecutorService READ_WRITE_THREAD_POOL = new  ThreadPoolExecutor(16,32,500, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(), Executors.defaultThreadFactory(),new ThreadPoolExecutor.AbortPolicy());

    public static void sockedHandlerSubmit(Runnable task) {
        SOCKED_THREAD_POOL.submit(task);
    }

    public static void writeHandlerSubmit(Runnable task) {
        READ_WRITE_THREAD_POOL.submit(task);
    }

    public static void shutdown() {
        // 先关闭端口监听，不在接受请求
        SOCKED_THREAD_POOL.shutdown();
        // 处理完剩下所有请求后关闭
        READ_WRITE_THREAD_POOL.shutdown();
    }

}
