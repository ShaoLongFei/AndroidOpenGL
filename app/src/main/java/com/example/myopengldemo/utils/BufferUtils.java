package com.example.myopengldemo.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

/**
 * @创建者 邵龙飞
 * @创建时间 2020/9/5 10:23 PM
 * @描述
 */
public class BufferUtils {

    public static IntBuffer getIntBuffer(int[] arr) {
        IntBuffer intBuffer;
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(arr.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        intBuffer = byteBuffer.asIntBuffer();
        intBuffer.put(arr);
        intBuffer.position(0);
        return intBuffer;
    }

    public static FloatBuffer getFloatBuffer(float[] arr) {
        FloatBuffer floatBuffer;
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(arr.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        floatBuffer = byteBuffer.asFloatBuffer();
        floatBuffer.put(arr);
        floatBuffer.position(0);
        return floatBuffer;
    }

    public static ShortBuffer getShortBuffer(short[] arr) {
        ShortBuffer shortBuffer;
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(arr.length * 2);
        byteBuffer.order(ByteOrder.nativeOrder());
        shortBuffer = byteBuffer.asShortBuffer();
        shortBuffer.put(arr);
        shortBuffer.position(0);
        return shortBuffer;
    }

}
