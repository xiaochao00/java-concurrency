package org.ex.boys.concurrency.shishan.jvm.week10;

import java.util.ArrayList;
import java.util.List;

/**
 * 由于code导致内存泄漏的GC问题
 *
 * @author shichao
 * @since 2021/3/21 17:15
 */
public class CodeGCFailDemo {
    public static void main(String[] args) throws InterruptedException {
        List<Data> datas = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            datas.add(new Data());
        }
        Thread.sleep(60 * 1000);
        "123 456".split("");
    }

    static class Data {

    }
}
