package com.concurrent.g_emulation;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;

/**
 * @author heshuai
 * @title: A_Assignment
 * @description: 线程机制，演示java编程思想中21.8.3节案例
 *              下面将演示书中的一个案例，分发工作
 *              这个是考虑一个用于生产汽车的机器人组装线，每辆汽车都将分为多个阶段构建，从创建底盘开始，紧跟着是安装发动机、车厢和轮子
 * @date 2021年03月25日 22:00
 */
public class A_Assignment {

    public static void main(String[] args) throws InterruptedException {
        // 阻塞队列，实现线程间的协调
        CarQueue chassisQueue = new CarQueue(), finishingQueue = new CarQueue();
        // 线程池
        ExecutorService service = Executors.newCachedThreadPool();
        // 机器人池，类似于线程池的容器概念，每一个生产机器人需要在这个中取
        RobotPool pool = new RobotPool();
        service.execute(new EngineRobot(pool));
        service.execute(new DriveTrainRobot(pool));
        service.execute(new WheelRobot(pool));
        service.execute(new Assembler(chassisQueue,finishingQueue,pool));
        // 这里和书中案例有所不同，书中案例只有一个线程对于chassisBuilder的执行，但是考虑到生产汽车的机器人流水线主要的产能瓶颈在于生产chassisBuilder，所以使用多个线程运行来提高产能
        ChassisBuilder chassisBuilder = new ChassisBuilder(chassisQueue);
        service.execute(chassisBuilder);
        service.execute(chassisBuilder);
        service.execute(chassisBuilder);
        // 查看十秒钟内的产线产能
        TimeUnit.SECONDS.sleep(10);
        service.shutdownNow();
        // 打印最后生产出来的汽车，书中例子是放到上面线程池中执行的，是实时汇报生产的情况，但是打印到console中，有点乱，放到这里最后统一打印吧
        new Thread(new Reporter(finishingQueue)).start();
    }

    // car 实体
    static class Car {
        // 每辆汽车唯一标识
        private final int id;
        // 汽车组装三种状态
        private boolean engine = false, driveTrain = false, wheels=false;

        public Car(int id) {
            this.id = id;
        }

        public Car() {
            this.id = -1;
        }

        public synchronized int getId() {
            return id;
        }

        public synchronized void addEngine() {
            this.engine = true;
        }
        public synchronized void addDriveTrain() {
            this.driveTrain = true;
        }
        public synchronized void addWheels() {
            this.wheels = true;
        }

        @Override
        public synchronized String toString() {
            return "Car{" +
                    "id=" + id +
                    ", engine=" + engine +
                    ", driveTrain=" + driveTrain +
                    ", wheels=" + wheels +
                    '}';
        }
    }
    // 阻塞队列
    static class CarQueue extends LinkedBlockingQueue<Car> {}
    // 流水线 机器人池
    static class RobotPool {
        // Set容器，并不是k-v的形式，Set中元素不可以重复，因此只可以有一个null
        private Set<Robot> pool = new HashSet<Robot>();
        // 添加机器人
        public synchronized void add(Robot r) {
            pool.add(r);
            notifyAll();
        }
        /**
         * 雇佣机器人
         * @param robotType 机器人类型
         * @param d 流水线
         */
        public synchronized void hire(Class<? extends Robot> robotType, Assembler d) throws InterruptedException {
            for (Robot r : pool){
                if (r.getClass().equals(robotType)){
                    // 取出对应机器人
                    pool.remove(r);
                    // 指派机器人到对应流水线
                    r.assignAssembler(d);
                    // 开始工作
                    r.engage();
                    return;
                }
            }
            wait();
            // 递归
            hire(robotType,d);
        }
        // 返还机器人
        public synchronized void release(Robot robot) {
            this.add(robot);
        }
    }
    // 生产汽车底盘，生产汽车第一步
    static class ChassisBuilder implements Runnable{

