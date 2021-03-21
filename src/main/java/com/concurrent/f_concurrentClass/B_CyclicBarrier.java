package com.concurrent.f_concurrentClass;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

/**
 * @author heshuai
 * @title: B_CyclicBarrier
 * @description: 本线程机制，演示java编程思想中21.7.2节案例
 *              CyclicBarrier类似于CountDownLatch，不过可以多次重用。
 *              这里先对这个案例进行一个功能上的描述吧（可能和书中的不一致），这是一场赛马游戏，每匹马几乎是同时从起来开始出发，每个阶段马根据自己的能力跑0-2步之间，
 *              可以将这个每个阶段理解成每秒，那么当一匹马跑完了这一秒的步伐了，那么他需要等待其他马也跑完这一秒，直到所有的马都在这一秒中做出了动作，那么将会查看每批马跑了
 *              多少步，如果有任意一匹马到达了终点，那么将停止比赛，否则将继续比赛。
 * @date 2021年03月21日 21:19
 */
public class B_CyclicBarrier {

    static class Horse implements Runnable{
        private static int counter = 0;
        private final int id= counter++;
        // 步幅
        private int strides =0;
        private static Random random = new Random(47);
        private static CyclicBarrier cyclicBarrier;

        public Horse(CyclicBarrier barrier) {
            this.cyclicBarrier = barrier;
        }

        public synchronized int getStrides() {
            return strides;
        }

        @Override
        public void run() {
            try {
                while (!Thread.interrupted()){
                    synchronized (this){
                        // 每个阶段所跑的步数
                        strides += random.nextInt(3);
                    }
                    /**
                     *  等待，直到所有的线程都到达指定的barrier处
                     *  如果将CyclicBarrier比作CountDownLatch，那么这里就是在做countDown(),告知cyclicBarrier已经有一个线程完成了任务
                     *  当所有的线程都完成任务后，那么将自动触发barrierAction，如果它为null，则忽略。
                     *  当barrierAction执行完成或忽略后，count将重置，也就是又可以countDown()了，这个就是CyclicBarrier和CountDownLatch的区别，
                     *  CyclicBarrier可以多次使用
                     */
                    cyclicBarrier.await();
                }
            } catch (InterruptedException e) {
                System.err.println("Horse 被中断...");
            } catch (BrokenBarrierException e) {
                // 当一组线程中，其他线程超时或者中断，则报此异常
                System.err.println("Horse BrokenBarrierException");
            }
        }

        @Override
        public String toString() {
            return "Horse{" +
                    "id=" + id +
                    ", strides=" + strides +
                    '}';
        }
        // 记录当前线程的所跑的步数
        public String tracks(){
            StringBuilder s = new StringBuilder();
            for (int i=0; i<getStrides(); i++){
                // 以“*”来代替马匹跑的步数
                s.append("*");
            }
            // 唯一标识这匹马
            s.append(id);
            return s.toString();
        }
    }
    static class HorseRace {
        // 这场比赛总共的步数
        static final int FINISH_LINE = 75;
        // 存放查看马匹状态
        private List<Horse> horses = new ArrayList<>();
        // 线程池
        private ExecutorService service = Executors.newCachedThreadPool();
        private CyclicBarrier barrier;

        /**
         * 构造器
         * @param nHorses 几匹马匹参赛
         * @param pause 停顿时间
         */
        public HorseRace(int nHorses, final int pause){
            /**
             * 第一个参数parties，一组线程中有多少个线程，类似于CountDownLatch的Count
             * 第二个参数barrierAction：直译为栅栏行为，我也不太清楚如何翻译好，大概意思是一个业务分为两个阶段，
             * 第一阶段分为N个部分，可以由N个线程来同时处理，提高效率；
             * 第二阶段：当第一阶段全部处理完成后会自动触发第二阶段的行为，也就是第一阶段所做的事情是第二阶段的先决条件。
             * 注：tripped 翻译为被触发
             */
            barrier = new CyclicBarrier(nHorses, new Runnable() {
                // 以下演示赛马动画效果
                @Override
                public void run() {
                    StringBuilder s = new StringBuilder();
                    // 显示赛道的长度
                    for (int i=0; i<FINISH_LINE; i++){
                        s.append("=");
                    }
                    System.out.println(s);
                    // 显示每个阶段后每匹马所跑的距离
                    for (Horse horse:horses){
                        System.out.println(horse.tracks());
                    }
                    // 判断是否有马已经赢得了比赛，若有则停止比赛
                    for (Horse horse:horses){
                        if(horse.getStrides()>=FINISH_LINE){
                            System.out.println(horse+"：赢得比赛");
                            service.shutdownNow();
                            return;
                        }
                    }
                    // 暂停一段时间
                    try {
                        TimeUnit.MILLISECONDS.sleep(pause);
                    } catch (InterruptedException e) {
                        System.err.println("HorseRace 被中断...");
                    }
                }
            });
            // 将每匹马放入赛道
            for (int i=0; i<nHorses; i++){
                Horse horse = new Horse(barrier);
                horses.add(horse);
                service.execute(horse);
            }
        }

        public static void main(String[] args) {
            int nHorses =3;
            int pause =200;
            new HorseRace(nHorses,pause);
        }
    }
}
