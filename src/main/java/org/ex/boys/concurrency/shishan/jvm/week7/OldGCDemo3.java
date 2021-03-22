package org.ex.boys.concurrency.shishan.jvm.week7;

/**
 * Old GC demo2
 *
 * @author shichao
 * @since 2021/3/19 1:31
 */
public class OldGCDemo3 {
    public static void main(String[] args) throws InterruptedException {
        byte[] array1 = new byte[4 * 1024 * 1024];
        array1 = null;

        byte[] array2 = new byte[2 * 1024 * 1024];
        byte[] array3 = new byte[2 * 1024 * 1024];
        byte[] array4 = new byte[2 * 1024 * 1024];
        byte[] array5 = new byte[128 * 1024];

        byte[] array6 = new byte[2 * 1204 * 1024];
        while(true){
            System.out.println("....");
            Thread.sleep(3);
        }
    }
}
