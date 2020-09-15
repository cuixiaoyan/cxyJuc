package com.cxy.single;

import java.lang.reflect.Constructor;

/**
 * @program: cxyJuc
 * @description: 枚举
 * @author: cuixy
 * @create: 2020-09-15 10:19
 **/
public enum EnumSingle {
    INSTANCE;
    public EnumSingle getInstance() {
        return INSTANCE;
    }
}

class Test {
    public static void main(String[] args) throws Exception {
        EnumSingle instance1 = EnumSingle.INSTANCE;
        Constructor<EnumSingle> declaredConstructor =
                EnumSingle.class.getDeclaredConstructor(String.class, int.class);
        declaredConstructor.setAccessible(true);
        EnumSingle instance2 = declaredConstructor.newInstance();
        // NoSuchMethodException: com.cxy.single.Test.main(EnumSingle.java:28)
        System.out.println(instance1);
        System.out.println(instance2);
    }
}