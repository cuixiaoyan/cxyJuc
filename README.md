# 什么是JUC

业务：普通的线程代码 Thread
Runnable
没有返回值、效率相比入 Callable 相对较低!

# 线程和进程

进程：一个程序，QQ.exe Music.exe 程序的集合；
一个进程往往可以包含多个线程，至少包含一个！
Java默认有几个线程？ 2 个 mian、GC
线程：开了一个进程 Typora，写字，自动保存（线程负责的）
对于Java而言：Thread、Runnable、Callable
Java 真的可以开启线程吗？ ==开不了==

```java
public synchronized void start() {
        /**
         * This method is not invoked for the main method thread or "system"
         * group threads created/set up by the VM. Any new functionality added
         * to this method in the future may have to also be added to the VM.
         *
         * A zero status value corresponds to state "NEW".
         */
        if (threadStatus != 0)
            throw new IllegalThreadStateException();

        /* Notify the group that this thread is about to be started
         * so that it can be added to the group's list of threads
         * and the group's unstarted count can be decremented. */
        group.add(this);

        boolean started = false;
        try {
            start0();
            started = true;
        } finally {
            try {
                if (!started) {
                    group.threadStartFailed(this);
                }
            } catch (Throwable ignore) {
                /* do nothing. If start0 threw a Throwable then
                  it will be passed up the call stack */
            }
        }
    }
		// 调用c++创建线程。
    private native void start0();
```

>并发、并行

并发编程：并发、并行
并发（多线程操作同一个资源）

- CPU 一核 ，模拟出来多条线程，天下武功，唯快不破，快速交替

并行（多个人一起行走）

- CPU 多核 ，多个线程可以同时执行； 线程池

```java
				// 获取cpu的核数
        // CPU 密集型，IO密集型
        System.out.println(Runtime.getRuntime().availableProcessors());
```



>线程有几个状态

```java
public enum State {
      // 新生
      NEW,
      // 运行
      RUNNABLE,
      // 阻塞
      BLOCKED,
      // 等待，死死地等
      WAITING,
      // 超时等待
      TIMED_WAITING,
      // 终止
      TERMINATED;
}
```

>wait/sleep 区别

1、来自不同的类
wait => Object
sleep => Thread
2、关于锁的释放
wait 会释放锁，sleep 睡觉了，抱着锁睡觉，不会释放！
3、使用的范围是不同的
wait必须在同步代码块中。

sleep可以在任何地方。

4、是否需要捕获异常
wait 不需要捕获异常
sleep 必须要捕获异常

# Lock锁（重点）

>传统 Synchronized

![image-20200811161639860](https://gitee.com/cuixiaoyan/uPic/raw/master/uPic/image-20200811161639860.png)

结果是混乱的，数据错误。

增加synchronized问题解决，但是效率不高。

![image-20200811161756488](https://gitee.com/cuixiaoyan/uPic/raw/master/uPic/image-20200811161756488.png)

```java
package com.cxy.demo01;

import sun.security.krb5.internal.Ticket;

/**
 * @program: cxyJuc
 * @description: 基本卖票例子。
 * @author: cuixy
 * @create: 2020-08-11 15:53
 **/
public class SaleTicketDemo01 {

    public static void main(String[] args) {
        // 并发：多线程操作同一个资源类，把资源类丢进线程内。
        Ticket ticket = new Ticket();
        //@FunctionalInterface 函数式接口，jdk1.8 lambda表达式(参数)-> {代码}
        new Thread(() -> {
            for (int i = 0; i < 40; i++) {
                ticket.sale();
            }
        }, "A").start();

        new Thread(() -> {
            for (int i = 0; i < 40; i++) {
                ticket.sale();
            }
        }, "B").start();

        new Thread(() -> {
            for (int i = 0; i < 40; i++) {
                ticket.sale();
            }
        }, "C").start();


    }

    //资源类 OOP
    static class Ticket {
        //属性.方法
        private int number = 30;

        //卖票的方式
        //synchronized 本质：队列，锁。
        public synchronized void sale() {
            if (number > 0) {
                System.out.println(Thread.currentThread().getName() + "卖出了" + (number--) + "票，剩余：" + number);
            }
        }
    }


}
```

> Lock接口

<img src="https://gitee.com/cuixiaoyan/uPic/raw/master/uPic/image-20200811162542573.png" alt="image-20200811162542573" style="zoom:50%;" />

## 实现类 ReentrantLock

<img src="https://gitee.com/cuixiaoyan/uPic/raw/master/uPic/image-20200811162906322.png" alt="image-20200811162906322" style="zoom:50%;" />

公平锁：十分公平：可以先来后到
非公平锁：十分不公平：可以插队 （默认）

<img src="https://gitee.com/cuixiaoyan/uPic/raw/master/uPic/image-20200811165351763.png" alt="image-20200811165351763" style="zoom:50%;" />

```java
package com.cxy.demo01;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @program: cxyJuc
 * @description: Lock锁。
 * @author: cuixy
 * @create: 2020-08-11 16:33
 **/
public class SaleTicketDemo02 {
    public static void main(String[] args) {
        Ticket2 ticket2 = new Ticket2();
        new Thread(() -> {
            for (int i = 0; i < 40; i++) {
                ticket2.sale();
            }
        }, "A").start();

        new Thread(() -> {
            for (int i = 0; i < 40; i++) {
                ticket2.sale();
            }
        }, "B").start();

        new Thread(() -> {
            for (int i = 0; i < 40; i++) {
                ticket2.sale();
            }
        }, "C").start();

    }
}

//lock三部曲
//1、new ReentrantLock();
//2、lock.lock(); 加锁
//3、finally => lock.unlock(); 解锁
class Ticket2 {
    //属性.方法
    private int number = 30;

    Lock lock = new ReentrantLock();

    public void sale() {
        //加锁
        lock.lock();
        try {
            if (number > 0) {
                System.out.println(Thread.currentThread().getName() + "卖出了" + (number--) + "票，剩余：" + number);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //解锁
            lock.unlock();
        }
    }
}
```

>Synchronized 和 Lock 区别

1、Synchronized 内置的Java关键字， Lock 是一个Java类
2、Synchronized 无法判断获取锁的状态，Lock 可以判断是否获取到了锁
3、Synchronized 会自动释放锁，lock 必须要手动释放锁！如果不释放锁，死锁
4、Synchronized 线程 1（获得锁，阻塞）、线程2（等待，傻傻的等）；Lock锁就不一定会等待下
去；
5、Synchronized 可重入锁，不可以中断的，非公平；Lock ，可重入锁，可以 判断锁，非公平（可以
自己设置）；
6、Synchronized 适合锁少量的代码同步问题，Lock 适合锁大量的同步代码！

```
锁是什么，如何判断锁的是谁！
```

# 生产者和消费者问题

>生产者和消费者问题 Synchronized 版

