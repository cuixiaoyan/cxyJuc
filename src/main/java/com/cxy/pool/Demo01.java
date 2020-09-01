package com.cxy.pool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @program: cxyJuc
 * @description: Executors 工具类，3大方法。
 * @author: cuixy
 * @create: 2020-09-01 11:05
 **/
public class Demo01 {
    public static void main(String[] args) {
        //单个线程
//        ExecutorService threadPool = Executors.newSingleThreadExecutor();

        //创建一个固定的线程池的大小
//        ExecutorService threadPool = Executors.newFixedThreadPool(5);


        //可伸缩的
        ExecutorService threadPool = Executors.newCachedThreadPool();

        try {
            for (int i = 0; i < 100; i++) {
                //使用线程池之后，使用线程池来创建线程。
                threadPool.execute(() -> {
                    System.out.println(Thread.currentThread().getName() + "ok");
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //线程池用完，程序结束，关闭线程池。
            threadPool.shutdown();
        }


    }

}