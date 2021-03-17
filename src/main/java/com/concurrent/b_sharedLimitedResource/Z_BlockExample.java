package com.concurrent.b_sharedLimitedResource;

import java.util.concurrent.TimeUnit;

/**
 * @author heshuai
 * @title: Z_BlockExample
 * @description: 锁实例类，只是为了实例锁的各种情况下的处理方式，该类无实际用处
 * @date 2021年03月06日 17:17
 */
public class Z_BlockExample {

    private String name;

    private String sex;

    public static String age;

    public static String role;

    public synchronized String getName() throws InterruptedException {
        System.out.println("getName方法正在执行......");
        TimeUnit.SECONDS.sleep(1);
        System.out.println("getName方法已完成！");
        return name;
    }

    public synchronized void setName(String name) throws InterruptedException {
        System.out.println("setName方法正在执行......");
        TimeUnit.SECONDS.sleep(1);
        System.out.println("setName方法已完成！");
        this.name = name;
    }

    public String getSex() throws InterruptedException {
        System.out.println("getSex方法正在执行......");
        TimeUnit.SECONDS.sleep(1);
        System.out.println("getSex方法已完成！");
        return sex;
    }

    public void setSex(String sex) throws InterruptedException {
        System.out.println("setSex方法正在执行......");
        TimeUnit.SECONDS.sleep(1);
        System.out.println("setSex方法已完成！");
        this.sex = sex;
    }

    public static String getAge() throws InterruptedException {
        System.out.println("getAge方法正在执行......");
        TimeUnit.SECONDS.sleep(1);
        System.out.println("getAge方法已完成！");
        return age;
    }

    public static void setAge(String age) throws InterruptedException {
        System.out.println("setAge方法正在执行......");
        TimeUnit.SECONDS.sleep(1);
        System.out.println("setAge方法已完成！");
        Z_BlockExample.age = age;
    }

    public synchronized static String getRole() throws InterruptedException {
        System.out.println("getRole方法正在执行......");
        TimeUnit.SECONDS.sleep(1);
        System.out.println("getRole方法已完成！");
        return role;
    }

    public synchronized static void setRole(String role) throws InterruptedException {
        System.out.println("setRole方法正在执行......");
        TimeUnit.SECONDS.sleep(1);
        System.out.println("setRole方法已完成！");
        Z_BlockExample.role = role;
    }
}
