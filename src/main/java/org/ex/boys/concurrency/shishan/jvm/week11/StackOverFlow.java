package org.ex.boys.concurrency.shishan.jvm.week11;

/**
 * stackover flow
 *
 * @author shichao
 * @since 2021/3/23 0:05
 */
public class StackOverFlow {

    public static int count = 0;

    /**
     * -XX:ThreadStackSize=1m
     */
    public static void main(String[] args) {
        work();
    }

    private static void work() {
        System.out.println("目前是第" + (count++) + "次调用。");
        work();
    }
}
