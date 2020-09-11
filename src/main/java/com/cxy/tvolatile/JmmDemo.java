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