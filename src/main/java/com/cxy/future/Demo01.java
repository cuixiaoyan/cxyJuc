package com.cxy.future;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @program: cxyJuc
 * @description: 异步调用： CompletableFuture
 * @author: cuixy
 * @create: 2020-09-10 14:20
 **/

public class Demo01 {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        /**
         * 没有返回值的 runASnc 异步回调。
         */
//        CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(() -> {
//            try {
//                TimeUnit.SECONDS.sleep(2);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            System.out.println(Thread.currentThread().getName() + "runAsync=>coid");
//            System.out.println("111");
//        });
//        completableFuture.get();

        /**
         * 有返回值的 supplyAsync 异步回调。
         * ajax 成功和失败的回调。
         * 返回的是错误信息。
         */
        CompletableFuture<Integer> completableFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName() + "supplyAsync=>Integer");
            int i = 10 / 0;
            return 1024;
        });
        completableFuture.whenComplete((t, u) -> {
            System.out.println("t=>" + t);//正常
            System.out.println("u=>" + u);//错误

        }).exceptionally((e) -> {
            System.out.println(e.getMessage());
            return 222;
        }).get();
    }
}