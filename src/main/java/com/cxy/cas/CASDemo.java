package com.cxy.cas;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicStampedReference;

/**
 * @program: cxyJuc
 * @description: 带版本号的，原子操作。
 * @author: cuixy
 * @create: 2020-09-15 10:31
 **/
public class CASDemo {
    //AtomicsStampedRederence 如果泛型是一个包装类，注意对象的饮用规则。
    static AtomicStampedReference<Integer> atomicStampedReference = new AtomicStampedReference<>(1, 1);

    //CAS compareAndSet 比较并交换。
    public static void main(String[] args) {
        new Thread(() -> {
            int stamp = atomicStampedReference.getStamp();//获得版本号
            System.out.println("a1=>" + stamp);

            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            atomicStampedReference.compareAndSet(1, 2,
                    atomicStampedReference.getStamp(),
                    atomicStampedReference.getStamp() + 1);

            System.out.println("a2=>" + atomicStampedReference.getStamp());

            atomicStampedReference.compareAndSet(2, 1,
                    atomicStampedReference.getStamp(),
                    atomicStampedReference.getStamp() + 1);

            System.out.println("a3=>" + atomicStampedReference.getStamp());

        }, "a").start();

        //乐观锁原理相同。
        new Thread(() -> {
            int stamp = atomicStampedReference.getStamp();
            System.out.println("b1=>" + stamp);

            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            atomicStampedReference.compareAndSet(1, 6,
                    stamp, stamp + 1);
            System.out.println("b2=>" + atomicStampedReference.getStamp());

        }, "b").start();


    }

}