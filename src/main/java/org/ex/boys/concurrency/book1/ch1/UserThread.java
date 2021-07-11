package org.ex.boys.concurrency.book1.ch1;

public class UserThread {
    public static void main(String[] args) {
        Thread userThread = new Thread(new Runnable() {

            public void run() {
                for (; ; ) {
                    System.out.println("I am sub thread.");
                }

            }
        });
        // 比较设置前后的差别
        userThread.setDaemon(true);
        userThread.start();
        System.out.println("Main is over.");
    }

}
