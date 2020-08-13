package com.cxy.pc;


import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @program: cxyJuc
 * @description: juc版的生产者和消费者，有序版。
 * @author: cuixy
 * @create: 2020-08-12 16:07
 **/
public class C {
    public static void main(String[] args) {

        MyData3 myData3 = new MyData3();

        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                myData3.printA();

            }
        }, "A").start();


        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                myData3.printB();
            }
        }, "B").start();

        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                myData3.printC();
            }
        }, "C").start();


    }

}

//判断等待，业务，通知。
class MyData3 {


    Lock lock = new ReentrantLock();
    Condition condition1 = lock.newCondition();
    Condition condition2 = lock.newCondition();
    Condition condition3 = lock.newCondition();
    //1A 2B 3C
    private int number = 1;

    public void printA() {
        lock.lock();
        try {
            while (number != 1) {
                condition1.await();
            }
            System.out.println(Thread.currentThread().getName() + "=>" + number);
            //唤醒指定的B
            number = 2;
            condition2.signal();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void printB() {
        lock.lock();
        try {
            while (number != 2) {
                condition2.await();
            }
            System.out.println(Thread.currentThread().getName() + "=>" + number);
            //唤醒指定的C
            number = 3;
            condition3.signal();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void printC() {
        lock.lock();
        try {
            while (number != 3) {
                condition3.await();
            }
            System.out.println(Thread.currentThread().getName() + "=>" + number);
            //唤醒指定的A
            number = 1;
            condition1.signal();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }


}