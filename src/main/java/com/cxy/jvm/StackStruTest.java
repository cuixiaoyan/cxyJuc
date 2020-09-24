package com.cxy.jvm;

/**
 * @program: cxyJuc
 * @description: 代码中包含static变量的时候，就会有clinit方法
 * @author: cuixy
 * @create: 2020-09-21 15:11
 **/
public class StackStruTest {

    private static int num = 1;

    static {
        num = 2;
        number = 20;
        System.out.println(num);
//        System.out.println(number);  //报错，非法的前向引用
    }

    private static int number = 10;


    public static void main(String[] args) {
        System.out.println(StackStruTest.num); // 2
        System.out.println(StackStruTest.number); // 10
    }

}