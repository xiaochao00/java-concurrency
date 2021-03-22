package org.ex.boys.concurrency.shishan.jvm.week7;

/**
 * YoungGC demo1
 *
 * @author shichao
 * @since 2021/3/18 21:36
 */
public class YoungGCDemo1 {
    /**
     * -XX:NewSize=5242880 -XX:MaxNewSize=5242880
     * -XX:InitialHeapSize=10485760 -XX:MaxHeapSize=10485760
     * -XX:SurvivorRatio=8 -XX:PretenureSizeThreshold=10485760
     * -XX:+UseParNewGC -XX:+UseConcMarkSweepGC
     * -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -Xloggc:gc.log
     *
     * @param args
     */
    public static void main(String[] args) {
        byte[] array1 = new byte[1024 * 1024];
        array1 = new byte[1024 * 1024];
        array1 = new byte[1024 * 1024];
        array1 = null;
        byte[] array2 = new byte[2 * 1024 * 1024];
    }
}
