package com.geordie.nio;

import com.geordie.Util;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class NioClient {

    private static String msg = "Hello I'm NioClient";

    public static void main(String[] args) throws IOException, InterruptedException {

        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        boolean connect = socketChannel.connect(new InetSocketAddress("localhost", 8080));

        Selector selector = Selector.open();
        socketChannel.register(selector, SelectionKey.OP_CONNECT);

        AtomicBoolean shutdown = new AtomicBoolean(false);

        Thread selectorThread = getSelectorThread(selector, shutdown);
        selectorThread.setDaemon(true);
        selectorThread.start();

        Util.waitFinish(1000, () -> !socketChannel.isOpen());

        shutdown.set(true);
        selector.wakeup();
        selectorThread.join();
        selector.close();

        System.out.println("complete");
    }

    private static Thread getSelectorThread(Selector selector, AtomicBoolean shutdown) {
        return new Thread(() -> {
            try {
                while (!shutdown.get()) {
                    selector.select();
                    Set<SelectionKey> selectedKeys = selector.selectedKeys();
                    Iterator<SelectionKey> iterator = selectedKeys.iterator();

                    while (iterator.hasNext()) {
                        SelectionKey key = iterator.next();
                        iterator.remove();

                        if (key.isConnectable()) {
                            SocketChannel channel = (SocketChannel) key.channel();
                            channel.finishConnect();
                            System.out.println("local address: " + channel.getLocalAddress());
                            System.out.println("address :" + channel.getRemoteAddress());
                            key.interestOps(SelectionKey.OP_WRITE);
                        }

                        if (key.isWritable()) {
                            SocketChannel channel = (SocketChannel) key.channel();
                            ByteBuffer bf = ByteBuffer.wrap(msg.getBytes());
                            int writeBytes = channel.write(bf);
                            System.out.println(Util.msg("Client send : ", writeBytes, bf));
                            key.interestOps(SelectionKey.OP_READ);
                        }

                        if (key.isReadable()) {
                            SocketChannel channel = (SocketChannel) key.channel();
                            ByteBuffer bf = ByteBuffer.allocate(1024 * 8);
                            int readBytes = channel.read(bf);
                            System.out.println(Util.msg("Client read: ", readBytes, bf));
                            channel.close();
                        }
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
