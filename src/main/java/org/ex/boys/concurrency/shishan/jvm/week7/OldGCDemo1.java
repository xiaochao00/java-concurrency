package org.ex.boys.concurrency.shishan.jvm.week7;

/**
 * OldGC demo1
 *
 * @author shichao
 * @since 2021/3/18 22:21
 */
public class OldGCDemo1 {

    public static void main(String[] args) {
        byte[] array1 = new byte[2 * 1024 * 1024];
        array1 = new byte[2 * 1024 * 1024];
        array1 = new byte[2 * 1024 * 1024];
        array1 = null;
        byte[] array2 = new byte[128 * 1024];
        // 第一次GC
        byte[] array3 = new byte[2 * 1024 * 1024];
        array3 = new byte[2 * 1024 * 1024];
        array3 = new byte[2 * 1024 * 1024];
        array3 = new byte[128 * 1024];
        array3 = null;
        // 第2次GC
        byte[] array4 = new byte[2 * 1024 * 1024];
    }
}
