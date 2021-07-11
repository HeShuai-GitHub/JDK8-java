package com.concurrent.f_concurrentClass;

import java.util.concurrent.Exchanger;

/**
 * @author heshuai
 * @title: H_Exchanger
 * @description: 线程机制，演示java编程思想中21.7.7节案例
 *              下面Exchanger的一个案例，但是并不是书中的案例，而是jdk注释的案例，书中的案例不知道为什么有一个类获取不到，所以简单写了一个简单的案例
 *              Exchanger：交换机，保障两个线程之间安全的交换对象。核心方法exchange，当一个线程将对象放入这个方法后，将阻塞等待另一个线程调用这个方法并将自己的对象也放入方法参数中，然后第一个线程将
 *              返回第二个线程放入的参数，第二个线程返回第一个线程放入的参数，以此完成交换。
 * @date 2021年03月25日 0:06
 */
public class H_Exchanger {
    static Exchanger<DataBuffer> exchanger = new Exchanger<DataBuffer>();

    public static void main(String[] args) {
        new Thread(new FillingLoop()).start();
        new Thread(new EmptyingLoop()).start();
    }

    static class FillingLoop implements Runnable {
      public void run() {
        DataBuffer currentBuffer = new DataBuffer("full");
        try {
            System.out.println("FillingLoop 开始前："+currentBuffer);
            if (currentBuffer.isFull()){
                // 等待交换对象，若另一个线程没有到达交换点，则阻塞等待
                currentBuffer = exchanger.exchange(currentBuffer);
                System.out.println("FillingLoop 交换后："+currentBuffer);
            }else {
                Thread.currentThread().interrupt();
            }
        } catch (InterruptedException ex) {
            System.out.println("FillingLoop 被中断");
        }
      }
    }

    static class EmptyingLoop implements Runnable {
        public void run() {
            DataBuffer currentBuffer =  new DataBuffer("");
            try {
                System.out.println("EmptyingLoop 开始前："+currentBuffer);
                if (currentBuffer.isEmpty()) {
                  currentBuffer = exchanger.exchange(currentBuffer);
                  System.out.println("EmptyingLoop 交换后："+currentBuffer);
                }else {
                    Thread.currentThread().interrupt();
                }
            } catch (InterruptedException ex) {
                System.out.println("FillingLoop 被中断");
            }
        }
    }

    static class DataBuffer {
       private String state;

        public DataBuffer(String state) {
            this.state = state;
        }

        public boolean isFull() {
            return state.equals("full") ;
        }

        public boolean isEmpty() {
            return state.equals("");
        }

        @Override
        public String toString() {
            return "DataBuffer{" +
                    "state='" + state + '\'' +
                    '}';
        }
    }
}
