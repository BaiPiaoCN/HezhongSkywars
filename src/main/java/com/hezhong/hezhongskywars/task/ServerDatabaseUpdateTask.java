package com.hezhong.hezhongskywars.task;

import com.hezhong.hezhongskywars.HezhongSkywars;
import org.bukkit.Warning;
import org.bukkit.scheduler.BukkitRunnable;

public class ServerDatabaseUpdateTask extends BukkitRunnable {

    @Override
    public void run() {
        // 每20 * 60 = 1200ticks运行一次

        HezhongSkywars.INSTANCE.getDatabase().allDatas = HezhongSkywars.INSTANCE.getDatabase().getAllDatabaseStats();
    }
}
