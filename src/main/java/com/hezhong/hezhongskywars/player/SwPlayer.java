package com.hezhong.hezhongskywars.player;

import com.hezhong.hezhongskywars.game.Game;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

@Getter
@Setter
public class SwPlayer {
    private final Player player;
    private SwPlayerStatistics stats;
    private Game playingGame;
    private Scoreboard scoreBoard;
    public SwPlayer(Player player) {
        this.player = player;
        this.stats = new SwPlayerStatistics(this);
        scoreBoard = Bukkit.getScoreboardManager().getNewScoreboard();
    }
    // TODO: 完成统计和持久化后，记得更新这里
    // 包括入服后缓存数据，退服时持久化保存数据
}

class SwPlayerStatistics {
    private SwPlayer p;
    public SwPlayerStatistics(SwPlayer p) {
        this.p = p;
    }
    // 临时记录统计数据，用于其他地方读取

}
