package org.ex.boys.concurrency.shishan.jvm.week11;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * metaspace outofmemory
 *
 * @author shichao
 * @since 2021/3/22 23:50
 */
public class MetaSpaceOOMDemo {
    /**
     * -XX:MetaspaceSize=10M -XX:MaxMetaspaceSize=10M
     *
     * -XX:MetaspaceSize=10M -XX:MaxMetaspaceSize=10M -XX:+UseParNewGC
     * -XX:+UseConcMarkSweepGC   -XX:+PrintGCDetails  -Xloggc:gc.log
     * -XX:+HeapDumpOnOutOfMemoryError  -XX:HeapDumpPath=./
     */

    static class Car {
        public void run() {
            System.out.println("汽车启动，开始行驶。。。");
        }
    }

    public static void main(String[] args) {
        int count = 0;
        while (true) {
            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(Car.class);
            enhancer.setUseCache(false);
            enhancer.setCallback(new MethodInterceptor() {
                @Override
                public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                    if ("run".equals(method.getName())) {
                        System.out.println("启动器车前，自动安全检查。。。");
                    }
                    return methodProxy.invokeSuper(o, objects);
                }
            });
            Car subCar = (Car) enhancer.create();
            subCar.run();
            System.out.println("Success create count:" + (count++));
        }
    }
}
