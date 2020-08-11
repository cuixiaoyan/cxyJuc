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
        Ticket1 ticket = new Ticket1();
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
}

//资源类 OOP
class Ticket1 {
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


