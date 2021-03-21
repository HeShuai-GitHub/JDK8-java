package com.concurrent.d_threadCooperation;

import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.sql.Time;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author heshuai
 * @title: E_Pipe_WriterAndReader
 * @description: 本线程机制，演示java编程思想中21.5.5节案例
 *              简单介绍PipedReader、PipedWriter的使用
 * @date 2021年03月21日 12:05
 */
public class E_Pipe_WriterAndReader {

    static class Sender implements Runnable{

        private Random random = new Random(47);
        private PipedWriter writer = new PipedWriter();

        public synchronized PipedWriter getWriter() {
            return writer;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    for (char c = 'A'; c<='Z';c++){
                        writer.write(c);
                        TimeUnit.MILLISECONDS.sleep(random.nextInt(500));
                    }
                }
            } catch (InterruptedException e) {
                System.out.println("Sender 被中断");
            } catch (IOException e) {
                System.err.println("Sender 发生异常");
            }
        }
    }

    static class Receiver implements Runnable{

        private PipedReader reader;

        public Receiver(Sender sender) throws IOException {
            // 指定在哪个stream中读取
            this.reader = new PipedReader(sender.getWriter());
        }

        @Override
        public void run() {
            try {
                while (true){
//                   若读取不到数据则自动阻塞,在阻塞时不会占有锁，I/O阻塞状态下依然占有锁
                    System.out.println("收到："+(char)reader.read());
                }
            } catch (IOException e) {
                System.err.println("发生异常：Receiver");
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Sender sender = new Sender();
        Receiver receiver = new Receiver(sender);
        ExecutorService service = Executors.newCachedThreadPool();
        service.execute(sender);
        service.execute(receiver);
        TimeUnit.SECONDS.sleep(3);
        service.shutdownNow();
    }
}