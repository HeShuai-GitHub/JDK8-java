package com.concurrent.d_threadCooperation;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author heshuai
 * @title: B_ProducerAndConsumer
 * @description: 本线程机制，演示java编程思想中21.5.3节案例
 *               通过仿真餐厅厨师做菜服务员取菜的过程来阐述生产者消费者打交道的过程
 * @date 2021年03月20日 22:36
 */
public class B_ProducerAndConsumer {

    // 菜
    static class Meal {
        private final int orderNum;

        public Meal(int orderNum) {
            this.orderNum = orderNum;
        }

        @Override
        public String toString() {
            return "Meal{" +
                    "orderNum=" + orderNum +
                    '}';
        }
    }

    // 服务生
    static class WaitPerson implements Runnable{

        private Restaurant restaurant;

        public WaitPerson(Restaurant restaurant) {
            this.restaurant = restaurant;
        }

        @Override
        public void run() {
            try {
                while (!Thread.currentThread().isInterrupted()){
                    synchronized (this){
                        // 判断当前的菜是否已经做完，若还没做完则挂起
                        while (this.restaurant.meal== null){
                            wait();
                        }
                    }
                    System.out.println("服务员拿到餐..."+this.restaurant.meal);
                    // 同步厨师，判断厨师是否正在尝试做菜，若是则等待厨师尝试失败后挂起，然后拿到锁将餐取走，并通知厨师
                    synchronized (this.restaurant.chef) {
                        this.restaurant.meal=null;
                        this.restaurant.chef.notifyAll();
                    }
                }
            }catch (InterruptedException e){
                System.out.println("WaitPerson 被中断");
            }
        }
    }

    static class Chef implements Runnable{

        private Restaurant restaurant;

        private int count = 0;

        public Chef(Restaurant restaurant) {
            this.restaurant = restaurant;
        }

        @Override
        public void run() {
            try {
                while (!Thread.currentThread().isInterrupted()){
                    synchronized (this){
                        // 若餐还在取餐口，则挂起。只有取餐口没有餐才开始做菜
                        while (this.restaurant.meal!= null){
                            wait();
                        }
                    }
                    // 做完九道菜，将不再做菜
                    if(++count == 10){
                        System.out.println("已经做完，不再做菜");
                        this.restaurant.service.shutdownNow();
                    }else{
                        System.out.println("开始做菜...");
                    }
                    // 尝试获取服务生的锁，若服务生挂起则将餐放到取餐口，并通知服务生
                    synchronized (this.restaurant.waitPerson) {
                        this.restaurant.meal=new Meal(count);
                        this.restaurant.waitPerson.notifyAll();
                    }
                    // 中断后，在这里抛出InterruptedException异常结束线程
                    TimeUnit.MILLISECONDS.sleep(100);
                }
            }catch (InterruptedException e){
                System.out.println("Chef 被中断");
            }
        }
    }

    static class Restaurant {
        Meal meal;
        private WaitPerson waitPerson = new WaitPerson(this);
        private Chef chef = new Chef(this);
        ExecutorService service = Executors.newCachedThreadPool();

        public Restaurant() {
            System.out.println("开始执行");
            service.execute(waitPerson);
            service.execute(chef);
        }

        public static void main(String[] args) {
            new Restaurant();
        }
    }
}
