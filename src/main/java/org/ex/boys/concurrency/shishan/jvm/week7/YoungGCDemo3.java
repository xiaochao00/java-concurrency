package org.ex.boys.concurrency.shishan.jvm.week7;

/**
 * young 优化
 *
 * @author shichao
 * @since 2021/3/20 1:41
 */
public class YoungGCDemo3 {
    public static void main(String[] args) throws InterruptedException {
        Thread.sleep(10000);
        while (true){
            loadData();
        }
    }

    private static void loadData() throws InterruptedException {
        // 每秒产生 5M的数据
        byte[] data = null;
        for (int i = 0; i < 4; i++) {
            data = new byte[10 * 1024 * 1024];
        }
        data = null;
        byte[]data1 = new byte[10 * 1024 * 1024];
        byte[]data2 = new byte[10 * 1024 * 1024];
        byte[]data3 = new byte[10 * 1024 * 1024];

        data3 = new byte[10 * 1024 * 1024];
        Thread.sleep(1000);
    }
}
