package com.geordie.aio;

import com.geordie.Util;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class AioClient {

    private static String msg = "Hello I'm AioClient";

    public static void main(String[] args) {

        try (AsynchronousSocketChannel socketChannel = AsynchronousSocketChannel.open()) {

            Future<Void> connect = socketChannel.connect(new InetSocketAddress("localhost", 8080));
            Util.waitFinish(10, connect::isDone);
            System.out.println("local address: " + socketChannel.getLocalAddress());
            System.out.println("address :" + socketChannel.getRemoteAddress());

            ByteBuffer buffer = ByteBuffer.allocate(1024 * 8);
            buffer.put(msg.getBytes(StandardCharsets.UTF_8));
            buffer.flip();

            Future<Integer> write = socketChannel.write(buffer);
            Util.waitFinish(10, write::isDone);
            System.out.println(Util.msg("Client send : ", write.get(), buffer));

            buffer.clear();

            Future<Integer> read = socketChannel.read(buffer);
            Util.waitFinish(10, read::isDone);
            System.out.println(Util.msg("Client read : ", read.get(), buffer));
            System.out.println("complete");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
