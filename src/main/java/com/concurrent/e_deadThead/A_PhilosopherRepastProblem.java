package com.concurrent.e_deadThead;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author heshuai
 * @title: A_PhilosopherRepastProblem
 * @description: 本线程机制，演示java编程思想中21.6节案例
 *               这个案例主要仿真哲学家就餐问题，指的是有五个哲学家并由五根筷子，他们围坐在一个圆桌上准备吃饭，每个人的左右两边都有一根筷子，哲学家想要吃饭
 *               必须拿到左右两边的两根筷子才可以就餐，他们是按照先拿右边筷子再拿左边的筷子的方式去拿的，如果所需要的筷子已经被其他的哲学家使用的话，那么它将陷入等待中，
 *               直到可以拿到筷子为止，这个就是典型的死锁问题，满足了死锁的四个条件。
 * @date 2021年03月21日 13:19
 */
public class A_PhilosopherRepastProblem {
    // 描述筷子被拿起放下的状态
    static class Chopstick {
        private boolean taken = false;
        public synchronized void taken() throws InterruptedException {
            while (taken){
                wait();
            }
            taken = true;
        }
        public synchronized void drop(){
            taken = false;
            notifyAll();
        }
        public synchronized void testLock(){
            System.err.println("获取到锁");
        }
    }

    static class Philosopher implements Runnable{

        private Chopstick left;
        private Chopstick right;
        private final int id;
        private final int ponderFactor;
        private Random random = new Random(47);

        public Philosopher(Chopstick left, Chopstick right, int id, int ponderFactor) {
            this.left = left;
            this.right = right;
            this.id = id;
            this.ponderFactor = ponderFactor;
        }
        // 模拟哲学家思考的过程
        private void pause() throws InterruptedException {
            if (ponderFactor ==0){
                return;
            }
            TimeUnit.MILLISECONDS.sleep(random.nextInt(ponderFactor*250));
        }

        @Override
        public void run() {
            try {
                while (!Thread.interrupted()){
                    System.out.println("哲学家开始思考...");
                    pause();
                    // 思考饿了，开始干饭
                    System.out.println("哲学家开始拿右边的筷子");
                    right.taken();
                    System.out.println("哲学家开始拿左边的筷子");
                    left.taken();
                    System.out.println("哲学家开始干饭");
                    pause();
                    System.out.println("吃饱了，放下筷子，给其他人用");
                    right.drop();
                    left.drop();
                }
            } catch (InterruptedException e) {
                System.err.println("Philosopher 被中断了");
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        // 将ponder哲学家思考时间设置足够短以达到可以尽快达到死锁状态
        int ponder =0;
        // 设置成5并不是不会出现死锁，而是几率比较低
//        int ponder =5;
        int size =5;
        ExecutorService service = Executors.newCachedThreadPool();
        Chopstick[] chopsticks = new Chopstick[size];
        // 生成五个哲学家
        for (int i=0;i<size;i++){
            chopsticks[i]=new Chopstick();
        }
        // 哲学家开始吃饭
        for (int i=0;i<size;i++){
            // 这里涉及一个叫做数据循环下标的处理方式，就是取余，这是正循环，负循环是（size+i-1)%size
            service.execute(new Philosopher(chopsticks[i],chopsticks[(i+1)%size],i,ponder));
        }
        // 测试当造成死锁后，因为是wait阻塞，是否会导致其他线程无法获取锁
        // 结果其他线程依然可以获取锁
        int i = 0;
        while (true){
            chopsticks[i%size].testLock();
            TimeUnit.SECONDS.sleep(1);
        }
        // 注释掉中断线程的方法，以便达到死锁效果
//        TimeUnit.SECONDS.sleep(5);
//        service.shutdownNow();
    }

}
