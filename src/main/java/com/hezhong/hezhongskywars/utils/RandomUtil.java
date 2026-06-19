package com.hezhong.hezhongskywars.utils;

import java.util.concurrent.ThreadLocalRandom;

public class RandomUtil {
    public static double randomDouble(double start, double end) {
        /*
        生成一个start~end（含）的随机浮点数
         */
        return ThreadLocalRandom.current().nextDouble(start, end + 1e-12);
    }

    public static int randomInt(int start, int end) {
        /*
        生成一个start~end（含）的随机整数
         */
        return ThreadLocalRandom.current().nextInt(start, end + 1);
    }
}