package com.hezhong.hezhongskywars.utils;

public class MathUtil {
    public static boolean isNumeric(String str) {
        return str != null && str.matches("-?\\d+");
    }
}
