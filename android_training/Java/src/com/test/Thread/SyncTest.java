package com.test.Thread;

public class SyncTest {

    public static void main(String[] args) {
        User u = new User("����", 100);
        AccountThread t1 = new AccountThread("�߳�A", u, 20);
        AccountThread t2 = new AccountThread("�߳�B", u, -60);
        AccountThread t3 = new AccountThread("�߳�C", u, -80);
        AccountThread t4 = new AccountThread("�߳�D", u, -30);
        AccountThread t5 = new AccountThread("�߳�E", u, 32);
        AccountThread t6 = new AccountThread("�߳�F", u, 21);

        t1.start();
        t2.start();
        t3.start();
        t4.start();
        t5.start();
        t6.start();
    }

}
