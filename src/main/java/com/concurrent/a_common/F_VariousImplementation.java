package com.concurrent.a_common;

import java.util.concurrent.TimeUnit;

/**
 * @author heshuai
 * @title: F_VariousImplementation
 * @description: 基本线程机制，演示java编程思想中21.2.9节案例
 *               这一节比较简单，只是各种实现线程的方法
 * @date 2021年01月31日 23:11
 */
public class F_VariousImplementation {
    public static void main(String []args){
        new InnerThread1("InnerThread1");
        new InnerThread2("InnerThread2");
        new InnerRunnable1("InnerThread1");
        new InnerRunnable2("InnerThread2");
        new ThreadMethod("ThreadMethod").runTask();
    }


    /**
     * 使用有名内部类的方式声明线程
     */
    static class InnerThread1{
        private int countDown=5;
        private Inner inner;
        private class Inner extends Thread{
            Inner(String name){
                super(name);
                start();
            }
            @Override
            public void run(){
                try{
                    while(true){
                        System.out.print(this);
                        if(--countDown ==0){
                            return;
                        }
                        Thread.sleep(10);
                    }
                }catch (InterruptedException e){
                    System.out.print("Interrupted");
                }
            }
            @Override
            public String toString(){
                return getName()+":"+countDown;
            }
        }
        public InnerThread1(String name){
            inner=new Inner(name);
        }
    }

    /**
     * 使用匿名内部类的方式声明线程
     */
    static class InnerThread2{
        private int countDown=5;
        private Thread t;

        public InnerThread2(String name){
            t=new Thread(name){
                @Override
                public void run(){
                    try{
                        while(true){
                            System.out.print(this);
                            if(--countDown ==0){
                                return;
                            }
                            Thread.sleep(10);
                        }
                    }catch (InterruptedException e){
                        System.out.print("Interrupted");
                    }
                }
                @Override
                public String toString(){
                    return getName()+":"+countDown;
                }
            };
            t.start();
        }

    }

    /**
     * 实现Runnable的内部类
     */
    static class  InnerRunnable1{
        private int countDown=5;
        private Inner inner;
        private class Inner implements Runnable{
            Thread t;
            Inner(String name){
                t=new Thread(this,name);
                t.start();
            }
            @Override
            public void run(){
                try{
                    while(true){
                        System.out.print(this);
                        if(--countDown ==0){
                            return;
                        }
                        TimeUnit.MILLISECONDS.sleep(10);
                    }
                }catch (InterruptedException e){
                    System.out.print("Interrupted");
                }
            }
            @Override
            public String toString(){
                return t.getName()+":"+countDown;
            }
        }
        public InnerRunnable1(String name){
            inner=new Inner(name);
        }
    }

    /**
     * 使用匿名内部类声明Runna的方式声明线程
     */
    static class InnerRunnable2{
        private int countDown=5;
        private Thread t;
        public InnerRunnable2(String name){
            t=new Thread(new Runnable() {
                @Override
                public void run(){
                    try{
                        while(true){
                            System.out.print(this);
                            if(--countDown ==0){
                                return;
                            }
                            TimeUnit.MILLISECONDS.sleep(10);
                        }
                    }catch (InterruptedException e){
                        System.out.print("Interrupted");
                    }
                }
                @Override
                public String toString(){
                    return Thread.currentThread().getName()+":"+countDown;
                }
            },name);
            t.start();
        }
    }

    /**
     * 使用独立的方法去运行多线程的代码
     */
    static class ThreadMethod{
        private int countDown=5;
        private Thread t;
        private String name;
        public ThreadMethod(String name){
            this.name=name;
        }
        public void runTask(){
            if(t==null){
                t=new Thread(name){
                    @Override
                    public void run(){
                        try{
                            while(true){
                                System.out.print(this);
                                if(--countDown==0){
                                    return;
                                }
                                sleep(10);
                            }
                        }catch (InterruptedException e){
                            System.out.print("sleep() Interrupted");
                        }
                    }
                    @Override
                    public String toString(){
                        return getName()+":"+countDown;
                    }
                };
                t.start();
            }
        }
    }
}
