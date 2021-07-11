package com.io.bufferAndDisk;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * @author heshuai
 * @title: BufferIOUnit
 * @description: 使用系统缓存来io操作
 * @date 2021年07月10日 11:12
 */
public class BufferIOUnit {

    public static void outputStream(String fileName, byte[] data) throws IOException {
        FileOutputStream fos = new FileOutputStream(fileName,true);
        fos.write(data);
        fos.flush();
    }

    public static void outputFileChannel(String fileName, byte[] data) throws IOException {
        FileChannel fileChannel = FileChannel.open(Paths.get(fileName), StandardOpenOption.APPEND);
        ByteBuffer buffer = ByteBuffer.allocate(3);
        buffer.put(data);
        buffer.flip();
        fileChannel.write(buffer);
        buffer.clear();
    }
}
