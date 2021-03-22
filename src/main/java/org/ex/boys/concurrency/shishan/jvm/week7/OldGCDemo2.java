package org.ex.boys.concurrency.shishan.jvm.week7;

/**
 * Old GC demo2
 *
 * @author shichao
 * @since 2021/3/19 1:31
 */
public class OldGCDemo2 {
    public static void main(String[] args) {
        byte[] array1 = new byte[2 * 1024 * 1024];
        array1 = new byte[2 * 1024 * 1024];
        array1 = new byte[2 * 1024 * 1024];

        byte[] array2 = new byte[128 * 1024];
        array2 = null;
        // 发生GC
        byte[] array3 = new byte[2 * 1024 * 1024];
    }
}
