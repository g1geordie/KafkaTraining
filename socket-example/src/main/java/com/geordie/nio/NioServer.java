package com.geordie.nio;

import com.geordie.Util;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

public class NioServer {

    public static void main(String[] args) throws IOException {

        String msg = "NioServer send you a resp -";
        ServerSocketChannel channel = ServerSocketChannel.open();
        Selector selector = Selector.open();
        channel.configureBlocking(false);

        channel.socket().bind(new InetSocketAddress("localhost", 8080));
        channel.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            int readyChannels = selector.select();
            if (readyChannels == 0) continue;
            Set<SelectionKey> selectedKeys = selector.selectedKeys();

            Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

            while (keyIterator.hasNext()) {

                SelectionKey key = keyIterator.next();
                try {
                    if (key.isAcceptable()) {
                        SocketChannel socketChannel = ((ServerSocketChannel) key.channel()).accept();
                        socketChannel.configureBlocking(false);
                        socketChannel.register(key.selector(), SelectionKey.OP_READ, ByteBuffer.allocate(1024 * 8));
                        System.out.println("connect: " + socketChannel.getRemoteAddress());
                    }

                    if (key.isReadable()) {
                        SocketChannel clientChannel = (SocketChannel) key.channel();
                        ByteBuffer buf = (ByteBuffer) key.attachment();
                        int bytesRead = clientChannel.read(buf);
                        if (bytesRead == -1) {
                            clientChannel.close();
                        } else if (bytesRead > 0) {
                            key.interestOps(SelectionKey.OP_WRITE);
                            System.out.println(Util.msg("Read :", bytesRead, buf));
                        }
                    }

                    if (key.isValid() && key.isWritable()) {
                        ByteBuffer buf = (ByteBuffer) key.attachment();
                        SocketChannel clientChannel = (SocketChannel) key.channel();
                        buf.flip();

                        byte[] respPrefix = msg.getBytes(StandardCharsets.UTF_8);
                        ByteBuffer resp = ByteBuffer.allocate(respPrefix.length + buf.limit())
                                .put(respPrefix).put(buf);

                        resp.flip();

                        int bytesWrite = clientChannel.write(resp);
                        key.interestOps(SelectionKey.OP_READ);
                        System.out.println(Util.msg("Write: ", bytesWrite, resp));

                        clientChannel.close();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    keyIterator.remove();
                }
            }
        }
    }
}
