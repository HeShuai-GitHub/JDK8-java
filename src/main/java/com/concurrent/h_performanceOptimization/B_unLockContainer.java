package com.concurrent.h_performanceOptimization;

import java.util.*;
import java.util.concurrent.*;

/**
 * @author heshuai
 * @title: B_unLockContainer
 * @description: 线程机制，演示java编程思想中21.9.2节案例
 *              关于免锁容器，这块在博客中讲到了很多，这里就不重复了，下面对于几种免锁容器进行性能测试
 * @date 2021年03月28日 17:44
 */
public class B_unLockContainer {

    abstract static class Tester<C> {
        // 重复测试次数
        static int testReps = 10;
        // 读写重复次数
        static int testCycles = 1000;
        // 容器大小
        static int containerSize = 1000;
        // 初始化容器
        abstract C containerInitializer();
        // 开始读写操作
        abstract void startReadersAndWriters();
        // 容器
        C testContainer;
        // 标识
        String testID;
        // 读取线程数
        int nReaders;
        // 写入线程数
        int nWriters;
        volatile long readResult = 0;
        // 读取所花费时间
        volatile long readTime = 0;
        // 写入所花费时间
        volatile long writeTime =0;
        // 协调线程
        CountDownLatch endLatch;
        static ExecutorService service = Executors.newCachedThreadPool();
        // 写入数据
        Integer[] writeData = new Integer[containerSize];

        public Tester(String testID, int nReaders, int nWriters) {
            this.testID = testID+" "+nReaders+"r "+nWriters+"w";
            this.nReaders = nReaders;
            this.nWriters = nWriters;
            // 初始化写入数据
            for (int i=0; i<containerSize; i++){
                writeData[i] = new Random(47).nextInt();
            }
            for (int i=0; i<testReps; i++){
                runTest();
                readTime=0;
                writeTime=0;
            }
        }
        // 执行测试，并且输出执行时间
        void runTest(){
            endLatch = new CountDownLatch(nReaders+nWriters);
            testContainer = containerInitializer();
            startReadersAndWriters();
            try {
                endLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.printf("%s %d %d\t",testID,readTime,writeTime);
            if (readTime!=0&&writeTime!=0){
                System.out.println("readTime+writeTime="+(readTime+writeTime));
            }
        }
        // 执行任务
        abstract class TestTask implements Runnable{
            // 测试读或者写
            abstract void test();
            // 统计结果
            abstract void putResults();
            long duration;

            @Override
            public void run() {
                long startTime = System.nanoTime();
                test();
                duration = System.nanoTime()-startTime;
                synchronized (Tester.this){
                    putResults();
                }
                endLatch.countDown();
            }
        }
    }
    // List集合
    static abstract class ListTest extends Tester<List<Integer>>{

        public ListTest(String testID, int nReaders, int nWriters) {
            super(testID, nReaders, nWriters);
        }
        // 读
        class Reader extends TestTask {
            long result =0L;
            @Override
            void test() {
                for (long i =0L; i<testCycles; i++){
                    for (int index=0; index<containerSize;index++){
                        result+=(testContainer.get(index)==null?0:testContainer.get(index));
                    }
                }
            }

            @Override
            void putResults() {
                readResult += result;
                readTime += duration;
            }
        }
        // 写入
        class Writer extends TestTask {
            @Override
            void test() {
                for (long i =0L; i<testCycles; i++){
                    for (int index=0; index<containerSize;index++){
                        // 使用特定元素来代替list集合中特定位置的元素
                        testContainer.set(index,writeData[index]);
                    }
                }
            }

            @Override
            void putResults() {
                writeTime += duration;
            }
        }
        // 通过多个线程来进行读写操作
        @Override
        void startReadersAndWriters() {
            for (int i=0;i<nReaders; i++){
                service.execute(new Reader());
            }
            for (int i=0;i<nWriters; i++){
                service.execute(new Writer());
            }
        }
    }
    // synchronized互斥机制
    static class SynchronizedArrayListTest extends ListTest{
        public SynchronizedArrayListTest(int nReaders, int nWriters) {
            super("SynchronizedArrayList", nReaders, nWriters);
        }

