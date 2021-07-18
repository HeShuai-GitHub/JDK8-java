package com.io.nio.socket.selector;

import com.io.nio.socket.CustomizedThreadPool;
import com.io.nio.socket.service.selected.SelectedServiceHandler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author heshuai
 * @title: ReadSelector
 * @description: 监听read事件的多路复用器
 * @date 2021年07月17日 18:10
 */
public class ReadSelector implements Runnable{
    private final Selector thisSelector;

    private final Selector writeSelector;

    public ReadSelector(Selector thisSelector, Selector  writeSelector) {
        this.thisSelector = thisSelector;
        this.writeSelector = writeSelector;
    }

    @Override
    public void run() {
        while (true) {
            try {
                // 返回已经准备好并且感兴趣的selectedKeys数量
                if (thisSelector.select(1000) == 0) {
                    continue;
                }
                // 返回已经准备好并且感兴趣的selectedKeys集合
                Set<SelectionKey> selectionKeys = this.thisSelector.selectedKeys();
                Iterator<SelectionKey> keyIterator = selectionKeys.iterator();
                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();
                    if (key.isReadable()) {
                        SocketChannel clientChannel = (SocketChannel) key.channel();
                        // 分配缓存区
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        StringBuilder readData = new StringBuilder();
                        // 一次性将请求内容全部读取到buffer中，在进行处理和写入操作；一边处理一边处理一边写暂未实现
                        while (clientChannel.read(buffer) > 0) {
                            readData.append(new String(buffer.array()));
                            buffer.clear();
                        }
                        CustomizedThreadPool.writeHandlerSubmit(new SelectedServiceHandler(readData.toString(), this.writeSelector, clientChannel));
                        // 解除该selectionKey和Selector之间的关系，并将它加入到该selector的cancelled-key set中，随后下次Selector操作将这个key从key sets中移除
                        key.cancel();
                        keyIterator.remove();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
