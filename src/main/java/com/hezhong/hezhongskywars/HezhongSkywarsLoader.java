package com.hezhong.hezhongskywars;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class HezhongSkywarsLoader extends JavaPlugin {


    @Override
    public void onEnable() {
        // Plugin startup logic
        HezhongSkywars.INSTANCE.start(this);


    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        HezhongSkywars.INSTANCE.stop();
    }
}
