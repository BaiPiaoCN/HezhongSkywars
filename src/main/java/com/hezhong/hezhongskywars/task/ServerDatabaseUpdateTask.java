package com.hezhong.hezhongskywars.task;

import com.hezhong.hezhongskywars.HezhongSkywars;
import com.hezhong.hezhongskywars.manager.SwPlayerManager;
import com.hezhong.hezhongskywars.player.SwPlayer;
import com.hezhong.hezhongskywars.utils.type.DatabaseStatsData;
import org.bukkit.Bukkit;
import org.bukkit.Warning;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import javax.xml.crypto.Data;
import java.util.List;
import java.util.UUID;

public class ServerDatabaseUpdateTask extends BukkitRunnable {

    @Override
    public void run() {
        // 每20 * 60 = 1200ticks运行一次
        HezhongSkywars.INSTANCE.getDatabase().refreshAllDatasCache();


    }
}
