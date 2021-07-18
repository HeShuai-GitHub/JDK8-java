package com.io.bio.socket;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.*;

/**
 * @author heshuai
 * @title: CurrentBioTest
 * @description: 利用线程池实现并发BIO模型
 * @date 2021年07月18日 21:07
 */
public class ConcurrentBioServerTest {

    private static ExecutorService executorService = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors()*2, Runtime.getRuntime().availableProcessors()*5,
            3, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());

    public static void main(String[] args) throws IOException {
        try (ServerSocket server = new ServerSocket()) {
            // 监听端口
            server.bind(new InetSocketAddress(InetAddress.getLocalHost(), 8090));
            while (true) {
                Socket socket = server.accept();
                executorService.submit(new SimpleSocketRunnable(socket));
            }
        }
    }

}
