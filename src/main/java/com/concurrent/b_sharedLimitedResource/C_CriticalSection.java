package com.concurrent.b_sharedLimitedResource;

/**
 * @author heshuai
 * @title: C_CriticalSection
 * @description: 临界区，这里很简单和之前的synchronized方法基本一致，只是范围大小的区别，这里主要介绍的是在其他对象上同步的一个错误形式（无法锁住指定资源）
 *              下面是一个错误的示范，主要是指假如在临界区内的对象锁弄错了，那么很可能不能达到阻止资源竞争的作用
 * @date 2021年03月17日 22:11
 */
public class C_CriticalSection {

    private Object syncObject = new Object();

    public synchronized void f(String str){
        for(int i=0; i<5; i++){
            System.out.printf("f()%s\n",str);
            Thread.yield();
        }
    }

    public void g(String str){
        synchronized (syncObject) {
            for(int i=0; i<5; i++){
                System.out.printf("g()%s\n",str);
                Thread.yield();
            }
        }
    }

    public static void main(String[] args) {
        C_CriticalSection c_criticalSection = new C_CriticalSection();
        new Thread() {
            @Override
            public void run() {
                c_criticalSection.f("1");
            }
        }.start();
        c_criticalSection.g("2");
    }

}
