package com.test.Thread;

public class MyDaemon implements Runnable {

    @Override
    public void run() {

        for (long i = 0; i < 9999999999L; i++) {
            System.out.println("��̨�̵߳�" + i + "������");

            try {
                Thread.sleep(7);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

}
