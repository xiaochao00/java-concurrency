package org.ex.boys.concurrency.shishan.jvm.week7;

/**
 * 频繁GC
 *
 * @author shichao
 * @since 2021/3/20 1:18
 */
public class YoungGCDemo2 {
    public static void main(String[] args) throws InterruptedException {
        Thread.sleep(10000);
        while (true) {
            loadData();
        }
    }

    private static void loadData() throws InterruptedException {
        // 每秒产生 5M的数据
        byte[] data = null;
        for (int i = 0; i < 50; i++) {
            data = new byte[100 * 1024];
        }
        data = null;
        Thread.sleep(1000);
    }
}
