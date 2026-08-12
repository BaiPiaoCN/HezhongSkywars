package com.hezhong.hezhongskywars.utils;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class ColorT {
    // Color Translator
    // 名称究极缩写
    public static String t(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }
    public static List<String> t(List<String> str) {
        List<String> newStr = new ArrayList<>();
        for (String s : str) newStr.add(t(s));
        return newStr;
    }
}
