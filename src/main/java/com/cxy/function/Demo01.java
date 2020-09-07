package com.cxy.function;

/**
 * @program: cxyJuc
 * @description:
 * @author: cuixy
 * @create: 2020-09-07 11:25
 **/

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Function 函数型接口, 有一个输入参数，有一个输出
 * 只要是 函数型接口 可以 用 lambda表达式简化
 */
public class Demo01 {
    public static void main(String[] args) {
        Function<String, String> function = (str) -> {
            return str;
        };
        System.out.println(function.apply("cxy"));

        Predicate<String> predicate = (str) -> {
            return str.isEmpty();
        };
        System.out.println(predicate.test(""));

    }
}