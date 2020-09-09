package com.cxy.stream;

/**
 * @program: cxyJuc
 * @description:
 * @author: cuixy
 * @create: 2020-09-07 15:45
 **/

import java.util.Arrays;
import java.util.List;

/**
 * 题目要求：
 * 一分钟内完成此题，只能用一行代码实现！
 * 现在有5个用户！筛选：
 * 1、ID 必须是偶数
 * 2、年龄必须大于23岁
 * 3、用户名转为大写字母
 * 4、用户名字母倒着排序
 * 5、只输出一个用户！
 */
public class Test {
    public static void main(String[] args) {
        cxyUser u1 = new cxyUser(1, "a", 21);
        cxyUser u2 = new cxyUser(2, "b", 22);
        cxyUser u3 = new cxyUser(3, "c", 23);
        cxyUser u4 = new cxyUser(4, "d", 24);
        cxyUser u5 = new cxyUser(5, "e", 25);

        //集合存储
        List<cxyUser> list = Arrays.asList(u1, u2, u3, u4, u5);
        //计算交给Stream流
        //lambda表达式，链式编程，函数式接口，Stream流计算
        list.stream()
//                .filter(u -> { return u.getId() % 2 == 0; })
                .filter(u->{return u.getAge() > 23;})
                .map(u->{return u.getName().toUpperCase();})
                .sorted((uu1,uu2)->{return uu2.compareTo(uu1);})
//                .limit(1)
                .forEach(System.out::println);

    }


}