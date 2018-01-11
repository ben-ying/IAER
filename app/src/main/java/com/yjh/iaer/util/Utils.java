package com.yjh.iaer.util;


public class Utils {
    public static String capWords(String str) {
        if (str != null) {
            char[] ch = str.toCharArray();
            if (ch[0] >= 'a' && ch[0] <= 'z') {
                ch[0] = (char) (ch[0] - 32);
            }
            return new String(ch);
        } else {
            return "";
        }
    }
}

