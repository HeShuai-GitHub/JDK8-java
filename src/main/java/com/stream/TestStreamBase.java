package com.stream;

import org.junit.Test;

import java.awt.image.AreaAveragingScaleFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @program: JDK8-java
 * @description: 测试Stream流基础特性
 * @author: hs
 * @create: 2020-11-10 21:16
 **/
public class TestStreamBase {

    /**
     * forEach 循环
     */
    @Test
    public void foreach(){
        List<String> list = new ArrayList<>();
        list.add("bb");
        list.add("aa");
        list.add("cc");
        list.stream().forEach(value -> {
            System.out.println(value);
        });
    }
    /**
     * filter 过滤
     */
    @Test
    public void filter(){
        List<String> list = new ArrayList<>();
        list.add("bb");
        list.add("aa");
        list.add("cc");
        list.stream().filter(value -> {
            return value.equals("aa");
        }).forEach(value -> {
            System.out.println(value);
        });
    }

    /**
     * sorted 排序
     */
    @Test
    public void sorted(){
        List<String> list = new ArrayList<>();
        list.add("bb");
        list.add("aa");
        list.add("cc");
        System.out.println("操作前list：");
        list.stream().forEach(value -> {
            System.out.print(value+"\t");
        });
        List<String> collect = list.stream().sorted((a,b) -> {
            return a.compareTo(b);
        }).collect(Collectors.toList());
        System.out.println();
        System.out.println("升序排序："+collect);
        collect = list.stream().sorted((a, b) -> {
            return b.compareTo(a);
        }).collect(Collectors.toList());
        System.out.println("降序排序："+collect);
    }
    /**
     * list集合返回拼接字符串
     */
    @Test
    public void joinString(){
        List<String> list = new ArrayList<>();
        list.add("bb");
        list.add("aa");
        list.add("cc");
        System.out.println("操作前list："+list);
        String collect = list.stream().sorted((a, b) -> {
            return b.compareTo(a);
        }).collect(Collectors.joining(",","前","后"));
        System.out.println("操作后："+collect);
    }
    /**
     * map 匹配操作元素
     */
    @Test
    public void map(){
        List<String> list = new ArrayList<>();
        list.add("bb");
        list.add("aa");
        list.add("cc");
        System.out.println("操作前list："+list);
        List<String> collect = list.stream().map(value -> {
            if (value.equals("aa")){
                return "dd";
            }
            return value;
        }).collect(Collectors.toList());
        System.out.println("操作后："+collect);
    }

    /**
     * flatMap 合并多个数组
     */
    @Test
    public void flatMap(){
        List<String> list = new ArrayList<>();
        list.add("aa");
        list.add("cc");
        List<String> list1 = Arrays.asList("bb");
        System.out.println("操作前list："+list+"\t"+"list1："+list1);
        List<List> lists = Arrays.asList(list,list1);
        List<String> collect = (List<String>) lists.stream().flatMap(value -> value.stream()).collect(Collectors.toList());
        System.out.println("操作后："+collect);
    }

    /**
     * reduce 折叠操作，可以对stream中所有的值进行操作
     */
    @Test
    public void reduce(){
        List<Integer> list = Arrays.asList(1,2,3,4,5,6,7,8,9);
        Integer sum = list.stream().reduce((value,count) ->{
            System.out.println("value:"+value+"，count："+count);
           return value += count;
        }).get();
        System.out.println(sum);

        System.out.println("***************");
        // 字符串连接，concat = "ABCD"
        String concat = Stream.of("A", "B", "C", "D").reduce("", String::concat);
        // 求最小值，minValue = -3.0
        double minValue = Stream.of(-1.5, 1.0, -3.0, -2.0).reduce(Double.MAX_VALUE, Double::min);
        // 求和，sumValue = 10, 有起始值
        int sumValue = Stream.of(1, 2, 3, 4).reduce(0, Integer::sum);
        // 求和，sumValue = 10, 无起始值
        sumValue = Stream.of(1, 2, 3, 4).reduce(Integer::sum).get();
        // 过滤，字符串连接，concat = "ace"
        concat = Stream.of("a", "B", "c", "D", "e", "F").
                filter(x -> x.compareTo("Z") > 0).
                reduce("", String::concat);
    }

}
