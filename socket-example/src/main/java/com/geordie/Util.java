package com.geordie;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.function.BooleanSupplier;

public class Util {

    public static String fromByteBuffer(ByteBuffer bf) {
        return StandardCharsets.UTF_8.decode(bf).toString();
    }

    public static ByteBuffer toByteBuffer(String msg) {
        return ByteBuffer.wrap(msg.getBytes(StandardCharsets.UTF_8));
    }

    public static String msg(String msg, int byteLength, ByteBuffer bf) {
        bf = bf.duplicate();
        bf.flip();
        return msg + ",length: " + byteLength + ",data: " + fromByteBuffer(bf);
    }

    public static void waitFinish(long checkAfter, BooleanSupplier check) throws InterruptedException {
        if (!check.getAsBoolean())
            Thread.sleep(checkAfter);
    }
}
