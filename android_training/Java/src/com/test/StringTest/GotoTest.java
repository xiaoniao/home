package com.test.StringTest;

public class GotoTest {

    public static void main(String[] args) {

        int free = 200;

        saveDefault: save(free);

    }

    static void saveDefault() {

        System.out.println("saveDefault");

    }

    static void save(int free) {

        System.out.println("save");

    }
}
