package com.concurrent.f_concurrentClass;


import java.util.*;
import java.util.concurrent.*;

/**
 * @author heshuai
 * @title: C_DelayQueue
 * @description: 本线程机制，演示java编程思想中21.7.3节案例
 *              延迟队列，这个是jdk提供的一个实现延迟任务的方式，DelayQueue
 *              当然这绝对不是实现延迟任务的最好方式，使用Redis实现应该会更好一些
 * @date 2021年03月21日 23:18
 */
public class C_DelayQueue {
    static class DelayedTask implements Runnable, Delayed {

        private static int counter =0;
        // 标记ID
        private final int id =counter++;
        // 延迟时长，单位Millisecond，非必须
        private final int delta;
        // 触发时间，单位nanosecond，写Delayed类必须属性
        private final long trigger;
        // 顺序集
        protected static List<DelayedTask> sequence = new ArrayList<>();

        public DelayedTask(int delayMilliseconds) {
            this.delta = delayMilliseconds;
            this.trigger = System.nanoTime()+TimeUnit.NANOSECONDS.convert(delta,TimeUnit.MILLISECONDS);
            sequence.add(this);
        }
        // 实现自Comparable接口方法，用于在DelayedQueue中进行排序时比较方法一致，对比触发时间
        @Override
        public int compareTo(Delayed o) {
            DelayedTask that = (DelayedTask)o;
            return this.trigger>that.trigger?1:(this.trigger==that.trigger?0:-1);
        }

        @Override
        public void run() {
            System.out.println(this+"");
        }

        /**
         * 返回当前对象的剩余延迟时间，可以通过TimeUnit来指定对应的时间单位
         */
        @Override
        public long getDelay(TimeUnit unit) {
            return unit.convert(trigger-System.nanoTime(),TimeUnit.NANOSECONDS);
        }

        @Override
        public String toString() {
            return "DelayedTask{" +
                    "id=" + id +
                    ", delta=" + delta +
                    ", trigger=" + trigger +
                    '}';
        }

        public String summary(){
            return "("+id+":"+delta+")";
        }

        /**
         * 直译：结束监控，当调用这个类的时候，结束线程池中的所有线程
         */
        public static class EndSentinel extends DelayedTask{
            private ExecutorService service;

            public EndSentinel(int delayMilliseconds, ExecutorService service) {
                super(delayMilliseconds);
                this.service = service;
            }
            @Override
            public void run() {
                // 打印每个元素
                for (DelayedTask pt:sequence){
                    System.out.print(pt.summary()+"");
                }
                System.out.println();
                System.out.printf(this+"\n启动 ShutDownNow\n");
                service.shutdownNow();
            }
        }
    }

    /**
     * 消费者
     */
    static class DelayedTaskConsumer implements Runnable{

        private DelayQueue<DelayedTask> q;

        public DelayedTaskConsumer(DelayQueue<DelayedTask> q) {
            this.q = q;
        }

        @Override
        public void run() {
            try {
                // 在queue中取出元素并运行
                while (!Thread.interrupted()){
                    q.take().run();
                }
            } catch (InterruptedException e) {
                System.err.println("DelayedTaskConsumer 被中断");
            }
            System.out.println("DelayedTaskConsumer 执行完成");
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Random random= new Random(47);
        ExecutorService service = Executors.newCachedThreadPool();
        DelayQueue<DelayedTask> queue = new DelayQueue<>();
        for (int i=0; i<20; i++){
            queue.put(new DelayedTask(random.nextInt(5000)));
        }
        queue.add(new DelayedTask.EndSentinel(5000, service));
        service.execute(new DelayedTaskConsumer(queue));
        // API测试
        testAPI();
    }

    /**
     * 测试常用的方法
     */
    static void testAPI() throws InterruptedException {
        TimeUnit.SECONDS.sleep(6);
        // BlockingQueue 是可以有容量限制的
        BlockingQueue linkedBlocking = new LinkedBlockingDeque(1);
        BlockingQueue arrayBlocking = new ArrayBlockingQueue(1);
        // 声明DelayQueue
        DelayQueue<DelayedTask> queue = new DelayQueue<>();
        // **DelayQueue 是没有容量限制的，默认队列大小为Integer.MAX_VALUE**
        System.out.println(queue.remainingCapacity());
        // **存元素所涉及的几个API方法，不可以向队列中添加null**
        // add 添加元素到queue队列中，基于Collection的形式，内部实现调用的offer方法
        queue.add(new DelayedTask(1999));
        queue.add(new DelayedTask(1998));
        // put 添加元素到queue队列，内部实现调用的offer方法
        queue.put(new DelayedTask(1994));
        queue.put(new DelayedTask(1995));
        // offer 添加元素
        queue.offer(new DelayedTask(1996));
        queue.offer(new DelayedTask(1993));
        // **取元素**
        // 显示队列中到期的元素（如果没有，那么显示下一个即将到期的元素），并不会移除元素
        // 如果队列元素为空，那么返回null
        // peek：窥探、偷看; Retrieves: 检索
        System.err.println(queue.peek());
        // 检索并移除元素，如果没有到期元素，则返回null
        // 此poll方法实现于Queue接口，但是Queue接口定义的poll方法是取队列头元素，没有延迟作用。
        System.err.println(queue.poll());
        // 检索并移除元素，如果没有到期元素，则等待元素到期，如果队列中没有元素，则无限等待添加元素后到期。
        System.err.println(queue.take());
        TimeUnit.SECONDS.sleep(1);
        System.err.println(queue.poll());
        System.err.println(queue.peek());

        List<DelayedTask> list = new ArrayList<>();
        // 将【到期】元素添加指定集合中，这个集合必须是Collection的子类
        // drainTo实现自BlockingQueue，在BlockingQueue中是将可以获取的元素（即队列中存在元素）添加到指定元素
        System.out.println(queue.drainTo(list));
        Iterator<DelayedTask> iterator = list.iterator();
        System.out.println("=======================================");
        while (iterator.hasNext()){
            System.out.println(iterator.next());
        }
        // 检索并移除到期元素，若没有到期元素，则报NoSuchElementException异常，
        // 这个方法在poll方法上进行了对返回null这种情况加了一层校验
        System.out.println(queue.remove());
        // 清空队列中元素
        queue.clear();
    }
}
