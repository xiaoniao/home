package com.test.Thread;

public class ThreadA {

    public static void main(String[] args) {
        ThreadB thread = new ThreadB(); // 没有用多态
        thread.start();

        System.out.println("thread对象start...");
        
        // 同步代码块
        synchronized (thread) {

            try {

                System.out.println("等待对象thread完成计算。。。");

                thread.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("thread对象计算的总和是：" + thread.total);
        }
    }
}
