package com.hezhong.hezhongskywars;

import com.hezhong.hezhongskywars.utils.ColorT;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class HezhongSkywarsLoader extends JavaPlugin {


    @Override
    public void onEnable() {
        // 反正版检测，在插件启动前
        boolean isOnlineMode = Bukkit.getOnlineMode();
        if (isOnlineMode) {
            Bukkit.getConsoleSender().sendMessage(ColorT.t("&c&l你正在&e&l正版&c&l服务器中启动Hezhong Skywars"));
            Bukkit.getConsoleSender().sendMessage(ColorT.t("&c&l你的服务器可能因此&e&l不受支持"));
        }

        // Plugin startup logic
        HezhongSkywars.INSTANCE.start(this);


    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        HezhongSkywars.INSTANCE.stop();
    }
}
