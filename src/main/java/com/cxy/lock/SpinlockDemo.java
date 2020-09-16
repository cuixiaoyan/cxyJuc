package com.cxy.lock;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @program: cxyJuc
 * @description: 自旋锁
 * @author: cuixy
 * @create: 2020-09-16 15:01
 **/
public class SpinlockDemo {
    // int 0
    // Thread null
    AtomicReference<Thread> atomicReference = new AtomicReference<>();

    //加锁
    public void myLock() {
        Thread thread = Thread.currentThread();
        System.out.println(Thread.currentThread().getName() + "==> mylock");
        //自旋锁
        while (!atomicReference.compareAndSet(null, thread)) {

        }
    }

    //解锁
    public void myUnLock() {
        Thread thread = Thread.currentThread();
        System.out.println(Thread.currentThread().getName() + "==> myUnLock");
        atomicReference.compareAndSet(thread, null);
    }


}