package com.concurrent.d_threadCooperation;

import com.sun.org.apache.xpath.internal.WhitespaceStrippingElementMatcher;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author heshuai
 * @title: A_WaitAndNotifyAll
 * @description: 本线程机制，演示java编程思想中21.5.1节案例
 *              在这里通过一个对汽车打蜡抛光的仿真程序来阐述线程互相协作的过程
 * @date 2021年03月20日 17:35
 */
public class A_WaitAndNotifyAll {

    static class Car {
        private boolean waxOn = false;

        // 打蜡
        public synchronized void waxed(){
            this.waxOn = true;
            notifyAll();
        }
        // 抛光
        public synchronized void buffed() {
            this.waxOn = false;
            notifyAll();
        }
        // 等待打蜡执行完毕
        public synchronized void waitForWaxing() throws InterruptedException {
            while (this.waxOn == false) {
                wait();
            }
        }
        // 等待抛光执行完毕
        public synchronized void waitForBuffing() throws InterruptedException {
            while (this.waxOn == true) {
                wait();
            }
        }
    }
    // 打蜡流程
    static class WaxOn implements Runnable {

        private Car car;

        public WaxOn(Car car) {
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
        private Car car;

        public WaxOff(Car car) {
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
        Car car = new Car();
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.execute(new WaxOn(car));
        executor.execute(new WaxOff(car));
        TimeUnit.SECONDS.sleep(6);
        executor.shutdownNow();
    }
}
