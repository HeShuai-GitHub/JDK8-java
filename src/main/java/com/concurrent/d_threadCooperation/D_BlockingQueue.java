package com.concurrent.d_threadCooperation;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author heshuai
 * @title: D_BlockingQueue
 * @description: 本线程机制，演示java编程思想中21.5.4节案例
 *              通过一个制作吐司的过程来描述使用BlockingQueue的方式
 * @date 2021年03月21日 0:22
 */
public class D_BlockingQueue {

    // 烤面包的实体
    static class Toast {
        public enum Status {
            DRY, BUTTERED, JAMMED
        }

        private Status status = Status.DRY;
        private final int id;

        public Toast(int id) {
            this.id = id;
        }
        public void butter() {
            status = Status.BUTTERED;
        }

        public void jam() {
            status = Status.JAMMED;
        }

        public Status getStatus() {
            return this.status;
        }
        public int getId(){
            return id;
        }

        @Override
        public String toString() {
            return "Toast{" +
                    "status=" + status +
                    ", id=" + id +
                    '}';
        }
    }
    // 继承自LinkedBlockingQueue，指定类型
    static class ToastQueue extends LinkedBlockingQueue<Toast>{}
//    开始制作吐司，并将它放入延迟队列中
    static class Toaster implements Runnable{

        private ToastQueue toasts;

        private int count =0;
        private Random random = new Random(47);

        public Toaster(ToastQueue toasts) {
            this.toasts = toasts;
        }

        @Override
        public void run() {
            try {
                while (!Thread.interrupted()){
                    TimeUnit.MILLISECONDS.sleep(100+random.nextInt(500));
                    Toast t=new Toast(count++);
                    System.out.println(t);
                    toasts.put(t); // 添加到LinkedBlockingQueue中，如果存在挂起的线程则立刻唤醒
                }
            }catch (InterruptedException e){
                System.out.println("Toaster被中断");
            }
            System.out.println("Toaster执行完毕");
        }
    }
//  将吐司涂上黄油，并将它放入butterQueue队列中
    static class Butter implements Runnable{

        private ToastQueue dryQueue,butterQueue;

        public Butter(ToastQueue dryQueue,ToastQueue butterQueue) {
            this.dryQueue = dryQueue;
            this.butterQueue = butterQueue;
        }

        @Override
        public void run() {
            try {
                while (!Thread.interrupted()){
                    // queue中无元素，则等待获取到元素。若有则在queue的head取出移除该元素。先进先出
                    Toast t=dryQueue.take();
                    t.butter();
                    System.out.println(t);
                    butterQueue.put(t);
                }
            }catch (InterruptedException e){
                System.out.println("Butter被中断");
            }
            System.out.println("Butter执行完毕");
        }
    }
//  将吐司堵上果酱，并将它放入finishedQueue队列中
    static class Jammer implements Runnable{

        private ToastQueue butterQueue,finishedQueue;

        public Jammer(ToastQueue butterQueue,ToastQueue finishedQueue) {
            this.finishedQueue = finishedQueue;
            this.butterQueue = butterQueue;
        }

        @Override
        public void run() {
            try {
                while (!Thread.interrupted()){
                    // queue中无元素，则等待获取到元素。若有则在queue的head取出移除该元素。先进先出
                    Toast t=butterQueue.take();
                    t.jam();
                    System.out.println(t);
                    finishedQueue.put(t);
                }
            }catch (InterruptedException e){
                System.out.println("Jammer被中断");
            }
            System.out.println("Jammer执行完毕");
        }
    }
//  将做好的吐司放到顾客面前，并判断吐司是否是按照顺序以及是否已经做完的状态
    static class Eater implements Runnable{

        private ToastQueue finishedQueue;
        private int counter =0;

        public Eater(ToastQueue finishedQueue) {
            this.finishedQueue = finishedQueue;
        }

        @Override
        public void run() {
            try {
                while (!Thread.interrupted()){
                    // queue中无元素，则等待获取到元素。若有则在queue的head取出移除该元素。先进先出
                    Toast t=finishedQueue.take();
                    // 检查是否是先进先出按照顺序来的以及状态是否是Status.JAMMED
                    if (t.getId() != counter++ || t.getStatus() != Toast.Status.JAMMED){
                        System.err.println("出现错误"+t);
                        System.exit(1);
                    }else {
                        System.out.println("吃掉它==="+t);
                    }
                }
            }catch (InterruptedException e){
                System.out.println("Eater被中断");
            }
            System.out.println("Eater执行完毕");
        }
    }
    public static void main(String[] args) throws InterruptedException {
        ToastQueue dryQueue = new ToastQueue(),
                butteredQueue = new ToastQueue(),
                finishedQueue = new ToastQueue();
        ExecutorService service = Executors.newCachedThreadPool();
        service.execute(new Toaster(dryQueue));
        service.execute(new Butter(dryQueue,butteredQueue));
        service.execute(new Jammer(butteredQueue,finishedQueue));
        Eater eater = new Eater(finishedQueue);
        service.execute(eater);
        TimeUnit.SECONDS.sleep(5);
        service.shutdownNow();
    }
}