        private CarQueue cars;
        // 累计生产汽车标识
        private int counter =0;
        public ChassisBuilder(CarQueue cars) {
            this.cars = cars;
        }
        @Override
        public void run() {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    TimeUnit.MILLISECONDS.sleep(500);
                    // 制作底盘
                    Car c = new Car(incrementCounter());
                    System.out.println("创建了一辆车_"+c);
                    cars.put(c);
                }
            } catch (InterruptedException e) {
                System.out.println("ChassisBuilder 被中断");
            }
        }
        // 通过synchronized来防止多个线程之间的资源强占
        private synchronized int incrementCounter(){
            return this.counter++;
        }
    }
    // 组装汽车流水线
    static class Assembler implements Runnable{
        private CarQueue chassisQueue, finishingQueue;
        private Car car;
        // 这个组件用来同步多线程间的协调问题，这里barrierAction是null，也就是没有触发行为
        private CyclicBarrier barrier = new CyclicBarrier(4);
        private RobotPool robotPool;

        public Assembler(CarQueue chassisQueue, CarQueue finishingQueue, RobotPool robotPool) {
            this.chassisQueue = chassisQueue;
            this.finishingQueue = finishingQueue;
            this.robotPool = robotPool;
        }

        public Car car() {
            return car;
        }

        public CyclicBarrier barrier() {
            return barrier;
        }

        @Override
        public void run() {
            try {
                while (!Thread.currentThread().isInterrupted()){
                    this.car = chassisQueue.take();
                    // 雇佣
                    robotPool.hire(EngineRobot.class,this);
                    robotPool.hire(DriveTrainRobot.class,this);
                    robotPool.hire(WheelRobot.class,this);
                    // barrier 等待其他线程完成
                    barrier.await();
                    finishingQueue.put(this.car);
                }
            } catch (InterruptedException | BrokenBarrierException e) {
                System.out.println("Assembler 被打断");
            }
        }
    }
    // 报告汽车完成情况
    static class Reporter implements Runnable {
        private CarQueue cars;

        public Reporter(CarQueue cars) {
            this.cars = cars;
        }

        @Override
        public void run() {
            try {
                while (!cars.isEmpty()){
                    System.err.println(cars.take());
                }
            } catch (InterruptedException e) {
                System.out.println("Reporter 被中断");
            }
            System.out.println("离开 Reporter");
        }
    }
    // Robot 抽象基类
    static abstract class Robot implements Runnable {

        private RobotPool pool;

        public Robot(RobotPool pool) {
            this.pool = pool;
        }
        protected Assembler assembler;

        public Robot assignAssembler(Assembler assembler){
            this.assembler = assembler;
            return this;
        }
        // 是否开始工作
        private boolean engage = false;
        public synchronized void engage() {
            engage = true;
            notifyAll();
        }
        // 由各个子类实现的行为
        abstract protected void performService();
        @Override
        public void run() {
            try {
                powerDown();
                while (!Thread.currentThread().isInterrupted()){
                    performService();
                    assembler.barrier().await();
                    powerDown();
                }
            } catch (InterruptedException | BrokenBarrierException e) {
                System.out.println("Robot 被中断");
            }
            System.out.println("离开 Robot");
        }
        // 释放资源
        private synchronized void powerDown() throws InterruptedException {
            engage = false;
            assembler = null;
            pool.release(this);
            while (engage == false){
                wait();
            }
        }

        @Override
        public String toString() {
            return getClass().getName();
        }
    }
    static class EngineRobot extends Robot{
        public EngineRobot(RobotPool pool) {
            super(pool);
        }

        @Override
        protected void performService() {
            System.out.println(this+"安装 引擎");
            assembler.car().addEngine();
        }
    }

    static class DriveTrainRobot extends Robot{
        public DriveTrainRobot(RobotPool pool) {
            super(pool);
        }

        @Override
        protected void performService() {
            System.out.println(this+"安装 动力装置");
            assembler.car().addDriveTrain();
        }
    }
    static class WheelRobot extends Robot{
        public WheelRobot(RobotPool pool) {
            super(pool);
        }

        @Override
        protected void performService() {
            System.out.println(this+"安装 轮子");
            assembler.car().addWheels();
        }
    }
}
