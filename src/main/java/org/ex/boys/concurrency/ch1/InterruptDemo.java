package org.ex.boys.concurrency.ch1;

public class InterruptDemo {
    public static void main(String[] args) throws InterruptedException {
        
        Thread thread1 = new Thread(new Runnable() {
            public void run() {
                while (!Thread.currentThread().isInterrupted()) {
                    System.out.println(Thread.currentThread().getName());
                }
            }
        });
        thread1.start();
        Thread.sleep(1000);
        System.out.println("Main thread interrupt thread.");
        thread1.interrupt();
        thread1.join();
        
        System.out.println("Main thread is over.");
    }
}
