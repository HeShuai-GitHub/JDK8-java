package com.concurrent.d_threadCooperation;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author heshuai
 * @title: C_AwaitAndSignalAll
 * @description: 本线程机制，演示java编程思想中21.5.3节案例
 *                使用显式Lock和Condition来重写汽车打蜡抛光的仿真程序
 *                使用显式的Lock和Condition肯定是更加灵活，但是对于这个案例来说，并没有太大的区别，反而增加了复杂性，
 *                因此Lock和Condition对象只有在更加困难的多线程问题中才是必须的
 * @date 2021年03月20日 23:35
 */
public class C_AwaitAndSignalAll {

    static class Car {

        private Lock lock = new ReentrantLock();
        // 和Object中的wait、notify、notifyAll一样，如果需要操作await()、signal()、signalAll(),必须先获得锁
        private Condition condition = lock.newCondition();

        private boolean waxOn = false;

        // 打蜡
        public void waxed(){
            lock.lock();
            // 每个对Lock()的调用都必须紧跟一个try-catch子句，用来保证在任何情况下都可以释放锁。
            try {
                this.waxOn = true;
                condition.notifyAll();
            }finally {
                lock.unlock();
            }
        }
        // 抛光
        public void buffed() {
            lock.lock();
            try {
                this.waxOn = false;
                condition.notifyAll();
            }finally {
                lock.unlock();
            }
        }
        // 等待打蜡执行完毕
        public void waitForWaxing() throws InterruptedException {
            lock.lock();
            try {
                while (this.waxOn == false) {
                    // 必须先获得锁才能对锁进行操作
                    condition.await();
                }
            }finally {
                lock.unlock();
            }
        }
        // 等待抛光执行完毕
        public void waitForBuffing() throws InterruptedException {
            lock.lock();
            try {
                while (this.waxOn == true) {
                    // 必须先获得锁才能对锁进行操作
                    condition.await();
                }
            }finally {
                lock.unlock();
            }
        }
    }
    // 打蜡流程
    static class WaxOn implements Runnable {

        private A_WaitAndNotifyAll.Car car;

        public WaxOn(A_WaitAndNotifyAll.Car car) {
            this.car = car;
        }

        @Override
        public void run() {
            try {
                while (!Thread.currentThread().isInterrupted()){
                    System.out.println("打蜡...");
                    TimeUnit.MILLISECONDS.sleep(200); // 模拟打蜡过程
                    car.waxed();
                    car.waitForBuffing();
                }
            }catch (InterruptedException e) {
                System.out.println("WaxOn 被中断");
            }
            System.out.println("WaxOn 执行完毕");
        }
    }
    // 抛光流程
    static class WaxOff implements Runnable {
        private A_WaitAndNotifyAll.Car car;

        public WaxOff(A_WaitAndNotifyAll.Car car) {
            this.car = car;
        }

        @Override
        public void run() {
            try {
                while (!Thread.currentThread().isInterrupted()){
                    car.waitForWaxing();
                    System.out.println("抛光...");
                    TimeUnit.MILLISECONDS.sleep(200); // 模拟抛光过程
                    car.buffed();
                }
            }catch (InterruptedException e) {
                System.out.println("WaxOff 被中断");
            }
            System.out.println("WaxOff 执行完毕");
        }
    }

    public static void main(String[] args) throws InterruptedException {
        A_WaitAndNotifyAll.Car car = new A_WaitAndNotifyAll.Car();
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.execute(new A_WaitAndNotifyAll.WaxOn(car));
        executor.execute(new A_WaitAndNotifyAll.WaxOff(car));
        TimeUnit.SECONDS.sleep(6);
        executor.shutdownNow();
    }
}
