package org.ex.boys.concurrency.shishan.jvm.week7;

/**
 * 第7周 jvm GC日志
 *
 * @author shichao
 * @since 2021/3/18 0:44
 */
public class Week7 {
    // -XX:NewSize=5m -XX:MaxNewSize=5m -XX:InitialHeapSize=10m
    // -XX:MaxHeapSize=10m -XX:SurvivorRatio=8
    // -XX:PretenureSizeThreshold=10485760
    // -XX:+UseParNewGC -XX:+UseConcMarkSweepGC
    // -XX:+PrintGCDetails -XX:+PrintGCTimeStamps

//    private static final byte[] data = new byte[1024 * 1024];
    private Integer a;
    private Integer a1;
    private Integer a2;
    private Integer a3;
    private Integer a4;

    public static void main(String[] args) {
        Week7 w = new Week7();
        w.a=1000;
        w.a2=1001;
        w.a3=1002;
        w.a4=1003;
        w.a1=1004;
        byte[] d2 = new byte[1024 * 1024];
        d2 = new byte[1024 * 1024];
        d2 = new byte[1024 * 1024];
        d2 = null;
    }
}
