package com.io.bio.socket;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;

/**
 * @author heshuai
 * @title: SimpleSocketRunable
 * @description: bio简单处理模型
 * @date 2021年07月18日 21:05
 */
public class SimpleSocketRunnable implements Runnable{

    private final Socket socket;

    public SimpleSocketRunnable(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            System.out.println("开始处理...");
            // 输入流-客户端的数据   InputStream是所有输入流的父类
            InputStream inputStream = this.socket.getInputStream();
            // 输出流-处理后传给的数据   OutputStream是所有输出流的父类
            OutputStream outputStream = this.socket.getOutputStream();
            // InputStreamReader 将字节流按照指定格式转换为字符流
            // 最佳实现方式 new BufferedReader(new InputStreamReader());
            BufferedReader bis = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("utf-8")));
            String requestData = "";
            System.out.println("请求数据是：");
            // 读取一行，以\n \r为结束符，若未读取到\n、\r或者EOF（end of file），则会一直等待读取
            while ((requestData = bis.readLine()) != null) {
                System.out.println(requestData);
                // 遇到end结束读取
                if (requestData.length() >= 3 && requestData.substring(0,3).equals("end")){
                    break;
                }
            }
            outputStream.write("welcome\n".getBytes());
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != socket) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
