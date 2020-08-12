package com.cxy.pc;


import javax.xml.crypto.Data;

/**
 * @program: cxyJuc
 * @description: 线程之间的通讯问题，生产者和消费者模式，等待唤醒，通知唤醒。
 * 线程交替执行 A B 操作同一个变量 num = 0
 * A num + 1
 * B num - 1
 * @author: cuixy
 * @create: 2020-08-12 15:34
 **/
public class A {
    public static void main(String[] args) {
        MyData myData = new MyData();

        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    myData.increment();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "A").start();

        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    myData.decrement();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "B").start();

        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    myData.increment();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "C").start();

        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    myData.decrement();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "D").start();

    }

}

class MyData {
    private int number = 0;

    //+1
    public synchronized void increment() throws InterruptedException {
        //if修改为while
        while (number != 0) { //0
            //等待
            this.wait();
        }
        number++;
        System.out.println(Thread.currentThread().getName() + "=>" + number);
        //通知其他线程+1执行完毕。
        this.notifyAll();

    }

    //-1
    public synchronized void decrement() throws InterruptedException {
        //if修改为while
        while (number == 0) {//1
            //等待
            this.wait();
        }
        number--;
        System.out.println(Thread.currentThread().getName() + "=>" + number);
        //通知其他线程 -1完毕。
        this.notifyAll();
    }


}