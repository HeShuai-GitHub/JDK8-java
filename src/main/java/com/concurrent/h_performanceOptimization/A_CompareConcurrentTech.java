package com.concurrent.h_performanceOptimization;

import jdk.internal.org.objectweb.asm.tree.FieldInsnNode;

import java.awt.image.VolatileImage;
import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.SimpleFormatter;

/**
 * @author heshuai
 * @title: A_CompareConcurrentTech
 * @description: 线程机制，演示java编程思想中21.9.1节案例
 *              这一节主要是对比各个互斥技术在性能方面的表现，主要是针对synchronized、lock、atomic进行测试
 *              微基准测试：指在隔离的、脱离上下文环境的情况下对某个特性进行性能测试
 *              以下为了避免jvm对于代码的特殊优化，而进行的必要的读取和写入操作，并且进一步复杂化程序和不可预测性，以使得jvm没有机会执行积极优化
 *              以下根据书中实例进行了部分调整，而且BaseLine是一个有问题的测试，因为在BaseLine中是没有互斥的，多线程在公共资源这里，造成了竞争，
 *              间接导致了一些问题，比如本实例中的ArrayIndexOutOfBoundsException，这个我尝试了优化，但是在不加互斥条件的情况下，没有办法完全
 *              避免多线程的资源竞争。
 * @date 2021年03月28日 10:56
 */
public class A_CompareConcurrentTech {
    // 模板方法设计模式
    static abstract class Accumulator {
        // 线程中循环执行读取和写入操作次数
        public static long cycles = 50000L;
        // 线程数量
        private static final int N =4;
        // 线程池
        public static ExecutorService service = Executors.newFixedThreadPool(N*2);
        // 协调每一个线程之间的进度
        private static CyclicBarrier barrier = new CyclicBarrier(N*2+1);
        // preLoaded 中坐标
        protected volatile int index =0;
        // preLoaded 中累加的值
        protected volatile long value =0;
        // 执行线程所花费时间
        protected long duration =0;
        // 唯一标识
        protected String id = "error";
        // preLoaded 数组中的元素个数
        protected final static int SIZE = 100000;
        // 预先设置的数据集合
        protected static int[] preLoaded = new int[SIZE];
        static {
            // 初始化preLoaded数组
            Random random = new Random(47);
            for (int i=0; i< SIZE; i++){
                preLoaded[i] = random.nextInt();
            }
        }
        // 模板方法
        public abstract void accumulate();
        public abstract Long read();
        // 写入任务
        private class Modifier implements Runnable{
            @Override
            public void run() {
                for (Long i =0L;i<cycles; i++){
                    accumulate();
                }
                try {
                    barrier.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }
        }
        // 读取任务
        private class Reader implements Runnable{
            private volatile Long value;
            @Override
            public void run() {
                for (Long i =0L;i<cycles; i++){
                    value = read();
                }
                try {
                    barrier.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }
        }
        // 测试执行多个线程进行读取写入所花费的时间
        public void timedTest() {
            Long start = System.nanoTime();
            for (int i=0; i<N; i++){
                service.execute(new Modifier());
                service.execute(new Reader());
            }
            try {
                barrier.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
            duration = System.nanoTime()-start;
            System.out.printf("%s：%d\t,%s：%d\t,%s：%d\n",id,duration,"index",index,"value",value);
        }
        // 线程不同互斥机制之间的性能差异
        public static void report(Accumulator acc1, Accumulator acc2){
            System.out.printf("%s：%f\n",acc1.id+"/"+acc2.id,(double)acc1.duration/acc2.duration);
        }
    }

    /**
     * 无互斥条件，这里会导致ArrayIndexOutOfBoundsException，因为多个线程在运行到
     *             if(index>=SIZE) {  // index=99999，不符合条件
     *                 index =0;
     *                 return;
     *             }
     *             value+=preLoaded[index++];  // 第一个线程修改了index后，index得到100000,第二个线程因为index被volatile标识，保证了可视性，那么将会数组越界
     *
     */
    static class BaseLine extends Accumulator{
        {id = "BaseLine"; }

        @Override
        public void accumulate() {
            // 这里将index加N来确定数组的临界点
            if(index+Accumulator.N>=SIZE) {
                index =0;
                return;
            }
            value+=preLoaded[index++];
        }

        @Override
        public Long read() {
            return value;
        }
    }
    // 测试synchronized锁
    static class SynchronizedTest extends Accumulator{
        {id = "synchronized"; }

        @Override
        public synchronized void accumulate() {
            value+=preLoaded[index++];
            if(index>=SIZE) {
                index =0;
            }
        }

        @Override
        public synchronized Long read() {
            return value;
        }
    }
    // 测试Lock锁，Lock在性能表现上要比synchronized要好一些
    static class LockTest extends Accumulator{
        {id = "Lock"; }
        private Lock lock = new ReentrantLock();

        @Override
        public void accumulate() {
            lock.lock();
            try {
                value+=preLoaded[index++];
                if(index>=SIZE) {
                    index =0;
                }
            }finally {
                lock.unlock();
            }
        }

        @Override
        public Long read() {
            lock.lock();
            try {
                return value;
            }finally {
                lock.unlock();
            }
        }
    }
    // 测试根据Atomic来保证线程安全，但是Atomic并不适合有多个原子性操作的场景，因为在多个原子性操作中间可能会发生上下文
    // 切换，作者希望通过这个来展示Atomic的性能优势。
    // 注：如果涉及到多个Atomic对象，那么就不可以使用Atomic来确保线程的一致性了，而应该使用更加常规的操作。JDK文档中也有声明，当对一个对象的临界更新被限制为只涉及单个变量时，才可以考虑使用这个方式。
    static class AtomicLine extends Accumulator{
        {id = "Atomic"; }
        // Atomic类
        private AtomicInteger index=new AtomicInteger(0);
        private AtomicLong value = new AtomicLong(0);

        @Override
        public void accumulate() {
            int i =index.getAndIncrement();
            // 这里可能发生上下文切换，如此操作会造成最后结果不一致
            if (++i>=SIZE){
                index.set(0);
                return;
            }
            value.getAndAdd(preLoaded[i]);
        }

        @Override
        public Long read() {
            return value.get();
        }
    }
    // 测试各个互斥条件的不同
    static class SynchronizationComparisons {
        static BaseLine baseLine = new BaseLine();
        static SynchronizedTest sync= new SynchronizedTest();
        static LockTest lock = new LockTest();
        static AtomicLine atomic = new AtomicLine();
        static void test() {
            System.out.println("===================================");
            System.out.printf("%s：%d\n","Cycles", Accumulator.cycles);
            baseLine.timedTest();
            sync.timedTest();
            lock.timedTest();
            atomic.timedTest();
            Accumulator.report(sync,baseLine);
            Accumulator.report(lock,baseLine);
            Accumulator.report(atomic,baseLine);
            Accumulator.report(sync,lock);
            Accumulator.report(sync,atomic);
            Accumulator.report(lock,atomic);
        }
    }

    public static void main(String[] args) {
        int iterations = 5;
        System.out.println("开始：");
        // 在开始的时候完成所有线程的创建，以免在测试过程中产生任何额外的开销，确保性能测试的准确性
        SynchronizationComparisons.baseLine.timedTest();
        for (int i=0; i<iterations;i++){
            SynchronizationComparisons.test();
            Accumulator.cycles*=2;
        }
        Accumulator.service.shutdown();
    }

}
