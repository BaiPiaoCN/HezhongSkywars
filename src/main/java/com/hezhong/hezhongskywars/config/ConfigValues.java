package com.hezhong.hezhongskywars.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigValues {
    // 储存配置数值
    // 纯静态类
    public static String serverIp;
    public static String serverName;
    public static String lobbyWorld;
    public static Map<String, ChestConfig> chestConfigs = new HashMap<>(); // K:V type:config
    public static Map<String, MapConfig> mapConfigs = new HashMap<>(); // K:V mapName:config
}
