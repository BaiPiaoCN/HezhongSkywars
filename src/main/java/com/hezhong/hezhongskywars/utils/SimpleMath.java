package com.hezhong.hezhongskywars.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

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

    public static String formatScale(double value, int scale) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(scale, RoundingMode.HALF_UP);
        return bd.toString();
    }
}