package com.cxy.forkjoin;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.stream.LongStream;

/**
 * @program: cxyJuc
 * @description: 同一个任务，效率高几十倍。一亿条记录。
 * @author: cuixy
 * @create: 2020-09-09 15:56
 **/
public class Test {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
//        test1(); 6088
//        test2(); 610
//        test3(); 113

    }

    //普通程序
    public static void test1() {
        Long sum = 0L;
        Long start = System.currentTimeMillis();
        for (Long i = 1L; i < 100000000L; i++) {
            sum += i;
        }
        Long end = System.currentTimeMillis();
        System.out.println("sum=" + sum + "时间:" + (end - start));
    }

    //会使用ForkJoin
    public static void test2() throws ExecutionException, InterruptedException {
        Long start = System.currentTimeMillis();
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        ForkJoinDemo task = new ForkJoinDemo(0L, 100000000L);
        ForkJoinTask<Long> submit = forkJoinPool.submit(task);//提交任务
        Long sum = submit.get();
        Long end = System.currentTimeMillis();
        System.out.println("sum=" + sum + "时间:" + (end - start));
    }

    //Stream并行流
    public static void test3(){
        Long start = System.currentTimeMillis();
        long sum = LongStream.rangeClosed(0L, 100000000L).parallel().reduce(0, Long::sum);
        Long end = System.currentTimeMillis();
        System.out.println("sum=" + sum + "时间:" + (end - start));
    }
}