package com.hezhong.hezhongskywars.player;

import com.hezhong.hezhongskywars.config.ConfigValues;
import com.hezhong.hezhongskywars.game.Game;
import com.hezhong.hezhongskywars.utils.type.DatabaseStatsData;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
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
    private DatabaseStatsData stats = null; // Null说明未加载
    private Game playingGame; // 玩家在什么游戏内，包括游玩和旁观。
    private Scoreboard scoreBoard;
    private Objective scoreBoardObjective;
    private boolean settingUpMap = false;
    private String setUpMapName = ""; // 是游戏地图名，不是MC服务器世界名。取世界名需要读配置！
    private SwPlayerSetupMapStatus setupMapStatus = new SwPlayerSetupMapStatus();
    private Location nextSpawnLocation;


    public SwPlayer(Player player) {
        this.player = player;
        stats = new DatabaseStatsData(this.player.getUniqueId(), this.player.getName());
        scoreBoard = Bukkit.getScoreboardManager().getNewScoreboard();
        scoreBoardObjective = scoreBoard.registerNewObjective("HSWScoreBoard", "dummy");
        scoreBoardObjective.setDisplayName(ConfigValues.serverName);
        scoreBoardObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
        player.setScoreboard(scoreBoard);
    }

    public boolean joinGame(Game g) {
        if (playingGame != null) {
            return false;
        }
        g.addPlayer(player);
        playingGame = g;
        return true;
    }

    public boolean hasKit(SwPlayerKit kit) {
        String kitName = kit.kitName;
        boolean has = stats.kits.stream().anyMatch(k -> k.kitName == kitName);
        return has;
    }
    public boolean hasKit(String kitName) {
        boolean has = stats.kits.stream().anyMatch(k -> k.kitName == kitName);
        return has;
    }

    @Getter
    @Setter
    public static class SwPlayerSetupMapStatus {
        private Block controllingBlock = null;
        private boolean listeningChat = false;

        private List<Vector> spawns = new ArrayList<>();
        private Map<Vector, String> chests = new LinkedHashMap<>(); // 确保遍历顺序

        public SwPlayerSetupMapStatus() {


        }
    }


    @Getter
    @Setter
    public static class SwPlayerKit {
        private final String kitName;
        public SwPlayerKit(String kitName) {
            this.kitName = kitName;
        }
    }

}
