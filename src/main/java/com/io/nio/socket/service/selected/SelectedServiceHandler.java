package com.io.nio.socket.service.selected;

import com.io.nio.socket.service.impl.SimpleServiceImpl;

import javax.sql.RowSet;
import java.io.IOException;
import java.net.SocketAddress;
import java.net.http.HttpResponse;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * @author heshuai
 * @title: SelectedServiceHandler
 * @description: 处理请求报文并将它发送到不同的处理器
 * @date 2021年07月17日 21:26
 */
public class SelectedServiceHandler implements Runnable{


    private final String requestData;

    private final Selector writeSelector;

    private final SocketChannel clientChannel;

    public SelectedServiceHandler(String requestData, Selector writeSelector, SocketChannel clientChannel) {
        this.requestData = requestData;
        this.writeSelector = writeSelector;
        this.clientChannel = clientChannel;
    }

    @Override
    public void run() {
        try {
            System.out.println("请求地址为：" + clientChannel.getRemoteAddress());
            String responseData = doGet();
            clientChannel.register(writeSelector, SelectionKey.OP_WRITE, responseData);
            writeSelector.wakeup();
        } catch (ClosedChannelException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String doGet() {
        SimpleServiceImpl simpleService = new SimpleServiceImpl();
        return simpleService.handler(requestData);
    }
}
