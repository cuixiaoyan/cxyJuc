package com.cxy.callable;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * @program: cxyJuc
 * @description:
 * @author: cuixy
 * @create: 2020-08-19 16:11
 **/
public class CallableTest {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
//        new Thread(new Runnable()).start();
//        new Thread(new FutureTask<V>()).start();;
//        new Thread(new FutureTask<V>(Callable)).start();
        new Thread().start(); //如何启动Callable

        MyThread thread = new MyThread();
        FutureTask futureTask = new FutureTask(thread); //适配类

        new Thread(futureTask, "A").start();
        new Thread(futureTask, "B").start();//结果会被缓存，效率高

        Integer o = (Integer) futureTask.get();
        System.out.println(o);
    }
}

class MyThread implements Callable<Integer> {
    @Override
    public Integer call() throws Exception {
        System.out.println("call()");
        return 1024;
    }
}