

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
        if (threadStatus != 0)
            throw new IllegalThreadStateException();
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
		//可重入锁
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

<img src="https://gitee.com/cuixiaoyan/uPic/raw/master/uPic/image-20200812160048556.png" alt="image-20200812160048556" style="zoom:50%;" />

出现如下问题，顺序混乱。

<img src="https://gitee.com/cuixiaoyan/uPic/raw/master/uPic/image-20200812155552631.png" alt="image-20200812155552631" style="zoom:50%;" />



<img src="https://gitee.com/cuixiaoyan/uPic/raw/master/uPic/image-20200812160311571.png" alt="image-20200812160311571" style="zoom:50%;" />

```java
package com.cxy.pc;


import javax.xml.crypto.Data;

/**
 * @program: cxyJuc
 * @description: 线程之间的通讯问题，生产者和消费者模式，等待唤醒，通知唤醒。
 * 线程交替执行 A B 操作同一个变量 num = 0
 * A num + 1
 * B num - 1
 * @author: cuixy
 * @create: 2020-08-12 15:34
 **/
public class A {
    public static void main(String[] args) {
        MyData myData = new MyData();

        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    myData.increment();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "A").start();

        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    myData.decrement();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "B").start();

        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    myData.increment();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "C").start();

        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    myData.decrement();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "D").start();

    }

}

class MyData {
    private int number = 0;

    //+1
    public synchronized void increment() throws InterruptedException {
        //if修改为while
        while (number != 0) { //0
            //等待
            this.wait();
        }
        number++;
        System.out.println(Thread.currentThread().getName() + "=>" + number);
        //通知其他线程+1执行完毕。
        this.notifyAll();

    }

    //-1
    public synchronized void decrement() throws InterruptedException {
        //if修改为while
        while (number == 0) {//1
            //等待
            this.wait();
        }
        number--;
        System.out.println(Thread.currentThread().getName() + "=>" + number);
        //通知其他线程 -1完毕。
        this.notifyAll();
    }


}
```

>JUC版的生产者和消费者问题

<img src="https://gitee.com/cuixiaoyan/uPic/raw/master/uPic/image-20200812160526730.png" alt="image-20200812160526730" style="zoom:50%;" />

>Condition 精准的通知和唤醒线程

<img src="https://gitee.com/cuixiaoyan/uPic/raw/master/uPic/image-20200812164352319.png" alt="image-20200812164352319" style="zoom:50%;" />

数据没有问题，但是顺序不整齐。

```java
package com.cxy.pc;


import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @program: cxyJuc
 * @description: juc版的生产者和消费者。
 * @author: cuixy
 * @create: 2020-08-12 16:07
 **/
public class B {
    public static void main(String[] args) {

        MyData2 myData2 = new MyData2();

        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    myData2.increment();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "A").start();

        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    myData2.decrement();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "B").start();


        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    myData2.increment();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "C").start();

        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    myData2.decrement();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "D").start();


    }

}

//判断等待，业务，通知。
class MyData2 {
    private int number = 0;

    Lock lock = new ReentrantLock();
    Condition condition = lock.newCondition();
    //condtion.await(); 等待
    //condition.signalAll(); 唤醒全部

    //+1
    public void increment() throws InterruptedException {
        lock.lock();
        try {
            while (number != 0) {
                condition.await();
            }
            number++;
            System.out.println(Thread.currentThread().getName() + "=>" + number);
            condition.signalAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    //-1
    public void decrement() throws InterruptedException {
        lock.lock();
        try {
            while (number == 0) {
                condition.await();
            }
            number--;
            System.out.println(Thread.currentThread().getName() + "=>" + number);
            condition.signalAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }


}
```

## 有序执行

<img src="https://gitee.com/cuixiaoyan/uPic/raw/master/uPic/image-20200813153404112.png" alt="image-20200813153404112" style="zoom:50%;" />

```java
package com.cxy.pc;


import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @program: cxyJuc
 * @description: juc版的生产者和消费者，有序版。
 * @author: cuixy
 * @create: 2020-08-12 16:07
 **/
public class C {
    public static void main(String[] args) {

        MyData3 myData3 = new MyData3();

        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                myData3.printA();

            }
        }, "A").start();


        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                myData3.printB();
            }
        }, "B").start();

        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                myData3.printC();
            }
        }, "C").start();


    }

}

//判断等待，业务，通知。
class MyData3 {


    Lock lock = new ReentrantLock();
    Condition condition1 = lock.newCondition();
    Condition condition2 = lock.newCondition();
    Condition condition3 = lock.newCondition();
    //1A 2B 3C
    private int number = 1;

    public void printA() {
        lock.lock();
        try {
            while (number != 1) {
                condition1.await();
            }
            System.out.println(Thread.currentThread().getName() + "=>" + number);
            //唤醒指定的B
            number = 2;
            condition2.signal();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void printB() {
        lock.lock();
        try {
            while (number != 2) {
                condition2.await();
            }
            System.out.println(Thread.currentThread().getName() + "=>" + number);
            //唤醒指定的C
            number = 3;
            condition3.signal();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void printC() {
        lock.lock();
        try {
            while (number != 3) {
                condition3.await();
            }
            System.out.println(Thread.currentThread().getName() + "=>" + number);
            //唤醒指定的A
            number = 1;
            condition1.signal();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }


}
```

# 8锁现象

如何判断锁的是谁！永远的知道什么锁，锁到底锁的是谁！
深刻理解我们的锁

