package com.io.bufferAndDisk;

import org.junit.Test;

import java.io.IOException;
import java.util.Random;

/**
 * @author heshuai
 * @title: CheckBufferAndDisk
 * @description: 测试Buffer和Disk写IO的访问效果
 * @date 2021年07月10日 15:58
 */
public class CheckBufferAndDisk {

    /**
     * 在HDD中对比直接写入disk和使用buffer的性能差异
     * @throws IOException
     */
    @Test
    public void hddTest() throws IOException {
        Long bufferBioTime = 0L;
        Long bufferNioTime = 0L;
        Long diskBioTime = 0L;
        Long diskNioTime = 0L;
        System.out.println("*************在HDD中进行IO测试**************");
        for (int i=0; i<100; i++){
            int a = new Random().nextInt(10);
            BufferIOUnit.outputStream("F:\\temp\\test.txt",(a+"a").getBytes());
        }
        Long start = System.currentTimeMillis();
        for (int i=0; i<1000; i++){
            int a = new Random().nextInt(10);
            BufferIOUnit.outputStream("F:\\temp\\test.txt",(a+"QQ").getBytes());
        }
        Long end = System.currentTimeMillis();
        System.out.print("BufferIOUnit.outputStream：");
        System.out.println(bufferBioTime=end-start);
        for (int i=0; i<100; i++){
            int a = new Random().nextInt(10);
            BufferIOUnit.outputFileChannel("F:\\temp\\test.txt",(a+"b").getBytes());
        }
        start = System.currentTimeMillis();
        for (int i=0; i<1000; i++){
            int a = new Random().nextInt(10);
            BufferIOUnit.outputFileChannel("F:\\temp\\test.txt",(a+"WW").getBytes());
        }
        end = System.currentTimeMillis();
        System.out.print("BufferIOUnit.outputFileChannel：");
        System.out.println(bufferNioTime=end-start);

        for (int i=0; i<100; i++){
            int a = new Random().nextInt(10);
            DirectDiskIOUnit.outputStream("F:\\temp\\test.txt",(a+"c").getBytes());
        }
        start = System.currentTimeMillis();
        for (int i=0; i<1000; i++){
            int a = new Random().nextInt(10);
            DirectDiskIOUnit.outputStream("F:\\temp\\test.txt",(a+"EE").getBytes());
        }
        end = System.currentTimeMillis();
        System.out.print("DirectDiskIOUnit.outputStream：");
        System.out.println(diskBioTime=end-start);


        for (int i=0; i<100; i++){
            int a = new Random().nextInt(10);
            DirectDiskIOUnit.outputFileChannel("F:\\temp\\test.txt",(a+"d").getBytes());
        }
        start = System.currentTimeMillis();
        for (int i=0; i<1000; i++){
            int a = new Random().nextInt(10);
            DirectDiskIOUnit.outputFileChannel("F:\\temp\\test.txt",(a+"RR").getBytes());
        }
        end = System.currentTimeMillis();
        System.out.print("DirectDiskIOUnit.outputFileChannel：");
        System.out.println(diskNioTime=end-start);
        System.out.println("Test Comparison");
        System.out.print("DirectDiskIOUnit.outputStream/BufferIOUnit.outputStream：");
        System.out.println(((diskBioTime*1.0)/bufferBioTime)+"倍");
        System.out.print("DirectDiskIOUnit.outputFileChannel/BufferIOUnit.outputFileChannel：");
        System.out.println(((diskNioTime*1.0)/bufferNioTime)+"倍");
        System.out.print("BufferIOUnit.outputFileChannel/BufferIOUnit.outputStream：");
        System.out.println(((bufferNioTime*1.0)/bufferBioTime)+"倍");
        System.out.print("DirectDiskIOUnit.outputFileChannel/DirectDiskIOUnit.outputStream：");
        System.out.println(((diskNioTime*1.0)/diskBioTime)+"倍");
    }

    /**
     * 在SSD中对比直接写入disk和使用buffer的性能差异
     * @throws IOException
     */
    @Test
    public void ssdTest() throws IOException {
        Long bufferBioTime = 0L;
        Long bufferNioTime = 0L;
        Long diskBioTime = 0L;
        Long diskNioTime = 0L;
        System.out.println("*************在SDD中进行IO测试**************");
        for (int i=0; i<100; i++){
            int a = new Random().nextInt(10);
            BufferIOUnit.outputStream("D:\\temp\\test.txt",(a+"a").getBytes());
        }
        Long start = System.currentTimeMillis();
        for (int i=0; i<1000; i++){
            int a = new Random().nextInt(10);
            BufferIOUnit.outputStream("D:\\temp\\test.txt",(a+"QQ").getBytes());
        }
        Long end = System.currentTimeMillis();
        System.out.print("BufferIOUnit.outputStream：");
        System.out.println(bufferBioTime=end-start);
        for (int i=0; i<100; i++){
            int a = new Random().nextInt(10);
            BufferIOUnit.outputFileChannel("D:\\temp\\test.txt",(a+"b").getBytes());
        }
        start = System.currentTimeMillis();
        for (int i=0; i<1000; i++){
            int a = new Random().nextInt(10);
            BufferIOUnit.outputFileChannel("D:\\temp\\test.txt",(a+"WW").getBytes());
        }
        end = System.currentTimeMillis();
        System.out.print("BufferIOUnit.outputFileChannel：");
        System.out.println(bufferNioTime=end-start);

        for (int i=0; i<100; i++){
            int a = new Random().nextInt(10);
            DirectDiskIOUnit.outputStream("D:\\temp\\test.txt",(a+"c").getBytes());
        }
        start = System.currentTimeMillis();
        for (int i=0; i<1000; i++){
            int a = new Random().nextInt(10);
            DirectDiskIOUnit.outputStream("D:\\temp\\test.txt",(a+"EE").getBytes());
        }
        end = System.currentTimeMillis();
        System.out.print("DirectDiskIOUnit.outputStream：");
        System.out.println(diskBioTime=end-start);


        for (int i=0; i<100; i++){
            int a = new Random().nextInt(10);
            DirectDiskIOUnit.outputFileChannel("D:\\temp\\test.txt",(a+"d").getBytes());
        }
        start = System.currentTimeMillis();
        for (int i=0; i<1000; i++){
            int a = new Random().nextInt(10);
            DirectDiskIOUnit.outputFileChannel("D:\\temp\\test.txt",(a+"RR").getBytes());
        }
        end = System.currentTimeMillis();
        System.out.print("DirectDiskIOUnit.outputFileChannel：");
        System.out.println(diskNioTime=end-start);
        System.out.println("Test Comparison");
        System.out.print("DirectDiskIOUnit.outputStream/BufferIOUnit.outputStream：");
        System.out.println(((diskBioTime*1.0)/bufferBioTime)+"倍");
        System.out.print("DirectDiskIOUnit.outputFileChannel/BufferIOUnit.outputFileChannel：");
        System.out.println(((diskNioTime*1.0)/bufferNioTime)+"倍");
        System.out.print("BufferIOUnit.outputFileChannel/BufferIOUnit.outputStream：");
        System.out.println(((bufferNioTime*1.0)/bufferBioTime)+"倍");
        System.out.print("DirectDiskIOUnit.outputFileChannel/DirectDiskIOUnit.outputStream：");
        System.out.println(((diskNioTime*1.0)/diskBioTime)+"倍");
    }

}
