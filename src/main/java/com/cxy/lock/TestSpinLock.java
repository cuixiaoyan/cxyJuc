package com.cxy.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @program: cxyJuc
 * @description: 测试自旋锁
 * @author: cuixy
 * @create: 2020-09-16 15:21
 **/
public class TestSpinLock {
    public static void main(String[] args) throws InterruptedException {
        //官方可重入锁。
//        ReentrantLock reentrantLock = new ReentrantLock();
//        reentrantLock.lock();
//        reentrantLock.unlock();

        //底层使用的自旋锁CAS
        SpinlockDemo lock = new SpinlockDemo();

        new Thread(() -> {
            lock.myLock();

            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.myUnLock();
            }

        }, "T1").start();

        TimeUnit.SECONDS.sleep(1);

        new Thread(() -> {
            lock.myLock();

            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.myUnLock();
            }

        }, "T2").start();


    }

}