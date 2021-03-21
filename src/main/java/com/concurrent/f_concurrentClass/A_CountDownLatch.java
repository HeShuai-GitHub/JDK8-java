package com.concurrent.f_concurrentClass;

import java.rmi.ServerError;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author heshuai
 * @title: A_CountDownLatch
 * @description: 本线程机制，演示java编程思想中21.7.1节案例
 *              CountDownLatch被用来同步一个或多个任务，强制他们等待由其他任务执行的“一组“操作完成
 * @date 2021年03月21日 16:02
 */
public class A_CountDownLatch {

    static class TaskPortion implements Runnable{
        private static int count = 0;
        private final int id = count++;
        private static Random random = new Random(47);
        private final CountDownLatch latch;

        public TaskPortion(CountDownLatch latch) {
            this.latch = latch;
        }

        @Override
        public void run() {
            try {
                doWork();
                // 将CountDownLatch初始化的count值依次递减，如果count==0，那么所有等待的线程将被唤醒，再次调用这个方法将什么也不会发生。
                latch.countDown();
            } catch (InterruptedException e) {
                System.err.println("TaskPortion 被中断");
            }
        }

        private void doWork() throws InterruptedException {
            TimeUnit.MILLISECONDS.sleep(random.nextInt(2000));
            System.out.println(this+"已完成");
        }

        @Override
        public String toString() {
            return "TaskPortion{" +
                    "id=" + id +
                    ", latch=" + latch +
                    '}';
        }
    }

    static class WaitingTask implements Runnable{
        private static int count = 0;
        private final int id = count++;
        private final CountDownLatch latch;

        public WaitingTask(CountDownLatch latch) {
            this.latch = latch;
        }

        @Override
        public void run() {
            try {
                // 阻塞线程，直到count变成0，如果本来就为0，那么将不再阻塞
                latch.await();
                System.out.println("在此之前一组操作已完成，现在完成最后操作："+this);
            } catch (InterruptedException e) {
                System.err.println("WaitingTask 被中断");
            }
        }

        @Override
        public String toString() {
            return "WaitingTask{" +
                    "id=" + id +
                    ", latch=" + latch +
                    '}';
        }
    }

    public static void main(String[] args) {
        int size = 100;
        ExecutorService service = Executors.newCachedThreadPool();
        CountDownLatch latch = new CountDownLatch(size);
        for (int i=0; i<10;i++){
            service.execute(new WaitingTask(latch));
        }
        for (int i=0; i<size;i++){
            service.execute(new TaskPortion(latch));
        }
        System.out.println("全部任务已经就绪......");
        service.shutdown();
    }
}
