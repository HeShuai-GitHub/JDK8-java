package com.concurrent.f_concurrentClass;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

/**
 * @author heshuai
 * @title: G_Semaphore
 * @description: 线程机制，演示java编程思想中21.7.6节案例
 *              Semaphore:信号旗，在书中称之为计数信号量。这个是类似于lock、synchronized的锁，但是不同的是，它是通过一个计数来管理进入资源的。
 *              当每个线程要进入一个方法时，会调用acquire()方法为每个线程分发一个permit，如果当前已经没有permit，那么将阻塞线程，并等到拿到permit后，在释放阻塞线程
 *              可以通过release()来添加permit。
 *              permit的总数量设置在在初始化Semaphore对象的时候进行设置，最多不可以超过初始化时的数量；在初始化时，可以设置fair是true还是false，若为true，则保证FIFO原则（先进先出），这个FIFO指的是调用
 *              acquire()的顺序，也就是说哪个线程先调用的acquire()，那么它将先获得permit；若为false,则刚好相反，不保证FIFO，那么很有可能出现无限等待的情况，相对来讲，肯定是false的性能更好一些
 *              不计时的tryAcquire()是不遵守FIFO原则的，即使fair为true，如果当前有可以获取的permit，那么它将直接获取，不会管是否有其他的等待线程，如果不想破坏FIFO原则，可以使用tryAcquire(0,TimeUnit.MILLISECONDS)来
 *              代替，是一样的意思
 *              注：在代码中实际是没有permit对象的，permit只是一个计数的信号量，并不是一个实际对象
 * @date 2021年03月24日 22:05
 */
public class G_Semaphore {

    static class Pool<T> {
        private int size;
        private List<T> items = new ArrayList<>();
        private volatile boolean[] checkedOut;
        private Semaphore available;

        public Pool(Class<T> classObject, int size) {
            this.size = size;
            checkedOut = new boolean[size];
            /**
             * 初始化一个Semaphore，指定permit数量，并且指定fair为true
             */
            available = new Semaphore(size, true);
            // 加载一个对象池来使用
            for (int i=0; i<size; ++i){
                try {
                    items.add(classObject.newInstance());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                }
            }
        }

        private synchronized T getItem() {
            for (int i=0; i<size; ++i){
                if (!checkedOut[i]){
                    checkedOut[i] = true;
                    return items.get(i);
                }
            }
            return null;
        }

        private synchronized boolean releaseItem(T item) {
            int index = items.indexOf(item);
            if (index == -1){
                return false;
            }
            if(checkedOut[index]){
                checkedOut[index] = false;
                return true;
            }
            return false;
        }

        public T checkOut() throws InterruptedException {
            // 获取一个permit，若没有，则阻塞线程
            available.acquire();
            /**
             * acquire()的一些重载方法进行必要解释
             * available.tryAcquire();  // 可以破坏FIFO原则，和tryLock差不多，调用时可以获得permit则获得，返回true，否则立即返回false
             * available.tryAcquire(0,TimeUnit.MILLISECONDS); // 准守FIFO原则，若permit没有，则等待指定时间，超时返回false; 否则返回true
             * available.tryAcquire(10); // 破坏FIFO原则，尝试获得10个permit，若足够可以获得，那么返回true；否则返回false
             * available.tryAcquire(10,0,TimeUnit.MILLISECONDS); // 遵守FIFO原则，获取指定数量的permit，获取不到等待指定时间，超时返回false，否则返回true
             */
            return getItem();
        }

        public void checkIn(T x) {
            if (releaseItem(x)){
                // 添加（释放）一个permit，最多不会超过初始化的数量
                available.release();
//                available.release(10); // 添加（释放）指定数量的permit
            }
        }
    }

    static class Fat {
        private volatile double d;
        private static int counter =0;
        private final int id = counter++;
        Random random = new Random(47);
        public Fat() throws InterruptedException {
            TimeUnit.MILLISECONDS.sleep(random.nextInt(500));
        }
        public void operation(){
            System.out.println(this);
        }
        @Override
        public String toString() {
            return "Fat{" +
                    "d=" + d +
                    ", id=" + id +
                    '}';
        }
    }
    static class CheckoutTask<T> implements Runnable{
        private static int counter =0;
        private final int id = counter++;
        private Pool<T> pool;

        public CheckoutTask(Pool<T> pool) {
            this.pool = pool;
        }

        @Override
        public void run() {
            try {
                T item = pool.checkOut();
                TimeUnit.SECONDS.sleep(1);
                System.out.println(this+"将存入"+item);
                pool.checkIn(item);
            } catch (InterruptedException e) {
                System.out.println("CheckoutTask 被中断"+this);
            }
        }

        @Override
        public String toString() {
            return "CheckoutTask{" +
                    "id=" + id +
                    '}';
        }
    }

    public static void main(String[] args) throws InterruptedException {
        final int size = 25;
        final Pool<Fat> pool = new Pool<>(Fat.class,size);
        ExecutorService service = Executors.newCachedThreadPool();
        for (int i =0; i<size; i++){
            service.execute(new CheckoutTask<Fat>(pool));
        }
        System.out.println("全部CheckoutTask被创建");
        List<Fat> list = new ArrayList<>();
        for (int i=0; i<size; i++){
            Fat f=pool.checkOut();
            System.out.println(i+" _main 线程取出对象_ "+f);
            f.operation();
            list.add(f);
        }
        Future<?> blocked = service.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    // 因为pool中所有对象已经全部被取出，所以这里将阻塞，直到调用cancel才会终止线程
                    pool.checkOut();
                } catch (InterruptedException e) {
                    System.out.println("Future<?> blocked 被中断");
                }
            }
        });
        TimeUnit.SECONDS.sleep(2);
        blocked.cancel(true);
        System.out.println("将list中的对象放入pool中："+list);
        for (Fat f:list){
            pool.checkIn(f);
        }
        // 多次向线程中放入对象，是没有影响的
        for (Fat f:list){
            pool.checkIn(f);
        }
        service.shutdownNow();
    }
}
