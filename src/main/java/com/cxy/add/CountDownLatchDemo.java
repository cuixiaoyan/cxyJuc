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