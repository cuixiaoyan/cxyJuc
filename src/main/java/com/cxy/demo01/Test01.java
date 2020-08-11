package com.cxy.demo01;

import java.util.concurrent.locks.Lock;

/**
 * @program: cxyJuc
 * @description: 并发测试
 * @author: cuixy
 * @create: 2020-08-11 15:34
 **/
public class Test01 {
    public static void main(String[] args) {
        // 获取cpu的核数
        // CPU 密集型，IO密集型
        System.out.println(Runtime.getRuntime().availableProcessors());


    }

}