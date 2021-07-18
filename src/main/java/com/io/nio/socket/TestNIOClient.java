package com.io.nio.socket;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.TimeUnit;

/**
 * @author heshuai
 * @title: TestNIOClient
 * @description: TODO
 * @date 2021年07月17日 23:45
 */
public class TestNIOClient {

    public static void main(String[] args) {
        byte[] data = "hello".getBytes();
        SocketChannel channel = null;
        try {
            channel = SocketChannel.open();
            channel.configureBlocking(false);
            // connect 连接一个socket端口，在nio模式下，如果socket连接立即建立成功，则返回true；如果正在建立，则返回false;
            // 在bio模式下，会阻塞直到连接建立或者发生异常
            if (!channel.connect(new InetSocketAddress(InetAddress.getLocalHost(), 8090))) {
                // 在一个nio中，建立连接后需要使用finishConnect去完成一个连接
                // 1. 如果连接建立成功，调用该方法返回true
                // 2. 连接建立失败，则抛出IOException异常
                // 3. 连接建立未完成，则返回false;
                // 在bio这一步中会阻塞流程并且直到连接建立完成，而nio会理解返回一个当前连接的状态结果
                while (!channel.finishConnect()) {
                    System.out.print(".");
                }
            }
            System.out.println("Connected to server...");
            ByteBuffer writeBuffer = ByteBuffer.wrap(data);
            ByteBuffer readBuffer = ByteBuffer.allocate(1024);
            int bytesReceived;
            while (true) {
                if (writeBuffer.hasRemaining()) {
                    channel.write(writeBuffer);
                }
                if ((channel.read(readBuffer)) == -1) {
                    break;
                }
                TimeUnit.SECONDS.sleep(1);
                System.out.print(".");
            }
            readBuffer.flip();
            System.out.println("Server said: " + new String(readBuffer.array()));
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                if (channel != null) {
                    channel.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
