package com.io.nio.socket.service.impl;

/**
 * @author heshuai
 * @title: SimpleService
 * @description: TODO
 * @date 2021年07月17日 21:41
 */
public class SimpleServiceImpl {

    public String handler(String requestData){
        System.out.println("开始业务处理...");
        // 业务处理
        System.out.println("请求数据为：");
        System.out.println("*************************");
        System.out.println(requestData);
        System.out.println("*************************");
        return "Welcome!!!";
    }

}
