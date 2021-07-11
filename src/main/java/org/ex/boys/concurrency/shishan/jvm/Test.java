package org.ex.boys.concurrency.shishan.jvm;

/**
 * test
 *
 * @author shichao
 * @since 1.0.0
 * 2021/7/7 1:11
 */
public class Test {
    public static void main(String[] args) {
        Integer m = 10;
        Integer n = 10;
        System.out.println(m == n);
        //
        m = 150;
        n = 150;
        System.out.println(m == n);
        //
        Integer a = 10;
        Integer b = 10;
        Integer c = 20;
        System.out.println(c == a + b);
        //
        a = 150;
        b = 150;
        c = 300;
        System.out.println(c == a + b);
    }
}
