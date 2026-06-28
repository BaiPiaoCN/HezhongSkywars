package com.hezhong.hezhongskywars.player;

import com.hezhong.hezhongskywars.config.ConfigValues;
import com.hezhong.hezhongskywars.game.Game;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;

import java.util.*;

@Getter
@Setter
public class SwPlayer {
    private final Player player;
    private SwPlayerStatistics stats;
    private Game playingGame; // 玩家在什么游戏内，包括游玩和旁观。
    private Scoreboard scoreBoard;
    private Objective scoreBoardObjective;
    private boolean settingUpMap = false;
    private String setUpMapName = ""; // 是游戏地图名，不是MC服务器世界名。取世界名需要读配置！
    private SwPlayerSetupMapStatus setupMapStatus = new SwPlayerSetupMapStatus();


    public SwPlayer(Player player) {
        this.player = player;
        stats = new SwPlayerStatistics();
        scoreBoard = Bukkit.getScoreboardManager().getNewScoreboard();
        scoreBoardObjective = scoreBoard.registerNewObjective("HSWScoreBoard", "dummy");
        scoreBoardObjective.setDisplayName(ConfigValues.serverName);
        scoreBoardObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }
    // TODO: 完成统计和持久化后，记得更新这里
    // 包括入服后缓存数据，退服时持久化保存数据

    public void refreshScoreBoard() {
        if (playingGame != null) {

        }
    }

    public boolean joinGame(Game g) {
        if (playingGame != null) {
            return false;
        }
        g.addPlayer(player);
        playingGame = g;
        return true;
    }

    @Getter
    @Setter
    public class SwPlayerStatistics {
        public SwPlayerStatistics() {

        }
        // 临时记录统计数据，用于其他地方读取
    }

    @Getter
    @Setter
    public class SwPlayerSetupMapStatus {
        private Block controllingBlock = null;
        private boolean listeningChat = false;

        private List<Vector> spawns = new ArrayList<>();
        private Map<Vector, String> chests = new LinkedHashMap<>(); // 确保遍历顺序

        public SwPlayerSetupMapStatus() {


        }
    }

}
