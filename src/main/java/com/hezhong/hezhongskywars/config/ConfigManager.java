package com.hezhong.hezhongskywars.config;

import com.hezhong.hezhongskywars.utils.ColorT;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;

public class ConfigManager {
    private Plugin serverPlugin;
    // 共4个配置
    // config.yml 主配置
    // chests.yml 箱子配置
    // maps.yml 地图配置
    // ranks.yml 等级配置

    private YamlConfiguration mainConfig;
    private YamlConfiguration chestsConfig;
    private YamlConfiguration mapsConfig;
    private YamlConfiguration ranksConfig;
    public ConfigManager(Plugin serverPlugin) {
        this.serverPlugin = serverPlugin;
    }
    public void loadConfig() {
        // 从jar释放文件
        File mainConfigFile = new File(serverPlugin.getDataFolder(), "config.yml");
        if (!mainConfigFile.exists()) {
            serverPlugin.saveResource("config.yml", false); // 不覆盖
        }
        File chestsConfigFile = new File(serverPlugin.getDataFolder(), "chests.yml");
        if (!chestsConfigFile.exists()) {
            serverPlugin.saveResource("chests.yml", false);
        }
        File mapsConfigFile = new File(serverPlugin.getDataFolder(), "maps.yml");
        if (!mapsConfigFile.exists()) {
            serverPlugin.saveResource("maps.yml", false);
        }
        File ranksConfigFile = new File(serverPlugin.getDataFolder(), "ranks.yml");
        if (!ranksConfigFile.exists()) {
            serverPlugin.saveResource("ranks.yml", false);
        }

        // 载入到Yaml配置对象中
        mainConfig = YamlConfiguration.loadConfiguration(mainConfigFile);
        chestsConfig = YamlConfiguration.loadConfiguration(chestsConfigFile);
        mapsConfig = YamlConfiguration.loadConfiguration(mapsConfigFile);
        ranksConfig = YamlConfiguration.loadConfiguration(ranksConfigFile);

        resolveConfigValues();
    }

    private void resolveConfigValues() {
        // 负责真正读取配置信息
        ConfigValues.serverIp = ColorT.t(mainConfig.getString("basicInfo.serverIp"));
    }
}
