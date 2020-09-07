package com.cxy.pool;

/**
 * @program: cxyJuc
 * @description: Executors 3大方法
 * @author: cuixy
 * @create: 2020-09-07 10:24
 **/

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * new ThreadPoolExecutor.AbortPolicy() 银行满了，还有人进来，不处理这个人的，抛出异常。
 * new ThreadPoolExecutor.CallerRunsPolicy() 哪里来的去哪里。
 * new ThreadPoolExecutor.DiscardPolicy() 队列满了，丢掉任务，不会抛出异常
 * new ThreadPoolExecutor.DiscardoldestPolicy() 队列满了，尝试去和最早的竞争，也不会抛出异常
 */
public class Demo02 {
    public static void main(String[] args) {
        // ThreadPoolExecutor,自定义线程池

        /**
         * 最大线程池该如何定义
         * cpu 密集型，几核，可以保证cpu的效率最高.
         * io 密集型 判断你程序中，十分耗io的线程。
         * 程序，15哥大型任务，io十分占用资源。
          */

        //获取cpu核树
        System.out.println(Runtime.getRuntime().availableProcessors());

        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
                2,
                Runtime.getRuntime().availableProcessors(),
                3,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(3),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.DiscardOldestPolicy());//队列满了，尝试去和最早的竞争，也不会抛出异常

        //最大承载：Deque + max
        //超过 RejectedExecutionException
        try {
            for (int i = 0; i < 10; i++) {
                //使用线程池之后，使用线程来创建线程。
                threadPool.execute(()->{
                    System.out.println(Thread.currentThread().getName() + "ok");
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //线程池用完，线程结束，关闭线程池。
            threadPool.shutdown();
        }


    }

}