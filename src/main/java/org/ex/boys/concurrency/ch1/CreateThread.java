package org.ex.boys.concurrency.ch1;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class CreateThread {
    public static class CallerTask implements Callable<String> {
        public String call() throws Exception {
            return "hello";
        }
    }

    public static void main(String[] args) throws InterruptedException {
        // Runuale
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName());
            }
        };
        Thread runableThread = new Thread(runnable);
        runableThread.setName("runable-thread");
        runableThread.start();
        runableThread.join();
        // Thread
        Thread threadDemo = new Thread() {
            @Override
            public void run() {
                System.out.println(this.getName());
            }
        };
        threadDemo.setName("thread-demo");
        threadDemo.start();
        threadDemo.join();

        FutureTask<String> futureTaskask = new FutureTask<String>(new CallerTask());
        new Thread(futureTaskask).start();
        try {
            String result = futureTaskask.get();
            System.out.println(result);
        } catch (InterruptedException | ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
