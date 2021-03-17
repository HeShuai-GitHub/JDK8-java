package com.concurrent.b_sharedLimitedResource;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author heshuai
 * @title: A_SynchronizedBlockade
 * @description: 基本线程机制，演示java编程思想中21.3.2节案例
 *              线程的同步锁分为类锁和对象锁，即class-lock、object-lock，这两个锁之间是不会互相产生影响的，是两个级别的的同步锁，分别对类方法加锁、对象方法加锁。
 *              类所有方法同享同一个锁，即进行一个同步锁的方法后，其他线程不可以访问这个类的其他加锁的方法，只有当前线程可以访问，并且每次访问在内部中都将锁的数量加1，每退出一个同步锁方法，就会减1，当到0的时候就会释放锁。这个对象锁也是一样的机制。
 *              注意：这讨论的都是加锁方法的互斥，未加锁的方法是不受影响的。
 * @date 2021年03月06日 13:12
 */
public class A_SynchronizedBlockade {

    static Z_BlockExample z_blockExample = new Z_BlockExample();
    static ExecutorService service = Executors.newCachedThreadPool();

    public static void main(String[] args) throws InterruptedException {
        testObjectLock();
//        testClassLock();
        service.shutdown();
    }

    /**
     * 测试类级锁
     */
    static void testClassLock(){
        service.execute(()->{
            try {
                System.out.println("*********线程一，开始执行......*********");
                A_SynchronizedBlockade.classLockBlockade();
                System.out.println("*********线程一，执行完毕*********");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        service.execute(()->{
            try {
                System.out.println("*********线程二，开始执行......*********");
                A_SynchronizedBlockade.classLockAttempt();
                System.out.println("*********线程二，执行完毕*********");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        // 测试加类锁后是否可以使用对象方法——结果，不受影响
        service.execute(()->{
            System.out.println("*********线程三，开始执行......*********");
            try {
                objectLockBlockade();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("*********线程三，执行完毕*********");
        });
        // 测试加类锁后是否可以使用类属性——结果，不受影响
        service.execute(()->{
            System.out.println("*********线程四，开始执行......*********");
            Z_BlockExample.age = "18";
            System.out.println(Z_BlockExample.age);
            System.out.println("*********线程四，执行完毕*********");
        });

        // 测试加类锁后是否可以使用未加锁的类方法——结果，不受影响
        service.execute(()->{
            System.out.println("*********线程五，开始执行......*********");
            try {
                classUnlockAttempt();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("*********线程五，执行完毕*********");
        });
    }

    public static void classLockBlockade() throws InterruptedException {
        Z_BlockExample.setRole("测试人员");
    }

    public static void classLockAttempt() throws InterruptedException {
        System.out.println(Z_BlockExample.getRole());
    }

    public static void classUnlockAttempt() throws InterruptedException {
        Z_BlockExample.setAge("18");
    }

    /**
     * 测试对象级锁
     */
    static void testObjectLock(){
        service.execute(()->{
            try {
                System.out.println("*********线程一，开始执行......*********");
                A_SynchronizedBlockade.objectLockBlockade();
                System.out.println("*********线程一，执行完毕*********");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        service.execute(()->{
            try {
                System.out.println("*********线程二，开始执行......*********");
                A_SynchronizedBlockade.objectLockAttempt();
                System.out.println("*********线程二，执行完毕*********");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        // 测试加对象锁后是否可以使用类属性——结果，不受影响
        service.execute(()->{
            System.out.println("*********线程三，开始执行......*********");
            Z_BlockExample.role = "测试人员";
            System.out.println(Z_BlockExample.role);
            System.out.println("*********线程三，执行完毕*********");
        });
        // 测试加对象锁后是否可以使用类方法——结果，不受影响
        service.execute(()->{
            try {
                System.out.println("*********线程四，开始执行......*********");
                System.out.println(Z_BlockExample.getRole());
                System.out.println("*********线程四，执行完毕*********");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        // 测试加对象锁后是否可以使用未加锁的对象方法——结果，不受影响
        service.execute(()->{
            System.out.println("*********线程五，开始执行......*********");
            try {
                objectUnlockAttempt();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("*********线程五，执行完毕*********");
        });
    }



    public static void objectLockBlockade() throws InterruptedException {
        z_blockExample.setName("张三");
    }

    public static void objectLockAttempt() throws InterruptedException {
        System.out.println(z_blockExample.getName());
    }
    public static void objectUnlockAttempt() throws InterruptedException {
        z_blockExample.setSex("男");
    }

}
