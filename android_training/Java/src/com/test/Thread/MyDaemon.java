package com.test.Thread;

public class MyDaemon implements Runnable {

    @Override
    public void run() {

        for (long i = 0; i < 9999999999L; i++) {
            System.out.println("后台线程第" + i + "次运行");

            try {
                Thread.sleep(7);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

}
