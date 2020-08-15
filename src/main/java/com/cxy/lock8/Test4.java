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