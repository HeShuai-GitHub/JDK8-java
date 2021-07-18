package com.io.nio.socket.selector;

import com.io.nio.socket.CustomizedThreadPool;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author heshuai
 * @title: SelectorManager
 * @description: 多路复用器控制器
 * @date 2021年07月17日 17:47
 */
public class SelectorManager {
    // 关注read事件的selector的集合
    private final static List<Selector> readSelectors = new ArrayList<>();
    // 关注write事件的selector的集合
    private final static List<Selector> writeSelectors = new ArrayList<>();

    private final int PORT;

    public SelectorManager(int port, int readAndWriteSelectorAccount) throws IOException {
        this.PORT = port;
        for (int i = 0; i < readAndWriteSelectorAccount; i++) {
            // 初始化写相关多路复用器
            Selector writeSelector = Selector.open();
            CustomizedThreadPool.sockedHandlerSubmit(new WriteSelector(writeSelector));
            writeSelectors.add(writeSelector);
            // 初始化读相关多路复用器
            Selector readSelector = Selector.open();
            CustomizedThreadPool.sockedHandlerSubmit(new ReadSelector(readSelector, writeSelector));
            readSelectors.add(readSelector);
        }
    }

    public void startNIO() throws IOException {
        // 多路复用器
        Selector acceptSelector = Selector.open();
        // 服务端通道
        ServerSocketChannel ssc = ServerSocketChannel.open();
        // 设置为非阻塞
        ssc.configureBlocking(false);
        // 监听本地端口
        ssc.bind(new InetSocketAddress(this.PORT));
        ssc.register(acceptSelector, SelectionKey.OP_ACCEPT);
        int i = 0;
        while (true) {
            // 返回已经准备好并且感兴趣的selectedKeys数量
            if (acceptSelector.selectNow() == 0) {
                continue;
            }
            // 返回已经准备好并且感兴趣的selectedKeys集合
            Iterator<SelectionKey> keyIterator = acceptSelector.selectedKeys().iterator();
            while (keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();
                if (key.isAcceptable()) {
                    ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
                    // 接受客户端请求
                    SocketChannel socketChannel = serverChannel.accept();
                    socketChannel.configureBlocking(false);
                    Selector readSelector = readSelectors.get(i % readSelectors.size());
                    // 关注Read事件
                    socketChannel.register(readSelector,SelectionKey.OP_READ);
                    // 将当前的selectorKey从selectedKeys移除，就不会重复触发accept事件了；
                    // 除非再次有请求到达触发该强求
                    keyIterator.remove();
                    // 唤醒read多路复用器所在线程，减少线程等待时间
                    readSelector.wakeup();
                    i++;
                    if (i == Integer.MAX_VALUE - 1) {
                        i = 0;
                    }
                }
            }
        }
    }


}
