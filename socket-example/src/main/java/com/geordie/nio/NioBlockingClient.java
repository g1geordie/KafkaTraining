package com.geordie.nio;

import com.geordie.Util;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class NioBlockingClient {

    public static void main(String[] args) throws IOException {

        String msg = "This is a test ";

        try (SocketChannel socketChannel = SocketChannel.open()) {
            socketChannel.connect(new InetSocketAddress("localhost", 8080));

            System.out.println("local address: " + socketChannel.getLocalAddress());
            System.out.println("address :" + socketChannel.getRemoteAddress());

            ByteBuffer bf = ByteBuffer.allocate(1024 * 8);

            bf.put(msg.getBytes());
            bf.flip();
            int writeBytes = socketChannel.write(bf);

            System.out.println(Util.msg("Read: ", writeBytes, bf));
            bf.clear();

            int readBytes = socketChannel.read(bf);

            System.out.println(Util.msg("Write: ", readBytes, bf));
            System.out.println("complete");
        }
    }
}
