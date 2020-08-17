package com.cxy.unsafe;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @program: cxyJuc
 * @description: List是不安全的。
 * @author: cuixy
 * @create: 2020-08-17 11:09
 **/
public class ListTest {
    public static void main(String[] args) {
        //并发下ArrayList是不安全的，java.util.ConcurrentModificationException 并发修改异常。
        //ArrayList<String> list = new ArrayList<>();

        //方案1
        //Vector<String> list = new Vector<>();
        //方案2
        //List<Object> list = Collections.synchronizedList(new ArrayList<>());
        //方案3
        CopyOnWriteArrayList<Object> list = new CopyOnWriteArrayList<>();
        //CopyOnWrite 写入时复制，多个线程调用的时候，List，读取的时候，固定的，写入(覆盖)
        //在写入的时候避免覆盖，造成数据问题。读写分离。
        //copyOnWriteArrayList快。

        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                list.add(UUID.randomUUID().toString().substring(0, 5));
                System.out.println(list);
            }, String.valueOf(i)).start();

        }


    }

}
