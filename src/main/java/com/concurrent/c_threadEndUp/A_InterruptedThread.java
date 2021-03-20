package com.concurrent.c_threadEndUp;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author heshuai
 * @title: A_InterruptedThread
 * @description: 本线程机制，演示java编程思想中21.4.3节案例
 *               线程分为四种状态，分别是new、runnable、blocked、dead，那么进行blocked有四种途径，分别是sleep阻塞、wait挂起、I/O阻塞、mutex资源阻塞
 *               这个类主要显示在四种阻塞状态下，如果中断Thread
 * @date 2021年03月17日 23:48
 */
public class A_InterruptedThread {

    static class SleepBlocked implements Runnable {

        @Override
        public void run() {
            try {
                TimeUnit.SECONDS.sleep(100);
            }catch (InterruptedException e){
                e.printStackTrace();
                System.err.println("程序被打断");
            }
            System.out.println("SleepBlocked 执行完毕");
        }
    }

    static class IOBlocked implements Runnable {

        private InputStream in;

        public IOBlocked(InputStream inn) {
            this.in = inn;
        }

        @Override
        public void run() {
            try {
                System.out.println("等待读：");
                in.read();
            }catch (IOException e){
                if (Thread.currentThread().isInterrupted()){
                    System.err.println("IOBlocked程序被打断");
                }else{
                    e.printStackTrace();
                }
            }
            System.out.println("IOBlocked 执行完毕");
        }
    }

    static class SynchronizedBlocked implements Runnable {

        public synchronized void f() {
            // 忙等待中，永远也不释放锁
            while (true)
                Thread.yield();
        }

        public SynchronizedBlocked() {
            new Thread(()->{
                // 开启一个线程锁住该对象
                    f();
            }).start();
        }

        @Override
        public void run() {
            System.out.println("尝试获取对象锁...");
            f();
            System.out.println("SynchronizedBlocked 执行完毕");
        }
    }

    static class ReentrantLockBlocked implements Runnable {

        private Lock lock = new ReentrantLock();

        public void f() throws InterruptedException {
            // 尝试获取锁，直到这个线程被中断
            lock.lockInterruptibly();
        }

        public ReentrantLockBlocked() {
            new Thread(()->{
                // 锁住当前对象
                lock.lock();
            }).start();
        }

        @Override
        public void run() {
            System.out.println("尝试获取对象锁...");
            try {
                f();
            } catch (InterruptedException e) {
                System.out.println("ReentrantLockBlocked 被中断");
            }
            System.out.println("ReentrantLockBlocked 执行完毕");
        }
    }

    private static ExecutorService exec = Executors.newCachedThreadPool();

    private static void test(Runnable r) throws InterruptedException {
        // 获得线程上下文
        Future<?> f = exec.submit(r);
        TimeUnit.MILLISECONDS.sleep(10);
        System.out.println("将要打断线程："+r.getClass().getName());
        // 中断指定线程，这个是中断单个线程的方式
        f.cancel(true);
        System.out.println("Interrupt 已经发送"+r.getClass().getName());
    }

    private static void testNormal() throws InterruptedException {
        test(new SleepBlocked());
        test(new IOBlocked(System.in)); // 不可被中断
        test(new SynchronizedBlocked()); // 不可被中断
        System.out.println("将要退出系统");
        System.exit(0);
    }

    private static void testIO() throws IOException, InterruptedException {
        ServerSocket socket1 = new ServerSocket(8080);
        ServerSocket socket2 = new ServerSocket(8081);
        InputStream socketInput1 = new Socket("localhost",8080).getInputStream();
        InputStream socketInput2 = new Socket("localhost",8081).getInputStream();
        exec.execute(new IOBlocked(socketInput1));
        exec.execute(new IOBlocked(socketInput2));
        TimeUnit.MILLISECONDS.sleep(100);
        System.out.println("中断由exec管理的所有的线程");
        exec.shutdownNow();
        TimeUnit.SECONDS.sleep(1);
        System.out.println("将关闭。。socketInput1资源");
        socketInput1.close(); // 关闭I/O资源来释放阻塞线程
        TimeUnit.SECONDS.sleep(1);
        System.out.println("将关闭。。socketInput2资源");
        socketInput2.close(); // 释放阻塞线程
    }

    public static void testReentrantLock() throws InterruptedException {
        exec.execute(new ReentrantLockBlocked());
        TimeUnit.SECONDS.sleep(1);
        System.out.println("将要中断ReentrantLockBlocked");
        exec.shutdownNow();
        System.out.println("中断ReentrantLockBlocked完成");
    }

    public static void main(String[] args) throws Exception {
//        testNormal(); // 测试正  常情况下，三种阻塞下响应Interrupted
//        testIO(); // 测试关闭IO资源以 释放阻塞线程
        testReentrantLock(); // 测试互斥状态下，中断阻塞线程
    }
}
