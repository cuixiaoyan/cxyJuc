package com.cxy.function;

/**
 * @program: cxyJuc
 * @description:
 * @author: cuixy
 * @create: 2020-09-07 11:25
 **/

import com.cxy.pc.C;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * 只要是 函数型接口 可以 用 lambda表达式简化
 */
public class Demo01 {
    public static void main(String[] args) {

        //函数型接口, 有一个输入参数，有一个输出
        Function<String, String> function = (str) -> {
            return str;
        };
        System.out.println(function.apply("cxy"));

        //断定型接口：有一个输入参数，返回值只能是 布尔值！
        Predicate<String> predicate = (str) -> {
            return str.isEmpty();
        };
        System.out.println(predicate.test(""));

        //Consumer 消费型接只有输入，没有返回值
        Consumer<String> consumer = (str) ->{
            System.out.println(str);
        };
        consumer.accept("consumer");

        //Supplier 供给型接口 没有参数，只有返回值
        Supplier supplier = ()->{return 1024;};
        System.out.println(supplier.get());


    }
}