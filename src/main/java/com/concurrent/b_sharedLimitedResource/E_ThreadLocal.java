package com.concurrent.b_sharedLimitedResource;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author heshuai
 * @title: E_ThreadLocal
 * @description: 基本线程机制，演示java编程思想中21.3.7节案例
 *  *            线程本地变量，第二种解决资源冲突问题的方案——根除对于变量的共享
 * @date 2021年03月17日 22:29
 */
public class E_ThreadLocal {

    public static void main(String[] args) throws InterruptedException {
        ExecutorService exec = Executors.newCachedThreadPool();
        for(int i=0; i<5; i++){
            exec.execute(new Accessor(String.valueOf(i)));
        }
        TimeUnit.SECONDS.sleep(1);
        exec.shutdownNow();
    }

    static class Accessor implements Runnable {

        private String id;

        public Accessor(String idn){
            this.id = idn;
        }

        @Override
        public String toString() {
            return "Accessor{" +
                    "id='" + id + '\'' +
                    '}'+"+++++++"+ThreadLocalVariableHolder.get();
        }

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()){
                ThreadLocalVariableHolder.increment();
                System.out.println(this);
                Thread.yield();
            }
        }
    }

    static class ThreadLocalVariableHolder {
        // 通常ThreadLocal对象当作静态域来存储
        private static ThreadLocal<Integer> value = new ThreadLocal<Integer>(){
            private Random random = new Random(47);
            protected Integer initialValue() {
                return random.nextInt(50000);
            }
        };
        // 此方法非synchronized，但是因为value为线程一级的变量，那么是可以保障不会出现竞争条件的。
        public static void increment(){
            ThreadLocalVariableHolder.value.set(ThreadLocalVariableHolder.value.get()+1);
        }

        public static Integer get(){
            return ThreadLocalVariableHolder.value.get();
        }

        public static void main(String[] args) {
            System.out.println(value.get());
            value.remove();
            System.out.println(value.get());
        }
    }
}
