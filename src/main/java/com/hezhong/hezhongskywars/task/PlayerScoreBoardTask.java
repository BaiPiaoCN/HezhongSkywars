package com.hezhong.hezhongskywars.task;

import com.hezhong.hezhongskywars.config.ConfigValues;
import com.hezhong.hezhongskywars.game.Game;
import com.hezhong.hezhongskywars.game.GameEvent;
import com.hezhong.hezhongskywars.game.GameStatus;
import com.hezhong.hezhongskywars.game.SwPlayingGamePlayer;
import com.hezhong.hezhongskywars.manager.SwPlayerManager;
import com.hezhong.hezhongskywars.player.SwPlayer;
import com.hezhong.hezhongskywars.utils.ColorT;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.*;

public class PlayerScoreBoardTask extends BukkitRunnable {

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            SwPlayer swPlayer = SwPlayerManager.getPlayer(player);
            if (swPlayer == null) continue;

            swPlayer.getScoreBoardUpdater().update();
        }
    }
}