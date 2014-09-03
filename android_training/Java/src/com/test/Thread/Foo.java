package com.test.Thread;

public class Foo {

    private int x = 100;

    public int getX() {
        return x;
    }

    public int fix(int y) {

        synchronized (this) {
            x = x - y;
        }

        return x;
    }

    public static synchronized int setName(String name) {

        return 1;
    }

    public static int setName2(String name) {

        synchronized (Foo.class) {

        }

        return 1;
    }

}
