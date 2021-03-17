package com.concurrent.a_common;

import java.util.concurrent.*;

/**
 * @program: JDK8-java
 * @description: 基本线程机制，演示java编程思想中21.2.3节案例
 *                在前面都是通过Thread进行线程创建，但是实际使用中呢，肯定不是这样的。我们需要对Thread进行封装，封装就产生了Executor这个线程管理器
 *                Executor允许你管理异步任务（Thread）的执行，这其中有三个主要的方法newCachedThreadPool()、newFixedThreadPool()、newSingleThreadExecutor(),
 *                虽然上诉这三个方法可以基本满足Thread使用的要求，但是在实际工作是不推荐使用这三个方法，而是使用手动方式去创建线程池，使用下面这个方式
 *                new ThreadPoolExecutor(10,20,200L, TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>(),new ThreadPoolExecutor.AbortPolicy());
 * 注意：在任何线程池中，现有线程在可能的情况下，都会被自动复用
 * @author: hs
 * @create: 2021-01-11 22:03
 **/
public class C_Executor {

    static class Inner_Cached{
        /**
         * 动态创建线程，Executors.newCachedThreadPool()将为每一个任务都创建一个线程。
         */
        public static void main(String []arg){
            ExecutorService exec= Executors.newCachedThreadPool();
            for(int i=0;i<5;i++){
                exec.execute(new A_RunTask());
            }
            // 直译，就是当这个方法被调用后，不可以再开始新的线程（不可以执行execute()方法)，将在现有线程执行完后有序地关闭
            exec.shutdown();
            System.out.printf("\nmain线程结束\n");
        }
    }

    static class Inner_Fixed{
        /**
         * 获取固定数量的线程数,Executors.newFixedThreadPool(5)将一次性执行完指定数量的线程分配。
         *
         * @param arg
         */
        public static void main(String []arg){
            ExecutorService exec= Executors.newFixedThreadPool(5);
            for(int i=0;i<5;i++){
                exec.execute(new A_RunTask());
            }
            exec.shutdown();
            System.out.printf("\nmain线程结束\n");
        }
    }

    static class Inner_Single{
        /**
         * 序列化线程，Executors.newSingleThreadExecutor()箱式线程数量为1的Executors.newFixedThreadPool(1)，只会创建一个线程
         *
         * @param arg
         */
        public static void main(String []arg){
            ExecutorService exec= Executors.newSingleThreadExecutor();
            for(int i=0;i<5;i++){
                exec.execute(new A_RunTask());
            }
            exec.shutdown();
            System.out.printf("\nmain线程结束\n");
        }
    }

    static class Inner_Customized{
        /**
         * 以上三种创建线程的方式，单一、可变、定长都有一定问题，
         * 原因是FixedThreadPool和SingleThreadExecutor底层
         * 都是用LinkedBlockingQueue实现的，这个队列最大长度为Integer.MAX_VALUE，容易导致OOM。
         * OOM： out of memory，内存超出
         *  所以一般情况下都会采用自定义线程池的方式来定义
         * @param args
         */
        public static void main(String[] args) {
            /**
             * 1、corePoolSize线程池的核心线程数
             * 2、maximumPoolSize能容纳的最大线程数
             * 3、keepAliveTime空闲线程存活时间
             * 4、unit 存活的时间单位
             * 5、workQueue 存放提交但未执行任务的队列
             * 6、threadFactory 创建线程的工厂类
             * 7、handler 等待队列满后的拒绝策略
             */
            ExecutorService executor = new ThreadPoolExecutor(10,20,200L, TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>(),new ThreadPoolExecutor.AbortPolicy());
            for(int i=0;i<5;i++){
                executor.execute(new A_RunTask());
            }
            executor.shutdown();
            System.out.printf("\nmain线程结束\n");
        }
    }

}