```java
package com.cxy.lock8;
/**
 * @program: cxyJuc
 * @description: 8锁问题。
 * @author: cuixy
 * @create: 2020-08-15 10:12
 **/
import java.util.concurrent.TimeUnit;
/**
 * 8锁，就是关于锁的8个问题
 * 1、标准情况下，两个线程先打印 发短信还是 打电话？ 1/发短信 2/打电话
 * 1、sendSms延迟4秒，两个线程先打印 发短信还是 打电话？ 1/发短信 2/打电话
 */
public class Test1 {
    public static void main(String[] args) {
        Phone phone = new Phone();
        new Thread(() -> {
            phone.sendSms();
        }, "A").start();
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        new Thread(() -> {
            phone.call();
        }, "B").start();
    }
}

class Phone {
    // synchronized 锁的对象是方法的调用者！、
    // 两个方法用的是同一个锁，谁先拿到谁执行！
    public synchronized void sendSms() {
        try {
            TimeUnit.SECONDS.sleep(4);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("发短信");

    }
    public synchronized void call() {
        System.out.println("打电话");
    }
}
```

```java
package com.cxy.lock8;

import java.util.concurrent.TimeUnit;

/**
 * @program: cxyJuc
 * @description:
 * @author: cuixy
 * @create: 2020-08-15 10:44
 **/
/**
* 3、 增加了一个普通方法后！先执行发短信还是Hello？ 普通方法
* 4、 两个对象，两个同步方法， 发短信还是 打电话？ // 打电话
*/

public class Test2 {
    public static void main(String[] args) {
        //两个对象，两个调用者，两把锁。
        Phone2 phone1 = new Phone2();
        Phone2 phone2 = new Phone2();

        //锁的存在
        new Thread(() -> {
            phone1.sendSms();
        }, "A").start();

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        new Thread(() -> {
            phone2.hello();
            phone2.call();
        }, "B").start();


    }

}

class Phone2 {

    //synchronized 锁的对象，是方法的调用者。
    public synchronized void sendSms() {
        try {
            TimeUnit.SECONDS.sleep(4);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("发短信");
    }

    public synchronized void call() {
        System.out.println("打电话");
    }

    //这了没有锁，不是同步方法，不受锁的影响。
    public void hello() {
        System.out.println("hello");
    }


}
```

```java
package com.cxy.lock8;

/**
 * @program: cxyJuc
 * @description:
 * @author: cuixy
 * @create: 2020-08-15 11:04
 **/

import java.util.concurrent.TimeUnit;

/**
 * 5、增加两个静态的同步方法，只有一个对象，先打印 发短信？打电话？
 * 6、两个对象！增加两个静态的同步方法， 先打印 发短信？打电话？
 */

public class Test3 {
    public static void main(String[] args) {
        // 两个对象的Class类模板只有一个，static，锁的是Class
        Phone3 phone1 = new Phone3();
        Phone3 phone2 = new Phone3();

        new Thread(() -> {
            phone1.sendSms();
        }, "A").start();

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        new Thread(() -> {
            phone2.call();
        }, "B").start();
    }

}

// Phone3唯一的一个 Class 对象
class Phone3 {
    // synchronized 锁的对象是方法的调用者！
    // static 静态方法
    // 类一加载就有了！锁的是Class
    public static synchronized void sendSms() {
        try {
            TimeUnit.SECONDS.sleep(4);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("发短信");
    }

    public static synchronized void call() {
        System.out.println("打电话");
    }
}
```

```java
package com.cxy.lock8;

/**
 * @program: cxyJuc
 * @description:
 * @author: cuixy
 * @create: 2020-08-15 11:11
 **/

import java.util.concurrent.TimeUnit;

/**
 * 1、1个静态的同步方法，1个普通的同步方法 ，一个对象，先打印 发短信？打电话？
 * 2、1个静态的同步方法，1个普通的同步方法 ，两个对象，先打印 发短信？打电话？
 */
public class Test4 {
    public static void main(String[] args) {
        Phone4 phone1 = new Phone4();
        Phone4 phone2 = new Phone4();

        new Thread(() -> {
            phone1.sendSms();
        }, "A").start();
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        new Thread(() -> {
            phone2.call();
        }, "B").start();

    }

}

class Phone4 {

    public static synchronized void sendSms() {
        try {
            TimeUnit.SECONDS.sleep(4);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("发短信");
    }

    //普通的同步方法，锁的调用者。
    public synchronized void call() {
        System.out.println("打电话");
    }


}
```

> 小结

new this 具体的一个手机
static Class 唯一的一个模板

# 集合类不安全

## List

```java
package com.cxy.unsafe;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @program: cxyJuc
 * @description: List是不安全的。
 * @author: cuixy
 * @create: 2020-08-17 11:09
 **/
public class ListTest {
    public static void main(String[] args) {
        //并发下ArrayList是不安全的，java.util.ConcurrentModificationException 并发修改异常。
        //ArrayList<String> list = new ArrayList<>();

        //方案1
        //Vector<String> list = new Vector<>();
        //方案2
        //List<Object> list = Collections.synchronizedList(new ArrayList<>());
        //方案3
        CopyOnWriteArrayList<Object> list = new CopyOnWriteArrayList<>();
        //CopyOnWrite 写入时复制，多个线程调用的时候，List，读取的时候，固定的，写入(覆盖)
        //在写入的时候避免覆盖，造成数据问题。读写分离。
        //copyOnWriteArrayList快。

        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                list.add(UUID.randomUUID().toString().substring(0, 5));
                System.out.println(list);
            }, String.valueOf(i)).start();

        }


    }

}

```

## Set

```java
package com.cxy.unsafe;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @program: cxyJuc
 * @description: 测试Set。
 * @author: cuixy
 * @create: 2020-08-17 11:39
 **/
public class SetTest {
    public static void main(String[] args) {
        //同样的错误，java.util.ConcurrentModificationException。
        //HashSet<String> set = new HashSet<>();
        //方案1
        //Set<Object> set = Collections.synchronizedSet(new HashSet<>());
        //方案2
        CopyOnWriteArraySet<String> set = new CopyOnWriteArraySet<>();

        for (int i = 0; i < 50; i++) {
            new Thread(() -> {
                set.add(UUID.randomUUID().toString().substring(0, 5));
                System.out.println(set);
            }, String.valueOf(i)).start();
        }
    }
}
```

