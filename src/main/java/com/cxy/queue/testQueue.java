package com.cxy.queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @program: cxyJuc
 * @description: 队列测试
 * @author: cuixy
 * @create: 2020-08-31 10:46
 **/
public class testQueue {
    public static void main(String[] args) {
        test4();
    }
    /**
     * 等待，阻塞（等待超时）
     */
    public static void test4() {
        try {
            ArrayBlockingQueue blockingQueue = new ArrayBlockingQueue<>(3);
            blockingQueue.offer("1");
            blockingQueue.offer("2");
            blockingQueue.offer("3");
            blockingQueue.offer("4", 2, TimeUnit.SECONDS);//等待超过两秒就退出。
            //超过数量，将一直阻塞。
            System.out.println(blockingQueue.poll());
            System.out.println(blockingQueue.poll());
            System.out.println(blockingQueue.poll());
            System.out.println(blockingQueue.poll(2, TimeUnit.SECONDS));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    /**
     * 有返回值，没有异常。
     */
    public static void test3() {
        try {
            ArrayBlockingQueue blockingQueue = new ArrayBlockingQueue<>(3);
            blockingQueue.put("1");
            blockingQueue.put("2");
            blockingQueue.put("3");
//            blockingQueue.put("4");
            //超过数量，将一直阻塞。
            System.out.println(blockingQueue.take());
            System.out.println(blockingQueue.take());
            System.out.println(blockingQueue.take());
//            System.out.println(blockingQueue.take());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    /**
     * 有返回值，没有异常。
     */
    public static void test2() {
        ArrayBlockingQueue blockingQueue = new ArrayBlockingQueue<>(3);
        System.out.println(blockingQueue.offer("1"));
        System.out.println(blockingQueue.offer("2"));
        System.out.println(blockingQueue.offer("3"));
        System.out.println(blockingQueue.offer("4"));

        System.out.println("--------------------");

        System.out.println(blockingQueue.poll());
        System.out.println(blockingQueue.poll());
        System.out.println(blockingQueue.poll());
        System.out.println(blockingQueue.poll());
    }

    /**
     * 抛出异常
     */
    public static void test1() {
        ArrayBlockingQueue blockingQueue = new ArrayBlockingQueue<>(3);
        System.out.println(blockingQueue.add("1"));
        System.out.println(blockingQueue.add("2"));
        System.out.println(blockingQueue.add("3"));
//        System.out.println(blockingQueue.add("4"));

        System.out.println("--------------------");

        System.out.println(blockingQueue.remove());
        System.out.println(blockingQueue.remove());
        System.out.println(blockingQueue.remove());
//        System.out.println(blockingQueue.remove());

        //java.lang.IllegalStateException
    }
}