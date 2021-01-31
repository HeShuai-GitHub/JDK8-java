package com.concurrent.common;

/**
 * @program: JDK8-java
 * @description: 基本线程机制，演示java编程思想中21.2.2节案例
 *               创建新线程，并在线程中执行run方法，当然Thread本身也可以继承Thread，而不需要实现Runnable来实现线程的创建运行，但是这个不利于程序面向对象的思想并且Thread其实就是实现的Runnable
 * @author: hs
 * @create: 2021-01-11 21:30
 **/
public class B_SimpleThread{

    public static void main(String[] args) {
        // 创建新线程
        Thread thread = new Thread(new A_RunTask());
        // 启动线程，并快速返回，不需要等待线程执行完成后返回，因此在大多数系统的返回结果上都会显示main线程先执行完毕，A_RunTask由新线程执行
        thread.start();
        System.out.println("等待A_RunTask.run()的执行");
    }

    /**
     * 通过内部类来继承Thread并实现运行，除了内部类外，还有匿名内部类的方式去创建新的线程，这个将在21.2.9中看到
     */
    static class InnerThread extends Thread{

        @Override
        public void run() {
            System.out.println("内部类继承Thread实现线程创建及运行");
        }

        public static void main(String[] args) {
            new InnerThread().start();
            System.out.println("等待InnerThread.run()的运行");
        }
    }


}
