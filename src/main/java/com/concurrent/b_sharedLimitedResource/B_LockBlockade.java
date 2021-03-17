package com.concurrent.b_sharedLimitedResource;

import org.junit.Test;

import java.sql.Time;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author heshuai
 * @title: B_LockBlockade
 * @description: 基本线程机制，演示java编程思想中21.3.3节案例
 *               显式使用Lock对象创建互斥机制，这个和synchronized功能差不多，但是用法不同
 * @date 2021年03月06日 18:50
 */
public class B_LockBlockade {
    private Lock lock = new ReentrantLock();
    private static Lock lockStatic = new ReentrantLock();



    public static void main(String[] args) throws InterruptedException {
        testObjectLock();
//        testClassLock();
    }

    private static void testClassLock() throws InterruptedException {
        System.out.println("类级锁");
        B_LockBlockade.untimedClass();
        B_LockBlockade.timedClass();
        new Thread(){
            {setDaemon(true);}
            public void run(){
                System.out.println(B_LockBlockade.lockStatic.tryLock());
                B_LockBlockade.lockStatic.lock();
                System.out.println("B_LockBlockade类已加锁");
            }
        }.start();
        Thread.yield();
        TimeUnit.SECONDS.sleep(2);
        B_LockBlockade.untimedClass();
        B_LockBlockade.timedClass();
        B_LockBlockade.unlockClass();
        // 执行对象级别的互斥方法，可以获取
        new B_LockBlockade().timedObject();
    }

    private static void testObjectLock() throws InterruptedException {
        System.out.println("对象级锁");
        final B_LockBlockade blockade = new B_LockBlockade();
        blockade.untimedObject();
        blockade.timedObject();
        new Thread(){
            {setDaemon(true);}
            public void run(){
                System.out.println(blockade.lock.tryLock());
                blockade.lock.lock();
                System.out.println("B_LockBlockade对象以加锁");
            }
        }.start();
        Thread.yield();
        TimeUnit.SECONDS.sleep(2);
        blockade.untimedObject();
        blockade.timedObject();
        blockade.unlockObject();
        // 执行类级别的互斥资源，可以执行
        B_LockBlockade.untimedClass();
    }

    public static void unlockClass(){
        System.out.println("执行非互斥资源");
    }

    public void unlockObject(){
        System.out.println("执行非互斥资源");
    }


    public void untimedObject() {
//        lock.lock(); // 获得锁
        // 获取锁，若获取到则返回true，否则返回false
        if(lock.tryLock()) {
            try {
                System.out.println("已获得锁，可以对互斥资源进行操作");
                // return 必须在try中，以确保在释放锁之前所有的操作已经执行完毕，其他线程不会强占数据
                return;
            }finally {
                // 释放锁，类似于synchronized，加锁几次就需要释放锁几次
                lock.unlock();
            }
        }else{
            System.out.println("当前所需资源已被其他资源占用，可以先进行其他操作，然后在进行尝试获取锁");
        }
    }

    public void timedObject(){
//        lock.lock(); // 获得锁
        try {
            /**
             * 等待特定时间去获取线程，若期间内可以获取则返回true并且获取到锁，否则false
             */
            if(lock.tryLock(2,TimeUnit.SECONDS)) {
                try {
                    System.out.println("已获得锁，可以对互斥资源进行操作");
                    return;
                }finally {
                    // 释放锁
                    lock.unlock();
                }
            }else{
                System.out.println("当前所需资源已被其他资源占用，可以先进行其他操作，然后在进行尝试获取锁");
            }
        } catch (InterruptedException e) {
            System.out.println("等待期间发生中断异常");
        }
    }

    public static void untimedClass() {
//        lock.lock(); // 获得锁
        // 获取锁，若获取到则返回true，否则返回false
        if(lockStatic.tryLock()) {
            try {
                System.out.println("已获得锁，可以对互斥资源进行操作");
                // return 必须在try中，以确保在释放锁之前所有的操作已经执行完毕，其他线程不会强占数据
                return;
            }finally {
                // 释放锁，类似于synchronized，加锁几次就需要释放锁几次
                lockStatic.unlock();
            }
        }else{
            System.out.println("当前所需资源已被其他资源占用，可以先进行其他操作，然后在进行尝试获取锁");
        }
    }

    public static void timedClass(){
//        lock.lock(); // 获得锁
        try {
            /**
             * 等待特定时间去获取线程，若期间内可以获取则返回true并且获取到锁，否则false
             */
            if(lockStatic.tryLock(2,TimeUnit.SECONDS)) {
                try {
                    System.out.println("已获得锁，可以对互斥资源进行操作");
                    return;
                }finally {
                    // 释放锁
                    lockStatic.unlock();
                }
            }else{
                System.out.println("当前所需资源已被其他资源占用，可以先进行其他操作，然后在进行尝试获取锁");
            }
        } catch (InterruptedException e) {
            System.out.println("等待期间发生中断异常");
        }
    }
}
