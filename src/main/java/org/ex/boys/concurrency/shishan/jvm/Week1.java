package org.ex.boys.concurrency.shishan.jvm;

public class Week1 {
    public static void main(String[] args) {
        System.out.println("Begin main");
        methodA();
        System.out.println("End main");
    }

    private static void methodA() {
        String str = new String("methodA");
        String str2 = new String("args");
        System.out.println(str + str2);
    }
}
