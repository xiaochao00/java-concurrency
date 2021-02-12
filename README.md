# Java并发编程之美 读书笔记

## 1.2实现线程的方式
	继承Thread的类，可以在run方法内直接通过this使用当前线程，不过不可以多继承；
	实现Runable接口，可以继承多个接口；以上这连个都没有返回值；
	CallAble和FutureTask,可以有返回值；

## 1.3 线程通知和等待
	wait，notify，notifyAll 方法需要事先获取该对象的监视器锁；
	notifyAll方法可以唤醒调用时刻前wait的所有线程；
	
## 1.7 线程中断
	interrupt 中断线程，B线程可以调用A线程的该方法，设置A的终端标志为true；
	isInterrupt 判断当前线程是否被中断；
	interrupted 检测当前线程的是否被中断，
		区别1：如果已经中断，会清空标志为false;
		区别2：另外，该方法获取的是当前调用线程的中断标志，即B线程调用A线程的中断方法，实际中断的线程是B；
		区别3：该方法是Thread类的静态方法；