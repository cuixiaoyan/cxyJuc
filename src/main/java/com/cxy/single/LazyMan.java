package com.cxy.single;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

/**
 * @program: cxyJuc
 * @description: 懒汉式单例
 * @author: cuixy
 * @create: 2020-09-14 10:25
 **/
public class LazyMan {
    //标志位
    private static boolean cxy = false;

    private LazyMan() {
        synchronized (LazyMan.class) {
            if (cxy == false) {
                cxy = true;
            } else {
                throw new RuntimeException("不要试图使用反射破坏异常");
            }
        }
    }

    private volatile static LazyMan lazyMan;

    //双重检测锁模式的，懒汉式单例，DCL懒汉式
    public static LazyMan getInstance() {
        if (lazyMan == null) {
            synchronized (LazyMan.class) {
                if (lazyMan == null) {
                    lazyMan = new LazyMan();//不是一个原子性操作
                }
            }
        }
        return lazyMan;
    }

    //反射
    public static void main(String[] args) throws Exception {
        Field cxy = LazyMan.class.getDeclaredField("cxy");
        cxy.setAccessible(true);

        Constructor<LazyMan> declaredConstructor = LazyMan.class.getDeclaredConstructor(null);
        declaredConstructor.setAccessible(true);
        LazyMan instance = declaredConstructor.newInstance();

        cxy.set(instance, false);

        LazyMan instance1 = declaredConstructor.newInstance();

        System.out.println(instance);
        System.out.println(instance1);


    }

    /**
     * 1. 分配内存空间
     * 2、执行构造方法，初始化对象
     * 3、把这个对象指向这个空间
     *
     * 123
     * 132 A
     *
     B // 此时lazyMan还没有完成构造
     */

}