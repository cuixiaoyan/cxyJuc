package com.cxy.add;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * @program: cxyJuc
 * @description: 模拟抢车位。
 * @author: cuixy
 * @create: 2020-08-25 16:20
 **/
public class SemaphoreDemo {
    public static void main(String[] args) {
        //线程数量，限流。
        Semaphore semaphore = new Semaphore(3);
        for (int i = 0; i < 6; i++) {
            new Thread(()->{
                try {
                    semaphore.acquire();//得到。
                    System.out.println(Thread.currentThread().getName() + "抢到车位了");
                    TimeUnit.SECONDS.sleep(2);
                    System.out.println(Thread.currentThread().getName() + "离开车位了");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    semaphore.release(); //释放。
                }
            },String.valueOf(i)).start();
        }
    }

}