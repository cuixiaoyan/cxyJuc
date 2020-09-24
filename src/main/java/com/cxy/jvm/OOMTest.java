package com.cxy.jvm;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @program: cxyJuc
 * @description: outofMemoryError异常。
 * @author: cuixy
 * @create: 2020-09-24 15:10
 **/
public class OOMTest {
    public static void main(String[] args) {
        List<Integer> list = new ArrayList<>();


        try {
            TimeUnit.SECONDS.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        while (true) {
            list.add(999999999);
        }


    }

}