

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

