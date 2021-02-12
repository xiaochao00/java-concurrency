package org.ex.boys.concurrency.ch1;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class CreateThread {
    public static class CallerTask implements Callable<String>{

        public String call() throws Exception {
            return "hello"; 
        }
        
    }
    public static void main(String[] args) {
        FutureTask<String> futureTaskask = new FutureTask<String>(new CallerTask());
        new Thread(futureTaskask).start();
        try {
            String result = futureTaskask.get();
            System.out.println(result);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
