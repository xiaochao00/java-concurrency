package org.ex.boys.concurrency.ch1;

public class StringTest {
    public static void main(String[] args) {
        String a = "abc1234";
        String b = new String("abc1234");
        String c = new StringBuilder("abc1234").toString();
        String d = "abc1234";
        System.out.println(a == b);
        System.out.println(c == b);
        System.out.println(a == d);
        System.out.println(a == a.intern());
        System.out.println(b.intern() == a.intern());
        System.out.println(c.intern() == a.intern());
    }
}
