package com.hezhong.hezhongskywars.player;

import org.bukkit.entity.Player;

public class SwPlayer {
    private Player player;
    private SwPlayerStatistics stats;
    public SwPlayer(Player player) {
        this.player = player;
        this.stats = new SwPlayerStatistics(this);
    }
}

class SwPlayerStatistics {
    private SwPlayer p;
    public SwPlayerStatistics(SwPlayer p) {
        this.p = p;
    }
    // 临时记录统计数据，用于其他地方读取

}