### hashset底层

```java
/**
     * Constructs a new, empty set; the backing <tt>HashMap</tt> instance has
     * default initial capacity (16) and load factor (0.75).
     */
    public HashSet() {
        map = new HashMap<>();
    }

    public boolean add(E e) {
        return map.put(e, PRESENT)==null;
    }
    // Dummy value to associate with an Object in the backing Map
    private static final Object PRESENT = new Object();
```

## Map

<img src="https://gitee.com/cuixiaoyan/uPic/raw/master/uPic/image-20200817134559319.png" alt="image-20200817134559319" style="zoom:50%;" />

```java
package com.cxy.unsafe;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: cxyJuc
 * @description:
 * @author: cuixy
 * @create: 2020-08-17 13:38
 **/
public class MapTest {
    public static void main(String[] args) {
        //同样报错，java.util.ConcurrentModificationException。
        //Map<String, String> map = new HashMap<>();
        //解决方案。
        ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
        for (int i = 0; i < 30; i++) {
            new Thread(() -> {
                map.put(Thread.currentThread().getName(), UUID.randomUUID().toString().substring(0, 5));
                System.out.println(map);
            }, String.valueOf(i)).start();
        }
    }
}
```

# Callable ( 简单 )

<img src="https://gitee.com/cuixiaoyan/uPic/raw/master/uPic/image-20200819161055864.png" alt="image-20200819161055864" style="zoom:50%;" />

<img src="https://gitee.com/cuixiaoyan/uPic/raw/master/uPic/image-20200819161114937.png" alt="image-20200819161114937" style="zoom:50%;" />

```java
package com.cxy.callable;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * @program: cxyJuc
 * @description:
 * @author: cuixy
 * @create: 2020-08-19 16:11
 **/
public class CallableTest {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
//        new Thread(new Runnable()).start();
//        new Thread(new FutureTask<V>()).start();;
//        new Thread(new FutureTask<V>(Callable)).start();
        new Thread().start(); //如何启动Callable

        MyThread thread = new MyThread();
        FutureTask futureTask = new FutureTask(thread); //适配类

        new Thread(futureTask, "A").start();
        new Thread(futureTask, "B").start();//结果会被缓存，效率高

        Integer o = (Integer) futureTask.get();
        System.out.println(o);
    }
}

class MyThread implements Callable<Integer> {
    @Override
    public Integer call() throws Exception {
        System.out.println("call()");
        return 1024;
    }
}
```

细节：
1、有缓存
2、结果可能需要等待，会阻塞！

# 常用的辅助类(必会)

## CountDownLatch

<img src="https://gitee.com/cuixiaoyan/uPic/raw/master/uPic/image-20200821160604906.png" alt="image-20200821160604906" style="zoom:50%;" />

```java
package com.cxy.add;

import java.util.concurrent.CountDownLatch;
/**
 * @program: cxyJuc
 * @description:
 * @author: cuixy
 * @create: 2020-08-21 16:06
 **/
public class CountDownLatchDemo {
    //计数器
    public static void main(String[] args) throws InterruptedException {
        //总数是6，必须要执行任务的时候，再使用。
        CountDownLatch countDownLatch = new CountDownLatch(6);
        for (int i = 1; i < 6; i++) {
            new Thread(() -> {
                System.out.println(Thread.currentThread().getName() + "Go out");
                countDownLatch.countDown();
            }, String.valueOf(i)).start();
        }
        countDownLatch.await(); //等待计数器归零，然后再向下执行。
        System.out.println("Close Door");
    }
}
```

原理：
countDownLatch.countDown(); // 数量-1

countDownLatch.await(); // 等待计数器归零，然后再向下执行
每次有线程调用 countDown() 数量-1，假设计数器变为0，countDownLatch.await() 就会被唤醒，继续
执行！

## CyclicBarrier

<img src="https://gitee.com/cuixiaoyan/uPic/raw/master/uPic/image-20200825160441198.png" alt="image-20200825160441198" style="zoom:50%;" />

```java
package com.cxy.add;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * @program: cxyJuc
 * @description: 加法计数器
 * @author: cuixy
 * @create: 2020-08-25 16:06
 **/
public class CyclicBarrierDemo {
    public static void main(String[] args) {
        CyclicBarrier cyclicBarrier = new CyclicBarrier(7, () -> {
            System.out.println("七剑合璧");
        });
        for (int i = 0; i < 7; i++) {
            final int temp = i;
            new Thread(()->{
                System.out.println(Thread.currentThread().getName() + "集齐" + temp + "把剑");
                try {
                    cyclicBarrier.await();//等待
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}
```

## Semaphore

<img src="https://gitee.com/cuixiaoyan/uPic/raw/master/uPic/image-20200825162014241.png" alt="image-20200825162014241" style="zoom:50%;" />

```java
package com.cxy.add;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * @program: cxyJuc
 * @description: 模拟抢车位。
 * @author: cuixy
 * @create: 2020-08-25 16:20
 **/
public class SemaphoreDemo {
    public static void main(String[] args) {
        //线程数量，限流。
        Semaphore semaphore = new Semaphore(3);
        for (int i = 0; i < 6; i++) {
            new Thread(()->{
                try {
                    semaphore.acquire();//得到。
                    System.out.println(Thread.currentThread().getName() + "抢到车位了");
                    TimeUnit.SECONDS.sleep(2);
                    System.out.println(Thread.currentThread().getName() + "离开车位了");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    semaphore.release(); //释放。
                }
            },String.valueOf(i)).start();
        }
    }

}
```

原理：
semaphore.acquire() 获得，假设如果已经满了，等待，等待被释放为止！

