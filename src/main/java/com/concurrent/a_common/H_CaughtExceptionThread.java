package com.concurrent.a_common;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * @author heshuai
 * @title: H_CaughtExceptionThread
 * @description: 基本线程机制，演示java编程思想中21.2.14节案例
 *              捕获异常，我们都知道如果在串行开发的时候如何捕获异常，那么接下来就是如果处理线程抛出的异常
 * @date 2021年03月06日 12:38
 */
public class H_CaughtExceptionThread {
    public static void main(String []args){
        ExecutorService exec= Executors.newCachedThreadPool(new HandlerThreadFactory());
        exec.execute(new ExceptionThread2());
    }


    static class ExceptionThread2 implements Runnable{

        @Override
        public void run(){
            Thread t= Thread.currentThread();
            System.out.println("run() by "+t);
            System.out.println("eh= "+t.getUncaughtExceptionHandler());
            throw new RuntimeException();
        }
    }

    /**
     * 继承未捕获异常处理器
     * t:发生异常的线程
     * e：所发生的异常
     */
    static class MyUncaughtException implements Thread.UncaughtExceptionHandler{
        @Override
        public void uncaughtException(Thread t,Throwable e){
            System.out.println("caught： "+e );
        }
    }

    /**
     * 工厂类，生成可以捕获异常的Thread工厂类
     */
    static class HandlerThreadFactory implements ThreadFactory {

        @Override
        public Thread newThread(Runnable r){
            System.out.println(this+" creating new Thread ");
            Thread thread=new Thread(r);
            System.out.println("created "+thread);
//            t.setUncaughtExceptionHandler(new MyUncaughtException());
            // lambda表达式
            thread.setUncaughtExceptionHandler((Thread t,Throwable e) -> {
                System.out.println("caught： "+e );
            });
            System.out.println("en = "+thread.getUncaughtExceptionHandler());
            return thread;
        }
    }
}
