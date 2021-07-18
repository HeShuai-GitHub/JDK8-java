package com.io.nio.socket;

import com.io.nio.socket.selector.SelectorManager;
import com.io.nio.socket.selector.SelectorManagerBuilder;

import java.io.IOException;

/**
 * @author heshuai
 * @title: TestNIO
 * @description: 测试自定义NIO模型
 * @date 2021年07月17日 22:20
 */
public class TestNIO {

    public static void main(String[] args) throws IOException {
        SelectorManager selectorManager = SelectorManagerBuilder.build(8090,  4);
        selectorManager.startNIO();
    }

}