semaphore.release(); 释放，会将当前的信号量释放 + 1，然后唤醒等待的线程！
作用： 多个共享资源互斥的使用！并发限流，控制最大的线程数！

# 读写锁

<img src="https://gitee.com/cuixiaoyan/uPic/raw/master/uPic/image-20200828102556485.png" alt="image-20200828102556485" style="zoom:50%;" />

<img src="https://gitee.com/cuixiaoyan/uPic/raw/master/uPic/image-20200828105330733.png" alt="image-20200828105330733" style="zoom:50%;" />

## 效果如下

<img src="https://gitee.com/cuixiaoyan/uPic/raw/master/uPic/image-20200828110953516.png" alt="image-20200828110953516" style="zoom:50%;" />

```java
package com.cxy.rw;

/**
 * @program: cxyJuc
 * @description:
 * @author: cuixy
 * @create: 2020-08-28 10:26
 **/

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 独占锁（写锁） 一次只能被一个线程占有
 * 共享锁（读锁） 多个线程可以同时占有
 * ReadWriteLock
 * 读-读 可以共存！
 * 读-写 不能共存！
 * 写-写 不能共存！
 */
public class ReadWriteLockDemo {
    public static void main(String[] args) {

        MyCacheLock myCache = new MyCacheLock();
        //写入
        for (int i = 0; i < 5; i++) {
            final int temp = i;
            new Thread(() -> {
                myCache.put(String.valueOf(temp), temp);
            }, String.valueOf(i)).start();
        }
        //读取
        for (int i = 0; i < 5; i++) {
            final int temp = i;
            new Thread(() -> {
                myCache.get(String.valueOf(temp));
            }, String.valueOf(i)).start();
        }


    }

}

/**
 * 加锁读自定义缓存
 */
class MyCacheLock {
    private volatile Map<String, Object> map = new HashMap<>();
    //读写锁，更加细粒度读控制
    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
//    private Lock lock = new ReentrantLock();


    //存入，写入读时候，只希望一个线程写。
    public void put(String key, Object value) {
        readWriteLock.writeLock().lock();
        try {
            System.out.println(Thread.currentThread().getName() + "写入" + key);
            map.put(key, value);
            System.out.println(Thread.currentThread().getName() + "写入完成");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    //取出，读，所有人都可以读。
    public void get(String key) {
        readWriteLock.readLock().lock();
        try {
            System.out.println(Thread.currentThread().getName() + "读取" + key);
            Object o = map.get(key);
            System.out.println(Thread.currentThread().getName() + "读取完成");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            readWriteLock.readLock().unlock();
        }
    }


}


/**
 * 自定义缓存
 */
class MyCache {
    private volatile Map<String, Object> map = new HashMap<>();

    //存入，写
    public void put(String key, Object value) {
        System.out.println(Thread.currentThread().getName() + "写入" + key);
        map.put(key, value);
        System.out.println(Thread.currentThread().getName() + "写入完成");
    }

    //取出，读
    public void get(String key) {
        System.out.println(Thread.currentThread().getName() + "读取" + key);
        Object o = map.get(key);
        System.out.println(Thread.currentThread().getName() + "读取完成");
    }
}

```

# 阻塞队列

<img src="https://gitee.com/cuixiaoyan/uPic/raw/master/uPic/image-20200831104421328.png" alt="image-20200831104421328" style="zoom:50%;" />

<img src="https://gitee.com/cuixiaoyan/uPic/raw/master/uPic/image-20200831104504237.png" alt="image-20200831104504237" style="zoom:50%;" />

<img src="https://gitee.com/cuixiaoyan/uPic/raw/master/uPic/image-20200831104526399.png" alt="image-20200831104526399" style="zoom:50%;" />

```java
package com.cxy.queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @program: cxyJuc
 * @description: 队列测试
 * @author: cuixy
 * @create: 2020-08-31 10:46
 **/
public class testQueue {
    public static void main(String[] args) {
        test4();
    }
    /**
     * 等待，阻塞（等待超时）
     */
    public static void test4() {
        try {
            ArrayBlockingQueue blockingQueue = new ArrayBlockingQueue<>(3);
            blockingQueue.offer("1");
            blockingQueue.offer("2");
            blockingQueue.offer("3");
            blockingQueue.offer("4", 2, TimeUnit.SECONDS);//等待超过两秒就退出。
            //超过数量，将一直阻塞。
            System.out.println(blockingQueue.poll());
            System.out.println(blockingQueue.poll());
            System.out.println(blockingQueue.poll());
            System.out.println(blockingQueue.poll(2, TimeUnit.SECONDS));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    /**
     * 有返回值，没有异常。
     */
    public static void test3() {
        try {
            ArrayBlockingQueue blockingQueue = new ArrayBlockingQueue<>(3);
            blockingQueue.put("1");
            blockingQueue.put("2");
            blockingQueue.put("3");
//            blockingQueue.put("4");
            //超过数量，将一直阻塞。
            System.out.println(blockingQueue.take());
            System.out.println(blockingQueue.take());
            System.out.println(blockingQueue.take());
//            System.out.println(blockingQueue.take());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    /**
     * 有返回值，没有异常。
     */
    public static void test2() {
        ArrayBlockingQueue blockingQueue = new ArrayBlockingQueue<>(3);
        System.out.println(blockingQueue.offer("1"));
        System.out.println(blockingQueue.offer("2"));
        System.out.println(blockingQueue.offer("3"));
        System.out.println(blockingQueue.offer("4"));

        System.out.println("--------------------");

        System.out.println(blockingQueue.poll());
        System.out.println(blockingQueue.poll());
        System.out.println(blockingQueue.poll());
        System.out.println(blockingQueue.poll());
    }

    /**
     * 抛出异常
     */
    public static void test1() {
        ArrayBlockingQueue blockingQueue = new ArrayBlockingQueue<>(3);
        System.out.println(blockingQueue.add("1"));
        System.out.println(blockingQueue.add("2"));
        System.out.println(blockingQueue.add("3"));
//        System.out.println(blockingQueue.add("4"));

        System.out.println("--------------------");

        System.out.println(blockingQueue.remove());
        System.out.println(blockingQueue.remove());
        System.out.println(blockingQueue.remove());
//        System.out.println(blockingQueue.remove());

        //java.lang.IllegalStateException
    }
}
```