        @Override
        List<Integer> containerInitializer() {
            return Collections.synchronizedList(new ArrayList<Integer>(Arrays.asList(new Integer[containerSize])));
        }
    }
    // 免锁容器
    static class CopyWriteArrayListTest extends ListTest{
        public CopyWriteArrayListTest(int nReaders, int nWriters) {
            super("CopyWriteArrayList", nReaders, nWriters);
        }

        @Override
        List<Integer> containerInitializer() {
            return new CopyOnWriteArrayList<Integer>(Arrays.asList(new Integer[containerSize]));
        }
    }
    static class ListComparisons {
        public static void main(String[] args) {
            new SynchronizedArrayListTest(10,0);
            new SynchronizedArrayListTest(9,1);
            new SynchronizedArrayListTest(5,5);
            new SynchronizedArrayListTest(3,7);
            new SynchronizedArrayListTest(1,9);
            new CopyWriteArrayListTest(10,0);
            new CopyWriteArrayListTest(9,1);
            new CopyWriteArrayListTest(5,5);
            new CopyWriteArrayListTest(3,7);
            new CopyWriteArrayListTest(1,9);
            Tester.service.shutdown();
        }
    }

    // Map集合
    static abstract class MapTest extends Tester<Map<Integer,Integer>>{

        public MapTest(String testID, int nReaders, int nWriters) {
            super(testID, nReaders, nWriters);
        }
        // 读
        class Reader extends TestTask {
            long result =0L;
            @Override
            void test() {
                for (long i =0L; i<testCycles; i++){
                    for (int index=0; index<containerSize;index++){
                        result+=(testContainer.get(index)==null?0:testContainer.get(index));
                    }
                }
            }

            @Override
            void putResults() {
                readResult += result;
                readTime += duration;
            }
        }
        // 写入
        class Writer extends TestTask {
            @Override
            void test() {
                for (long i =0L; i<testCycles; i++){
                    for (int index=0; index<containerSize;index++){
                        // 使用特定元素来代替list集合中特定位置的元素
                        testContainer.put(index,writeData[index]);
                    }
                }
            }

            @Override
            void putResults() {
                writeTime += duration;
            }
        }
        // 通过多个线程来进行读写操作
        @Override
        void startReadersAndWriters() {
            for (int i=0;i<nReaders; i++){
                service.execute(new Reader());
            }
            for (int i=0;i<nWriters; i++){
                service.execute(new Writer());
            }
        }
    }

    // synchronized互斥机制
    static class SynchronizedHashMapTest extends MapTest{
        public SynchronizedHashMapTest(int nReaders, int nWriters) {
            super("SynchronizedHashMap", nReaders, nWriters);
        }

        @Override
        Map<Integer,Integer> containerInitializer() {
            return Collections.synchronizedMap(new HashMap<>());
        }
    }

    // 免锁容器
    static class ConcurrentHashMapTest extends MapTest{
        public ConcurrentHashMapTest(int nReaders, int nWriters) {
            super("ConcurrentHashMap", nReaders, nWriters);
        }

        @Override
        Map<Integer,Integer> containerInitializer() {
            return new ConcurrentHashMap<Integer,Integer>();
        }
    }

    static class MapComparisons {
        public static void main(String[] args) {
            new SynchronizedHashMapTest(10,0);
            new SynchronizedHashMapTest(9,1);
            new SynchronizedHashMapTest(5,5);
            new SynchronizedHashMapTest(3,7);
            new SynchronizedHashMapTest(1,9);
            new ConcurrentHashMapTest(10,0);
            new ConcurrentHashMapTest(9,1);
            new ConcurrentHashMapTest(5,5);
            new ConcurrentHashMapTest(3,7);
            new ConcurrentHashMapTest(1,9);
            Tester.service.shutdown();
        }
    }
}
