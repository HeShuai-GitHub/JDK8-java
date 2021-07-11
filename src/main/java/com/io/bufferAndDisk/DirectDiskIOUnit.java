package com.io.bufferAndDisk;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * @author heshuai
 * @title: DirectDiskIOUnit
 * @description: 直接写入disk
 * @date 2021年07月10日 12:00
 */
public class DirectDiskIOUnit {

    public static void outputStream(String fileName, byte[] data) throws IOException {
        FileOutputStream fos = new FileOutputStream(fileName,true);
        fos.write(data);
        fos.flush();
        fos.getFD().sync();
    }

    public static void outputFileChannel(String fileName, byte[] data) throws IOException {
        FileChannel fileChannel = FileChannel.open(Paths.get(fileName), StandardOpenOption.APPEND);
        ByteBuffer buffer = ByteBuffer.allocate(3);
        buffer.put(data);
        buffer.flip();
        fileChannel.write(buffer);
        fileChannel.force(true);
        buffer.clear();
    }
}
