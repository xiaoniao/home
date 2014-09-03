package com.test.Thread;

public class ThreadB extends Thread {

    int total;

    @Override
    public void run() {
        synchronized (this) {

            for (int i = 0; i < 101; i++) {
                total += i;
            }
            System.out.println("完成计算");
            // （完成计算了）唤醒在此对象监视器上等待的单个线程，在本例中线程A被唤醒
            notify();

            // 只要调用notify()并不意味着这时该锁变得可用。
        }
    }

    public int getTotal() {

        return total;
    }
}
