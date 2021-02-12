package org.ex.boys.concurrency.ch1;

public class InheritableThreadLocalDemo {
    /**
     * 该demo验证了：
     * 1.子线程可以访问到子线程的threadLocal变量；
     * 2.子线程对inheritedThreadLocal变量修改后，主线程不受影响；
     */
    private static ThreadLocal<String> mainThreadLocal = new InheritableThreadLocal<String>();
    private static ThreadLocal<String> subLocal = new InheritableThreadLocal<String>();

    public static void main(String[] args) throws InterruptedException {
        mainThreadLocal.set("Main hello ");
        // 输出的值和设置的一致
        System.out.println("Begin main get:" + mainThreadLocal.get());
        //
        Thread thread = new Thread(new Runnable() {

            public void run() {
                // 和主线程的值一致
                System.out.println("Sub get main first:" + mainThreadLocal.get());
                mainThreadLocal.set("Sub hello");
                // 输出为，当前线程设置的值
                System.out.println("Sub get main after set:" + mainThreadLocal.get());
                subLocal.set("Sub hello 2");
                // 输出为，当前线程设置的值
                System.out.println("Sub get sub:" + subLocal.get());
            }
        });

        thread.start();
        thread.join();
        // 输出为 主线程设置的值，子线程设置后对此不受影响；
        System.out.println("End main get:" + mainThreadLocal.get());
        // 输出为空
        System.out.println("Main get sub:" + subLocal.get());
    }

}
