package com.test.Thread;

public class AccountThread extends Thread {

    private User u;
    private int  y = 0;

    AccountThread(String name, User u, int y) {
        super(name);
        this.u = u;
        this.y = y;
    }

    public void run() {
        u.oper(y);
    }
}
