package com.test.StringTest;

import java.util.Locale;

public class StringHash {

    public static void main(String[] args) {
        StringBuilder sb = new StringBuilder();

        sb.append("id").append("").append("year").append("").append("title").append("").append("desc").append("")
                .append("vid").append("").append("topic").append("").append("speakers").append("")
                .append("thumbnailUrl").append("");

        computeWeakHash(sb.toString());

    }

    public static void computeWeakHash(String string) {
        System.out.println("string.hashCode()  " + string.hashCode());
        System.out.println("" + String.format(Locale.US, "%08x%08x", string.hashCode(), string.length()));
    }
}
