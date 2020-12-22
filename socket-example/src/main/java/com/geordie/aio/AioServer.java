package com.geordie.aio;

import com.geordie.Util;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class AioServer {

    private static String msg = "AioServer send you a resp -";

    public static void main(String[] args) throws IOException {

        AsynchronousServerSocketChannel serverSocketChannel = AsynchronousServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress("localhost", 8080));

        Future<AsynchronousSocketChannel> accept;

        while (true) {
            try {
                AsynchronousSocketChannel socketChannel = serverSocketChannel.accept().get();
                System.out.println("connect: " + socketChannel.getRemoteAddress());

                ByteBuffer bf = ByteBuffer.allocate(1024 * 8);
                socketChannel.read(bf, bf, new CompletionHandler<Integer, ByteBuffer>() {
                    @Override
                    public void completed(Integer result, ByteBuffer attachment) {
                        System.out.println(Util.msg("Read :", result, attachment));

                        attachment.flip();
                        byte[] respPrefix = msg.getBytes(StandardCharsets.UTF_8);
                        ByteBuffer resp = ByteBuffer.allocate(respPrefix.length + attachment.limit())
                                .put(respPrefix).put(attachment);

                        resp.flip();

                        socketChannel.write(resp, attachment, new CompletionHandler<Integer, ByteBuffer>() {
                            @Override
                            public void completed(Integer result, ByteBuffer attachment) {
                                System.out.println(Util.msg("Write :", result, attachment));
                                try {
                                    socketChannel.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void failed(Throwable exc, ByteBuffer attachment) {

                            }
                        });
                    }

                    @Override
                    public void failed(Throwable exc, ByteBuffer attachment) {
                        throw new RuntimeException(exc);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}