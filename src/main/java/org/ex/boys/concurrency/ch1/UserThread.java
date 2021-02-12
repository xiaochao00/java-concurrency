package org.ex.boys.concurrency.ch1;

public class UserThread {
    public static void main(String[] args) {
        Thread userThread = new Thread(new Runnable() {

            public void run() {
                for (;;) {

                }

            }
        });
        userThread.setDaemon(true);
        userThread.start(); // 比较设置前后的差别
        System.out.println("Main is over.");
    }

}
