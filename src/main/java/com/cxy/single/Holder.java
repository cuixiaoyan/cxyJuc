package com.cxy.single;

import com.sun.org.apache.bcel.internal.classfile.InnerClass;

/**
 * @program: cxyJuc
 * @description: 静态内部类。
 * @author: cuixy
 * @create: 2020-09-14 11:18
 **/
public class Holder {
    private Holder() {

    }

    public static Holder getInstance() {
        return InnerClass.HOLDER;
    }

    public static class InnerClass {
        private static final Holder HOLDER = new Holder();
    }


}