package com.lambda;

import org.junit.Test;

import java.sql.ClientInfoStatus;
import java.util.*;
import java.util.function.Predicate;

/**
 * @program: JDK8-java
 * @description: Lambda 测试集合
 * @create: 2020-11-10 16:15
 **/
public class TestCollection {

    private final static List<String> list =new ArrayList<>();

    private final static Map<String,Object> map = new HashMap<>();

    static {
        list.add("aa");
        list.add("bbb");
        list.add("1");
    }

    /**
     * Lambda 遍历
     */
    @Test
    public void listForEach(){
        list.forEach(s -> {
            System.out.print(s+",");
        });
        System.out.println("\n引用遍历");
        list.forEach(System.out::print);
    }

    /**
     *  Predicate 的and, or, xor使用，为逻辑判断
     */
    @Test
    public void Predicate() {
        Predicate<String> startWithJ = (n) -> n.startsWith("J");
        Predicate<String> fourLength = (n) -> n.length() == 4;

        List<String> languages = Arrays.asList("Java", "Scala", "C++", "Haskell", "Lisp","J++");
        languages.stream().filter(startWithJ.and(fourLength))
                .forEach(System.out::println);
    }

}
