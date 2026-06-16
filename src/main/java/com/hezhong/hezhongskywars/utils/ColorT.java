package com.hezhong.hezhongskywars.utils;

import org.bukkit.ChatColor;

public class ColorT {
    // Color Translator
    public static String t(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }
}
