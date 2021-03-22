package org.ex.boys.concurrency.shishan.jvm.week11;

import java.util.ArrayList;
import java.util.List;

/**
 * Heap out of memory
 *
 * @author shichao
 * @since 2021/3/23 0:09
 */
public class HeapOutOfMemory {
    /**
     * -Xms10m -Xmx10m
     */
    public static void main(String[] args) {
        long count = 0;
        List<Object> list = new ArrayList<>();
        while (true) {
            list.add(new Object());
            System.out.println("当前添加第：" + (count++) + "个对象");
        }
    }
}
