package com.test.interfa;

public class InterfaceTest implements IDownload, IDecode {

    /** 如果俩接口中有同一个方法 **/

    public static void main(String[] args) {

        new InterfaceTest().hello();

    }

    @Override
    public void hello() {
        System.out.println("Hello");

    }

}
