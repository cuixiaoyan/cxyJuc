package com.cxy.cas;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @program: cxyJuc
 * @description:
 * @author: cuixy
 * @create: 2020-09-15 10:31
 **/
public class CASDemo {
    //CAS compareAndSet 比较并交换。
    public static void main(String[] args) {
        AtomicInteger atomicInteger = new AtomicInteger(2020);
        //达到期望，就更新。
        System.out.println(atomicInteger.compareAndSet(2020, 2021));
        System.out.println(atomicInteger.get());

        System.out.println(atomicInteger.compareAndSet(2021, 2020));
        System.out.println(atomicInteger.get());

        System.out.println(atomicInteger.compareAndSet(2020, 6666));
        System.out.println(atomicInteger.get());

    }

}