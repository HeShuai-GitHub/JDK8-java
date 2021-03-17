package com.concurrent.a_common;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * @author heshuai
 * @title: E_DaemonThread
 * @description: 基本线程机制，演示java编程思想中21.2.8节案例
 *                这里提到了后台（daemon)线程，也称之为服务线程，它是做什么的呢？在这种类型的线程中主要是做一些辅助性工作的，比如开启一个服务线程去时刻检查一个类的状态。
 *                它有几个特性：1、它由setDaemon(true)来设置后台线程，所有由后台线程创建的线程都默认是后台线程；
 *                           2、当所有非后台线程结束后，进程会直接停止，这里不会有序的去关闭后台线程，而是采用直接强制关闭的方式，所以当我们在后台线程的finally语句块中进行一些操作的
 *                           时候，会因为进程的关闭而不会执行到
 * @date 2021年01月30日 23:09
 */
public class E_DaemonThread {

    /*
    public static void main(String[] args) {
        for (int i=0;i<5;++i){
            Thread daemon=new Thread(new SimpleDaemons());
            daemon.setDaemon(true);
            daemon.start();
        }
        System.out.println("全部后台线程已启动");
        try {
            TimeUnit.MILLISECONDS.sleep(9);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
*/

    public static void main(String[] args) {
        /**
         * Executors 自1.5之后开始使用，可以创建ExecutorService、ScheduledExecutorService、ThreadFactory（默认一种的内部实现方式）、Callable
         * Executors.newCachedThreadPool();
         *          创建线程池对象，线程池即创建若干线程，若线程结束则不会全部杀死，而是保留一部分，当有新需要线程的时候就会直接从池子从拿出已有的线程来使用，这样就不会浪费反复创建线程资源了
         *          这个方法在这个工具类中一共有两个重载方法，若不设置ThreadFactory，则使用默认的线程工厂实现方式（Executors.defaultThreadFactory()），当然也可以和下面一样，使用自己自定义的线程工厂实现方式
         *          其他几种创建线程池的方法基本一样。不过阿里手册推荐使用自定义去创建线程池的方法（ new ThreadPoolExecutor()），当然这个方法内部也是这样使用的
         */
        ExecutorService es= Executors.newCachedThreadPool(new DaemonThreadFactory());
        for (int i=0; i<5;++i){
            // 执行
            es.execute(new SimpleDaemons());
        }
        System.out.println("全部后台线程已启动");
        try {
            TimeUnit.MILLISECONDS.sleep(9);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 自定义一个线程工厂，实现ThreadFactory
     *      在ThreadFactory只有一个方法newThread()，用来创建新的线程，当然这里也就是对创建线程的几个参数进行限定，如：priority、name、daemon status等（还有一个线程组，这个在java编程思想一书中提到在jdk5之后就没有用到了）
     *
     *      在这个自定义ThreadFactory中，设置所有新Thread都是后台线程
     */
    static class DaemonThreadFactory implements ThreadFactory {

        @Override
        public Thread newThread(Runnable r) {
            Thread t=new Thread(r);
            // 定义线程为后台线程
            t.setDaemon(true);
            return t;
        }
    }

    static class SimpleDaemons implements Runnable{

        @Override
        public void run(){
            try {
                System.out.println(Thread.currentThread()+"："+this);
                //isDaemon()判断是否为后台线程
                System.out.println("是否是后台线程："+Thread.currentThread().isDaemon());
                TimeUnit.MILLISECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                //当非后台线程全部终止的时候，jvm会关闭所有的后台进程，并不会执行到finally，直接强硬关闭
                System.out.println("后台线程将关闭");
            }
        }
    }
}