> SynchronousQueue 同步队列

没有容量，
进去一个元素，必须等待取出来之后，才能再往里面放一个元素！
put、take

```java
package com.cxy.queue;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

/**
 * @program: cxyJuc
 * @description: 同步队列，和其他的BlockingQueue不一样，SynchronousQueue不存储元素。
 * put一个元素，必须从里面先take取出来，否则不能在put进去值。
 * @author: cuixy
 * @create: 2020-09-01 10:35
 **/
public class SynchronousQueueDemo {
    public static void main(String[] args) {
        SynchronousQueue<String> blockingQueue = new SynchronousQueue<>();//同步队列

        new Thread(() -> {
            try {
                System.out.println(Thread.currentThread().getName() + "put 1");
                blockingQueue.put("1");
                System.out.println(Thread.currentThread().getName() + "put 2");
                blockingQueue.put("2");
                System.out.println(Thread.currentThread().getName() + "put 3");
                blockingQueue.put("3");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "T1").start();

        new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(3);
                System.out.println(Thread.currentThread().getName() + "=>" + blockingQueue.take());
                TimeUnit.SECONDS.sleep(3);
                System.out.println(Thread.currentThread().getName() + "=>" + blockingQueue.take());
                TimeUnit.SECONDS.sleep(3);
                System.out.println(Thread.currentThread().getName() + "=>" + blockingQueue.take());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }, "T2").start();
    }
}
```

# 线程池

线程池：三大方法、7大参数、4种拒绝策略

> 池化技术

程序的运行，本质：占用系统的资源！ 优化资源的使用！=>池化技术
线程池、连接池、内存池、对象池///..... 创建、销毁。十分浪费资源
池化技术：事先准备好一些资源，有人要用，就来我这里拿，用完之后还给我。
线程池的好处:
1、降低资源的消耗
2、提高响应的速度
3、方便管理。
线程复用、可以控制最大并发数、管理线程。

>线程池：三大方法

<img src="https://gitee.com/cuixiaoyan/uPic/raw/master/uPic/image-20200901105827836.png" alt="image-20200901105827836" style="zoom:50%;" />

```java
package com.cxy.pool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @program: cxyJuc
 * @description: Executors 工具类，3大方法。
 * @author: cuixy
 * @create: 2020-09-01 11:05
 **/
public class Demo01 {
    public static void main(String[] args) {
        //单个线程
//        ExecutorService threadPool = Executors.newSingleThreadExecutor();

        //创建一个固定的线程池的大小
//        ExecutorService threadPool = Executors.newFixedThreadPool(5);


        //可伸缩的
        ExecutorService threadPool = Executors.newCachedThreadPool();

        try {
            for (int i = 0; i < 100; i++) {
                //使用线程池之后，使用线程池来创建线程。
                threadPool.execute(() -> {
                    System.out.println(Thread.currentThread().getName() + "ok");
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //线程池用完，程序结束，关闭线程池。
            threadPool.shutdown();
        }


    }

}
```

> 七大参数

源码分析

```java
 public ThreadPoolExecutor(int corePoolSize,// 核心线程池大小
                              int maximumPoolSize,// 最大核心线程池大小
                              long keepAliveTime,// 超时了没有人调用就会释放
                              TimeUnit unit,// 超时单位
                              BlockingQueue<Runnable> workQueue// 阻塞队列) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
             Executors.defaultThreadFactory(), defaultHandler // 拒绝策略);
    }
```

<img src="https://gitee.com/cuixiaoyan/uPic/raw/master/uPic/image-20200901113440475.png" alt="image-20200901113440475" style="zoom:50%;" />

<img src="https://gitee.com/cuixiaoyan/uPic/raw/master/uPic/image-20200901113459121.png" alt="image-20200901113459121" style="zoom:50%;" />

> 手动创建一个线程池

```java
package com.cxy.pool;
/**
 * @program: cxyJuc
 * @description: Executors 3大方法
 * @author: cuixy
 * @create: 2020-09-07 10:24
 **/
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
/**
 * new ThreadPoolExecutor.AbortPolicy() 银行满了，还有人进来，不处理这个人的，抛出异常。
 * new ThreadPoolExecutor.CallerRunsPolicy() 哪里来的去哪里。
 * new ThreadPoolExecutor.DiscardPolicy() 队列满了，丢掉任务，不会抛出异常
 * new ThreadPoolExecutor.DiscardoldestPolicy() 队列满了，尝试去和最早的竞争，也不会抛出异常
 */
public class Demo02 {
    public static void main(String[] args) {
        // ThreadPoolExecutor,自定义线程池
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
                2,
                5,
                3,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(3),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.DiscardOldestPolicy());//队列满了，尝试去和最早的竞争，也不会抛出异常
        //最大承载：Deque + max
        //超过 RejectedExecutionException
        try {
            for (int i = 0; i < 10; i++) {
                //使用线程池之后，使用线程来创建线程。
                threadPool.execute(()->{
                    System.out.println(Thread.currentThread().getName() + "ok");
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //线程池用完，线程结束，关闭线程池。
            threadPool.shutdown();
        }
    }
}
```

> 小结

池的最大的大小如何去设置！
了解：IO密集型，CPU密集型：（调优）

# 四大函数式接口

新时代的程序员：lambda表达式、链式编程、函数式接口、Stream流式计算

>函数式接口： 只有一个方法的接口

