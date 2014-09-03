package com.test.StringTest;

public class Refer {

    static class User {
        String name;
    }

    public static void change(String str) {
        System.out.println("BB1 " + str.hashCode());
        str = "gbc";
        System.out.println("BB2 " + str.hashCode());
        System.out.println("change " + str);
    }

    public static void changeUser(User user) {
        user.name = "hah";
    }

    public static void changeInt(int a) {
        a = 20;
    }

    public static void main(String[] args) {

        // 但为什么String不呢?
        // String str = "abc";
        String str = new String("abc");
        System.out.println("AA1 " + str.hashCode());
        change(str);
        System.out.println(str);
        System.out.println("AA2 " + str.hashCode());

        int a = 10;
        changeInt(a);
        System.out.println(a);

        // 传递的是引用
        User user = new User();
        changeUser(user);
        System.out.println(user.name);
    }

}
