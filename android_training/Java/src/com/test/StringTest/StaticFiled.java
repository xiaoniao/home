package com.test.StringTest;

public class StaticFiled {
    static int level = 100; // 静态变量只会有一只内存区域

    public static void main(String[] args) {

        StaticFiled s1 = new StaticFiled();

        StaticFiled s2 = new StaticFiled();

        // 尝试改变值
        StaticFiled.level = 1;

        System.out.println(s1.level);
        System.out.println(s2.level);
        System.out.println(StaticFiled.level);

        
        System.getProperties().list(System.out);
        System.out.println(System.getProperty("user.name"));
        System.out.println(System.getProperty("java.library.path"));
        
    }
}