```java
@FunctionalInterface
public interface Runnable {}
// 泛型、枚举、反射
// lambda表达式、链式编程、函数式接口、Stream流式计算
// 超级多FunctionalInterface
// 简化编程模型，在新版本的框架底层大量应用！
// foreach(消费者类的函数式接口)
```

<img src="https://gitee.com/cuixiaoyan/uPic/raw/master/uPic/image-20200907111934776.png" alt="image-20200907111934776" style="zoom:50%;" />

>Function函数式接口

<img src="https://gitee.com/cuixiaoyan/uPic/raw/master/uPic/image-20200907112353934.png" alt="image-20200907112353934" style="zoom:50%;" />

```java
//函数型接口, 有一个输入参数，有一个输出
        Function<String, String> function = (str) -> {
            return str;
        };
        System.out.println(function.apply("cxy"));
```

>断定型接口：有一个输入参数，返回值只能是 布尔值！

<img src="https://gitee.com/cuixiaoyan/uPic/raw/master/uPic/image-20200907114007013.png" alt="image-20200907114007013" style="zoom:50%;" />

```java
 //断定型接口：有一个输入参数，返回值只能是 布尔值！
        Predicate<String> predicate = (str) -> {
            return str.isEmpty();
        };
        System.out.println(predicate.test(""));
```

>Consumer 消费型接口

<img src="https://gitee.com/cuixiaoyan/uPic/raw/master/uPic/image-20200907144144439.png" alt="image-20200907144144439" style="zoom:50%;" />

```java
//Consumer 消费型接只有输入，没有返回值
        Consumer<String> consumer = (str) ->{
            System.out.println(str);
        };
        consumer.accept("consumer");
```

>Supplier 供给型接口

<img src="https://gitee.com/cuixiaoyan/uPic/raw/master/uPic/image-20200907151818542.png" alt="image-20200907151818542" style="zoom:50%;" />

```java
//Supplier 供给型接口 没有参数，只有返回值
        Supplier supplier = ()->{return 1024;};
        System.out.println(supplier.get());
```

# Stream流式计算

>什么是Stream流式计算

大数据：存储 + 计算
集合、MySQL 本质就是存储东西的；
计算都应该交给流来操作！

```java
package com.cxy.stream;

/**
 * @program: cxyJuc
 * @description:
 * @author: cuixy
 * @create: 2020-09-07 15:45
 **/
import java.util.Arrays;
import java.util.List;
/**
 * 题目要求：
 * 一分钟内完成此题，只能用一行代码实现！
 * 现在有5个用户！筛选：
 * 1、ID 必须是偶数
 * 2、年龄必须大于23岁
 * 3、用户名转为大写字母
 * 4、用户名字母倒着排序
 * 5、只输出一个用户！
 */
public class Test {
    public static void main(String[] args) {
        cxyUser u1 = new cxyUser(1, "a", 21);
        cxyUser u2 = new cxyUser(2, "b", 22);
        cxyUser u3 = new cxyUser(3, "c", 23);
        cxyUser u4 = new cxyUser(4, "d", 24);
        cxyUser u5 = new cxyUser(5, "e", 25);

        //集合存储
        List<cxyUser> list = Arrays.asList(u1, u2, u3, u4, u5);
        //计算交给Stream流
        //lambda表达式，链式编程，函数式接口，Stream流计算
        list.stream()
//                .filter(u -> { return u.getId() % 2 == 0; })
                .filter(u->{return u.getAge() > 23;})
                .map(u->{return u.getName().toUpperCase();})
                .sorted((uu1,uu2)->{return uu2.compareTo(uu1);})
//                .limit(1)
                .forEach(System.out::println);
    }
}
```

# ForkJoin

ForkJoin 在 JDK 1.7 ， 并行执行任务！提高效率。大数据量！
大数据：Map Reduce （把大任务拆分为小任务）

<img src="https://gitee.com/cuixiaoyan/uPic/raw/master/uPic/image-20200909141142281.png" alt="image-20200909141142281" style="zoom:50%;" />

>ForkJoin 特点：工作窃取

这个里面维护的都是双端队列

<img src="https://gitee.com/cuixiaoyan/uPic/raw/master/uPic/image-20200909143828622.png" alt="image-20200909143828622" style="zoom:50%;" />

>ForkJoin

<img src="https://gitee.com/cuixiaoyan/uPic/raw/master/uPic/image-20200909144531524.png" alt="image-20200909144531524" style="zoom:50%;" />

```java
package com.cxy.forkjoin;

/**
 * @program: cxyJuc
 * @description:
 * @author: cuixy
 * @create: 2020-09-09 14:48
 **/

import java.util.concurrent.RecursiveTask;

/**
 * 求和计算的任务！
 * 3000
 * 6000（ForkJoin） 9000（Stream并行流）
 * // 如何使用 forkjoin
 * // 1、forkjoinPool 通过它来执行
 * // 2、计算任务 forkjoinPool.execute(ForkJoinTask task)
 * // 3. 计算类要继承 ForkJoinTask
 */
public class ForkJoinDemo extends RecursiveTask<Long> {

    private Long start; //1
    private Long end; //1990900000

    //临界值
    private Long temp = 10000L;

    public ForkJoinDemo(Long start, Long end) {
        this.start = start;
        this.end = end;
    }


    //计算方法
    @Override
    protected Long compute() {
        if ((end - start) < temp) {
            Long sum = 0L;
            for (Long i = start; i <= end; i++) {
                sum += i;
            }
            return sum;
        } else { // forkjoin 递归
            long middle = (start + end) / 2;
            ForkJoinDemo task1 = new ForkJoinDemo(start, middle);
            task1.fork();//拆分任务，把任务压入线程队列。
            ForkJoinDemo task2 = new ForkJoinDemo(middle + 1, end);
            task2.fork();
            return task1.join() + task2.join();
        }
    }
}
```

## 测试类

