package com.concurrent.a_common;

/**
 * @author heshuai
 * @title: G_JoinThread
 * @description: 基本线程机制，演示java编程思想中21.2.11节案例
 *               线程的join方法
 * @date 2021年03月06日 12:32
 */
public class G_JoinThread {

    public static void main(String []args){
        Sleeper sleepy=new Sleeper("Sleepy",150);
        Sleeper grumpy=new Sleeper("Grumpy",150);
        Joiner dopey=new Joiner("Dopey",sleepy);
        Joiner doc=new Joiner("Doc",grumpy);
        grumpy.interrupt();
    }

    static class Sleeper extends Thread{
        private int duration;
        public Sleeper(String name, int sleepTime){
            super(name);
            duration=sleepTime;
            start();
        }
        @Override
        public void run(){
            try {
                sleep(duration);
            }catch (InterruptedException e){
                System.out.println(getName()+" was interrupted. "+"isInterrupted(): "+isInterrupted());
                return;
            }
            System.out.println(getName()+"join completed");
        }
    }

    static class Joiner extends Thread{
        private Sleeper sleeper;
        public Joiner(String name, Sleeper sleeper){
            super(name);
            this.sleeper=sleeper;
            start();
        }
        @Override
        public void run(){
            try {
                sleeper.join();
            }catch (InterruptedException e){
                System.out.println("Interrupted");
            }
            System.out.println(getName()+" join completed");
        }
    }

}
