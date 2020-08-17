package com.cxy.unsafe;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @program: cxyJuc
 * @description: 测试Set。
 * @author: cuixy
 * @create: 2020-08-17 11:39
 **/
public class SetTest {
    public static void main(String[] args) {
        //同样的错误，java.util.ConcurrentModificationException。
        //HashSet<String> set = new HashSet<>();
        //方案1
        //Set<Object> set = Collections.synchronizedSet(new HashSet<>());
        //方案2
        CopyOnWriteArraySet<String> set = new CopyOnWriteArraySet<>();

        for (int i = 0; i < 50; i++) {
            new Thread(() -> {
                set.add(UUID.randomUUID().toString().substring(0, 5));
                System.out.println(set);
            }, String.valueOf(i)).start();
        }
    }
}