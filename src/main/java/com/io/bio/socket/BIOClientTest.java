package com.io.bio.socket;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * @author heshuai
 * @title: NIOClientTest
 * @description: NIO 客户端简单模拟
 * @date 2021年07月18日 21:51
 */
public class BIOClientTest {

    public static void main(String[] args) throws IOException {
        try (Socket socket = new Socket();){
            // 连接
            socket.connect(new InetSocketAddress(InetAddress.getLocalHost(), 8090));
            OutputStream out = socket.getOutputStream();
            InputStream in = socket.getInputStream();
            // 从控制台输入内容作为参数
            BufferedReader dataByKey = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                String requestData = dataByKey.readLine();
                // 加上每一行结束符 \n
                out.write(requestData.concat("\n").getBytes());
                if (requestData.length() >= 3 && requestData.substring(0,3).equals("end")){
                    break;
                }
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            System.out.println("服务端响应: ");
            String responseData = reader.readLine();
            System.out.println(responseData);
        }
    }

}