```java
package com.cxy.forkjoin;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.stream.LongStream;

/**
 * @program: cxyJuc
 * @description: 同一个任务，效率高几十倍。一亿条记录。
 * @author: cuixy
 * @create: 2020-09-09 15:56
 **/
public class Test {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
//        test1(); 6088
//        test2(); 610
//        test3(); 113

    }

    //普通程序
    public static void test1() {
        Long sum = 0L;
        Long start = System.currentTimeMillis();
        for (Long i = 1L; i < 100000000L; i++) {
            sum += i;
        }
        Long end = System.currentTimeMillis();
        System.out.println("sum=" + sum + "时间:" + (end - start));
    }

    //会使用ForkJoin
    public static void test2() throws ExecutionException, InterruptedException {
        Long start = System.currentTimeMillis();
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        ForkJoinDemo task = new ForkJoinDemo(0L, 100000000L);
        ForkJoinTask<Long> submit = forkJoinPool.submit(task);//提交任务
        Long sum = submit.get();
        Long end = System.currentTimeMillis();
        System.out.println("sum=" + sum + "时间:" + (end - start));
    }

    //Stream并行流
    public static void test3(){
        Long start = System.currentTimeMillis();
        long sum = LongStream.rangeClosed(0L, 100000000L).parallel().reduce(0, Long::sum);
        Long end = System.currentTimeMillis();
        System.out.println("sum=" + sum + "时间:" + (end - start));
    }
}
```

# 异步回调

<img src="https://gitee.com/cuixiaoyan/uPic/raw/master/uPic/image-20200910141138667.png" alt="image-20200910141138667" style="zoom:50%;" />

```java
package com.cxy.future;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @program: cxyJuc
 * @description: 异步调用： CompletableFuture
 * @author: cuixy
 * @create: 2020-09-10 14:20
 **/

public class Demo01 {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        /**
         * 没有返回值的 runASnc 异步回调。
         */
//        CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(() -> {
//            try {
//                TimeUnit.SECONDS.sleep(2);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            System.out.println(Thread.currentThread().getName() + "runAsync=>coid");
//            System.out.println("111");
//        });
//        completableFuture.get();

        /**
         * 有返回值的 supplyAsync 异步回调。
         * ajax 成功和失败的回调。
         * 返回的是错误信息。
         */
        CompletableFuture<Integer> completableFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName() + "supplyAsync=>Integer");
            int i = 10 / 0;
            return 1024;
        });
        completableFuture.whenComplete((t, u) -> {
            System.out.println("t=>" + t);//正常
            System.out.println("u=>" + u);//错误

        }).exceptionally((e) -> {
            System.out.println(e.getMessage());
            return 222;
        }).get();
    }
}
```

# JMM

Volatile 是 Java 虚拟机提供轻量级的同步机制
1、保证可见性
2、不保证原子性
3、禁止指令重排

JMM ： Java内存模型，不存在的东西，概念！约定！
关于JMM的一些同步的约定：
1、线程解锁前，必须把共享变量立刻刷回主存。
2、线程加锁前，必须读取主存中的最新值到工作内存中！
3、加锁和解锁是同一把锁
线程
工作内存
主内存

<img src="https://gitee.com/cuixiaoyan/uPic/raw/master/uPic/image-20200911134200402.png" alt="image-20200911134200402" style="zoom:50%;" />

内存交互操作有8种，虚拟机实现必须保证每一个操作都是原子的，不可在分的（对于double和long类
型的变量来说，load、store、read和write操作在某些平台上允许例外）

- lock （锁定）：作用于主内存的变量，把一个变量标识为线程独占状态
- unlock （解锁）：作用于主内存的变量，它把一个处于锁定状态的变量释放出来，释放后的变量
  才可以被其他线程锁定
- read （读取）：作用于主内存变量，它把一个变量的值从主内存传输到线程的工作内存中，以便
  随后的load动作使用
- load （载入）：作用于工作内存的变量，它把read操作从主存中变量放入工作内存中
- use （使用）：作用于工作内存中的变量，它把工作内存中的变量传输给执行引擎，每当虚拟机
  遇到一个需要使用到变量的值，就会使用到这个指令
- assign （赋值）：作用于工作内存中的变量，它把一个从执行引擎中接受到的值放入工作内存的变
  量副本中
- store （存储）：作用于主内存中的变量，它把一个从工作内存中一个变量的值传送到主内存中，
  以便后续的write使用
- write （写入）：作用于主内存中的变量，它把store操作从工作内存中得到的变量的值放入主内
  存的变量中


## JMM对这八种指令的使用，制定了如下规则：

- 不允许read和load、store和write操作之一单独出现。即使用了read必须load，使用了store必须
  write
- 不允许线程丢弃他最近的assign操作，即工作变量的数据改变了之后，必须告知主存
- 不允许一个线程将没有assign的数据从工作内存同步回主内存
- 一个新的变量必须在主内存中诞生，不允许工作内存直接使用一个未被初始化的变量。就是怼变量
  实施use、store操作之前，必须经过assign和load操作
- 一个变量同一时间只有一个线程能对其进行lock。多次lock后，必须执行相同次数的unlock才能解
  锁
- 如果对一个变量进行lock操作，会清空所有工作内存中此变量的值，在执行引擎使用这个变量前，
  必须重新load或assign操作初始化变量的值
- 如果一个变量没有被lock，就不能对其进行unlock操作。也不能unlock一个被其他线程锁住的变量
- 对一个变量进行unlock操作之前，必须把此变量同步回主内存

<img src="https://gitee.com/cuixiaoyan/uPic/raw/master/uPic/image-20200911134627256.png" alt="image-20200911134627256" style="zoom:50%;" />

# Volatile

>1、保证可见性

