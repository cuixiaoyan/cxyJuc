package com.cxy.unsafe;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: cxyJuc
 * @description:
 * @author: cuixy
 * @create: 2020-08-17 13:38
 **/
public class MapTest {
    public static void main(String[] args) {
        //同样报错，java.util.ConcurrentModificationException。
        //Map<String, String> map = new HashMap<>();
        //解决方案。
        ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
        for (int i = 0; i < 30; i++) {
            new Thread(() -> {
                map.put(Thread.currentThread().getName(), UUID.randomUUID().toString().substring(0, 5));
                System.out.println(map);
            }, String.valueOf(i)).start();
        }
    }
}