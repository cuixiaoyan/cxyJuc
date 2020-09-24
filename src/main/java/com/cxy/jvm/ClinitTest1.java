package com.cxy.jvm;

/**
 * @program: cxyJuc
 * @description:
 * @author: cuixy
 * @create: 2020-09-22 10:15
 **/
public class ClinitTest1 {
    public static void main(String[] args) {
        new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + "\t 线程t1开始");
            new DeadThread();
        }, "t1").start();

        new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + "\t 线程t2开始");
            new DeadThread();
        }, "t2").start();
    }
}

class DeadThread {
    static {
        if (true) {
            System.out.println(Thread.currentThread().getName() + "\t 初始化当前类");
            while (true) {

            }
        }
    }

}