```java
package com.cxy.tvolatile;

import java.util.concurrent.TimeUnit;

/**
 * @program: cxyJuc
 * @description: 保证可见性
 * @author: cuixy
 * @create: 2020-09-11 13:56
 **/
public class JmmDemo {
    //不加 volatile 程序就会死循环。
    //加 volatile 可以保证可见性
    private volatile static int num = 0;
    public static void main(String[] args) {
        new Thread(() -> {
            while (num == 0) {
            }
        }).start();
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        num = 1;
        System.out.println(num);
    }
}
```

>2、不保证原子性

原子性 : 不可分割
线程A在执行任务的时候，不能被打扰的，也不能被分割。要么同时成功，要么同时失败。

```java
package com.cxy.tvolatile;

/**
 * @program: cxyJuc
 * @description:
 * @author: cuixy
 * @create: 2020-09-11 14:19
 **/
// volatile 不保证原子性
public class VDemo02 {
    // volatile 不保证原子性
    private volatile static int num = 0;

    public static void add() {
        num++;
    }
    public static void main(String[] args) {
        //理论上num结果应该是两万,但是只有 18167
        for (int i = 1; i <= 20; i++) {
            new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    add();
                }
            }).start();
        }
        //两个线程 main和gc
        while (Thread.activeCount() > 2) {
            Thread.yield();
        }
        System.out.println(Thread.currentThread().getName() + " " + num);
    }
}
```

<img src="https://gitee.com/cuixiaoyan/uPic/raw/master/uPic/image-20200911150036549.png" alt="image-20200911150036549" style="zoom:50%;" />

这些类的底层都直接和操作系统挂钩！在内存中修改值！Unsafe类是一个很特殊的存在！

> 指令重排

什么是 指令重排：你写的程序，计算机并不是按照你写的那样去执行的。
源代码-->编译器优化的重排--> 指令并行也可能会重排--> 内存系统也会重排---> 执行
处理器在进行指令重排的时候，考虑：数据之间的依赖性！

int x = 1; // 1
int y = 2; // 2
x = x + 5; // 3
y = x * x; // 4

我们所期望的：1234 但是可能执行的时候回变成 2134 1324
可不可能是 4123！

可能造成影响的结果： a b x y 这四个值默认都是 0；

<img src="https://gitee.com/cuixiaoyan/uPic/raw/master/uPic/image-20200914095349648.png" alt="image-20200914095349648" style="zoom:50%;" />

正常的结果： x = 0；y = 0；但是可能由于指令重排

<img src="https://gitee.com/cuixiaoyan/uPic/raw/master/uPic/image-20200914095407351.png" alt="image-20200914095407351" style="zoom:50%;" />

volatile可以避免指令重排：
内存屏障。CPU指令。作用：
1、保证特定的操作的执行顺序！
2、可以保证某些变量的内存可见性 （利用这些特性volatile实现了可见性）

<img src="/Users/cuixiaoyan/Library/Application%20Support/typora-user-images/image-20200914095457456.png" alt="image-20200914095457456" style="zoom:50%;" />

Volatile 是可以保持 可见性。不能保证原子性，由于内存屏障，可以保证避免指令重排的现象产生！

# 单例模式

饿汉式 DCL懒汉式，深究！

## 饿汉式

```java
package com.cxy.single;

/**
 * @program: cxyJuc
 * @description: 饿汉式
 * @author: cuixy
 * @create: 2020-09-14 10:03
 **/
public class Hungry {
    //可能会浪费空间
    private byte[] data1 = new byte[1024 * 1024];
    private byte[] data2 = new byte[1024 * 1024];
    private byte[] data3 = new byte[1024 * 1024];
    private byte[] data4 = new byte[1024 * 1024];

    private Hungry() {
    }

    private final static Hungry HUNGRY = new Hungry();

    public static Hungry getInstance() {
        return HUNGRY;
    }
}
```

## DCL 懒汉式

```java
package com.cxy.single;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

/**
 * @program: cxyJuc
 * @description: 懒汉式单例
 * @author: cuixy
 * @create: 2020-09-14 10:25
 **/
public class LazyMan {
    //标志位
    private static boolean cxy = false;

    private LazyMan() {
        synchronized (LazyMan.class) {
            if (cxy == false) {
                cxy = true;
            } else {
                throw new RuntimeException("不要试图使用反射破坏异常");
            }
        }
    }

    private volatile static LazyMan lazyMan;

    //双重检测锁模式的，懒汉式单例，DCL懒汉式
    public static LazyMan getInstance() {
        if (lazyMan == null) {
            synchronized (LazyMan.class) {
                if (lazyMan == null) {
                    lazyMan = new LazyMan();//不是一个原子性操作
                }
            }
        }
        return lazyMan;
    }

    //反射
    public static void main(String[] args) throws Exception {
        Field cxy = LazyMan.class.getDeclaredField("cxy");
        cxy.setAccessible(true);

        Constructor<LazyMan> declaredConstructor = LazyMan.class.getDeclaredConstructor(null);
        declaredConstructor.setAccessible(true);
        LazyMan instance = declaredConstructor.newInstance();

        cxy.set(instance, false);

        LazyMan instance1 = declaredConstructor.newInstance();

        System.out.println(instance);
        System.out.println(instance1);


    }

    /**
     * 1. 分配内存空间
     * 2、执行构造方法，初始化对象
     * 3、把这个对象指向这个空间
     *
     * 123
     * 132 A
     *
     B // 此时lazyMan还没有完成构造
     */

}
```

## 静态内部类

```java
package com.cxy.single;

import com.sun.org.apache.bcel.internal.classfile.InnerClass;

/**
 * @program: cxyJuc
 * @description: 静态内部类。
 * @author: cuixy
 * @create: 2020-09-14 11:18
 **/
public class Holder {
    private Holder() {

    }

    public static Holder getInstance() {
        return InnerClass.HOLDER;
    }

    public static class InnerClass {
        private static final Holder HOLDER = new Holder();
    }


}
```

