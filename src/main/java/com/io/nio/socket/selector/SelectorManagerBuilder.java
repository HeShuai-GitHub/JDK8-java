package com.io.nio.socket.selector;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.spi.SelectorProvider;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author heshuai
 * @title: SelectorManagerBuilder
 * @description: 构建一个SelectorManager
 * @date 2021年07月17日 17:46
 */
public class SelectorManagerBuilder {


    public static SelectorManager build(int port, int readAndWriteSelectorAccount) throws IOException {
        if (readAndWriteSelectorAccount == 0) {
            throw new IllegalArgumentException("readAndWriteSelectorAccount 不可以为0");
        }
        return new SelectorManager(port, readAndWriteSelectorAccount);
    }


}
