package com.test.interfa;

public class InterfaceTest implements IDownload, IDecode {

    /** ������ӿ�����ͬһ������ **/

    public static void main(String[] args) {

        new InterfaceTest().hello();

    }

    @Override
    public void hello() {
        System.out.println("Hello");

    }

}