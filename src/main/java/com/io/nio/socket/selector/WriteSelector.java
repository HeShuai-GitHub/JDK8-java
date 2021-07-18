package com.io.nio.socket.selector;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @author heshuai
 * @title: WriteSelector
 * @description: 读操作的多路复用器
 * @date 2021年07月17日 18:11
 */
public class WriteSelector implements Runnable{

    private final Selector thisSelector;

    public WriteSelector(Selector thisSelector) {
        this.thisSelector = thisSelector;
    }

    @Override
    public void run() {
        while (true) {
            try {
                // 返回已经准备好并且感兴趣的selectedKeys数量
                if (thisSelector.select(1000) == 0) {
                    continue;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Set<SelectionKey> selectionKeys = thisSelector.selectedKeys();
            Iterator<SelectionKey> keyIterator = selectionKeys.iterator();
            while (keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();
                try {
                    // 该key的附件，这里放逻辑处理后的返回值
                    String responseData = String.valueOf(key.attachment());
                    if (key.isValid() && key.isWritable() && (!isStringEmpty(responseData))) {
                        SocketChannel clientChannel = (SocketChannel) key.channel();
                        clientChannel.write(ByteBuffer.wrap(responseData.getBytes()));
                    }
                    key.cancel();
                    keyIterator.remove();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    key.cancel();
                    try {
                        System.out.println("closed.......");
                        key.channel().close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private Boolean isStringEmpty(String data) {
        return null == data || "".equals(data);
    }
}
