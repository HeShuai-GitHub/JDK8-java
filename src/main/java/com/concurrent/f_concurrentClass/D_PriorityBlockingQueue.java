package com.concurrent.f_concurrentClass;

import jdk.internal.org.objectweb.asm.tree.FieldInsnNode;
import org.junit.internal.runners.statements.RunAfters;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author heshuai
 * @title: D_PriorityBlockingQueu
 * @description: 线程机制，演示java编程思想中21.7.4节案例
 *               PriorityBlockingQueue是一个很基础的优先级队列，它具有可阻塞的读取操作，队列中的对象可以按照优先级顺序从队列中读取任务
 * @date 2021年03月23日 20:58
 */
public class D_PriorityBlockingQueue {

    static class PrioritizedTask implements Runnable, Comparable<PrioritizedTask>{

        private static int counter =0;
        // 标记ID
        private final int id =counter++;

        private Random random = new Random(47);

        private final int priority;
        protected static List<PrioritizedTask> sequence = new ArrayList<>();

        public PrioritizedTask(int priority) {
            this.priority = priority;
            sequence.add(this);
        }
        // 刚好和Comparable接口中定义的顺序是反的，PriorityBlockingQueue队列中存储实现compareTo的实体类
        @Override
        public int compareTo(PrioritizedTask o) {
            PrioritizedTask that = o;
            // 队列中大的排在header位置
            return this.priority<that.priority?1:(this.priority>that.priority?-1:0);
        }

        @Override
        public void run() {
            try {
                // 延迟一会，模拟操作
                TimeUnit.MILLISECONDS.sleep(random.nextInt(250));
            } catch (InterruptedException e) {
                System.out.println("PrioritizedTask 被中断");
            }
            System.out.println(this);
        }

        @Override
        public String toString() {
            return "PrioritizedTask{" +
                    "id=" + id +
                    ", priority=" + priority +
                    '}';
        }

        public String summary(){
            return "("+id+":"+priority+")";
        }

        public static class EndSentinel extends PrioritizedTask{
            private ExecutorService service;

            public EndSentinel(ExecutorService service) {
                super(-1);
                this.service = service;
            }

            @Override
            public void run() {
                int count =0;
                for (PrioritizedTask task:sequence){
                    System.out.print(task.summary());
                    if (++count%5==0)
                        System.out.println();
                }
                System.out.println();
                System.out.println("将要调用shutdownNow");
                service.shutdownNow();
            }
        }
    }

    /**
     * 生产者
     */
    static class PrioritizedTaskProducer implements Runnable{
        private Random random = new Random(47);
        private Queue<Runnable> queue;
        private ExecutorService service;

        public PrioritizedTaskProducer(Queue<Runnable> queue, ExecutorService service) {
            this.queue = queue;
            this.service = service;
        }

        @Override
        public void run() {
            for (int i=0; i<20; i++){
                queue.add(new PrioritizedTask(random.nextInt(10)));
                Thread.yield();
            }
            try {
                for (int i=0; i<10; i++){
                    TimeUnit.MILLISECONDS.sleep(250);
                    // 插入元素，该元素必须实现了Comparable
                    queue.add(new PrioritizedTask(10));
                }
                for (int i=0; i<10; i++){
                    queue.add(new PrioritizedTask(10));
                }
                queue.add(new PrioritizedTask.EndSentinel(service));
            } catch (InterruptedException e) {
                System.out.println("PrioritizedTaskProducer 被中断");
            }
            System.out.println("PrioritizedTaskProducer 结束...");
        }
    }

    static class PrioritizedTaskConsumer implements Runnable{

        private PriorityBlockingQueue<Runnable> priorityBlockingQueue;

        public PrioritizedTaskConsumer(PriorityBlockingQueue<Runnable> priorityBlockingQueue) {
            this.priorityBlockingQueue = priorityBlockingQueue;
        }

        @Override
        public void run() {
            try {
//                TimeUnit.SECONDS.sleep(4);
                while (!Thread.interrupted()){
                    priorityBlockingQueue.take().run();
                }
            } catch (InterruptedException e) {
                System.out.println("PrioritizedTaskConsumer 被中断");
            }
            System.out.println("PrioritizedTaskConsumer 结束");
        }
    }

    public static void main(String[] args) {
        Random random=new Random(47);
        ExecutorService service = Executors.newCachedThreadPool();
        PriorityBlockingQueue<Runnable> priorityBlockingQueue =new PriorityBlockingQueue<>();
        service.execute(new PrioritizedTaskProducer(priorityBlockingQueue,service));
        service.execute(new PrioritizedTaskConsumer(priorityBlockingQueue));
    }
}
