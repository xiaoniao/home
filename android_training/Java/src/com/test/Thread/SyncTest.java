package com.test.Thread;

public class SyncTest {

    public static void main(String[] args) {
        User u = new User("张三", 100);
        AccountThread t1 = new AccountThread("线程A", u, 20);
        AccountThread t2 = new AccountThread("线程B", u, -60);
        AccountThread t3 = new AccountThread("线程C", u, -80);
        AccountThread t4 = new AccountThread("线程D", u, -30);
        AccountThread t5 = new AccountThread("线程E", u, 32);
        AccountThread t6 = new AccountThread("线程F", u, 21);

        t1.start();
        t2.start();
        t3.start();
        t4.start();
        t5.start();
        t6.start();
    }

}
