package com.hezhong.hezhongskywars.utils;

public class SimpleMath {
    // 简单的数学类

    public static int floor(double value) {
        return Double.valueOf(Math.floor(value)).intValue();
    }

    public static int ceil(double value) {
        return Double.valueOf(Math.ceil(value)).intValue();
    }

    public static int round(double value) {
        return Long.valueOf(Math.round(value)).intValue();
    }
}