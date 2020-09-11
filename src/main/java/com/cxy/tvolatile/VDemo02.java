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