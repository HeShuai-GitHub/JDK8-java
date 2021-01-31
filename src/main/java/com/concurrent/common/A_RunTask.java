package com.concurrent.common;

/**
 * @program: JDK8-java
 * @description: 基本线程机制，演示java编程思想中21.2.1节案例
 *              实现Runnable接口来定义一个任务，任务本身不具有线程能力，必须显示的将任务依附在线程上才可以
 * @author: hs
 * @create: 2021-01-11 21:18
 **/
public class A_RunTask implements Runnable{
    protected int countDown=10;
    private static int taskCount=0;
    private final int id=taskCount++;
    public A_RunTask(){}
    public A_RunTask(int countDown){
        this.countDown=countDown;
    }

    public String status(){
        return "#"+id+"("+(countDown>0?countDown:"Liftoff!")+"),";
    }

    @Override
    public void run() {

        while (countDown-- >0){
            System.out.print(status());
            Thread.yield();
        }

    }

    /**
     * 使用main方法去执行run方法，这相当于将run方法依附在main线程上，这是顺序执行，并没有创建新的线程
     */
    public static void main(String[] args) {
        A_RunTask task = new A_RunTask();
        task.run();
    }
}
