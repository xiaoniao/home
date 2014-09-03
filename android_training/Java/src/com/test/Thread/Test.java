package com.test.Thread;

public class Test {

    public static void main(String[] args) {

        Thread t1 = new MyCommon();

        Thread t2 = new Thread(new MyDaemon());
        t2.setDaemon(true);

        t1.start();
        t2.start();

    }
}
