package com.lambda;

import org.junit.Test;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * @program: JDK8-java
 * @description: 测试Lambda 基础使用
 *            * 内置的四个函数式接口
 *  *              消费型接口Consumer<T>：有入参，无返参
 *  *              供给型接口Supplier<T>：无入参，有返参
 *  *              函数式接口Function<T,R>：有入参，有返参
 *  *              断言型接口Predicate<T>：有入参，返参为boolean型
 * @create: 2020-11-10 11:55
 **/
public class TestBase {
    /**
     * //函数式接口：接口中只有一个抽象方法的接口，通过注解@Functioninterface 限定
     *  使用lambda表达式的前提：接口（函数式接口）中只能有一个抽象方法。
     */
    @Test
    public void base(){
        Comparator<Integer> com = (x, y)->Integer.compare(x,y);
        System.out.println(com.compare(1,2));
    }

    /**
     * 经典实现 Runable接口
     */
    @Test
    public void runable(){
        new Thread( () -> System.out.println("无参，无返回值") ).start();
    }

    /**
     * Lambda 隐式返回
     *      当方法体只有一句语句时，可以省略掉return 与大括号
     */
    @Test
    public void implicitReturn(){
        Supplier<Integer> su = ()-> {
            return new Random().nextInt(100);
        };
        System.out.println("显示返回："+su.get());

        su = ()->new Random().nextInt(100);
        System.out.println("隐式返回："+su.get());
    }

    /**
     * Lambda 四大函数接口
     */

    /**
     * 消费型接口Consumer<T>
     *     con.accept触发lambda实现方法
     *     抽象方法：void accept(T t);
     */
    @Test
    public void consumer(){
        Consumer<Double> con = (x)-> {
            System.out.printf("参数是：%f,开始执行\n",x);
            System.out.println(x);
        };
        con.accept(10D);

    }

    /**
     * 供给型接口Supplier<T>
     *     su.get():返回值
     *     抽象方法：T get();
     */
    @Test
    public void provider(){
        Supplier<Integer> su = ()->new Random().nextInt(100);
        System.out.println("provider："+su.get());

    }

    /**
     * 函数式接口Function<T,R>
     *     有入参和返参，处理一些方法逻辑问题
     *     抽象方法：R apply(T t);
     */
    @Test
    public void function(){
        Function<String, String> fun = x-> x.toUpperCase();
        System.out.println("返回值："+fun.apply("a"));
    }

    /**
     * 测试断言型接口Predicate<T>
     *     返回值为boolean
     */
    @Test
    public void predicate(){
        Predicate<String> pre = s -> s.length() > 2;
        System.out.println("Boolean："+pre.test("sss"));
        System.out.println("Boolean："+pre.test("ss"));
    }

    /**
     * 方法引用：当函数实现已经存在，也就是另外一个类已有的实现时，可以使用方法引用
     */
    @Test
    public void quote(){
        Date date = new Date();
        Supplier<Long> supplier = date::getTime;
        System.out.println(supplier.get());
    }

}
