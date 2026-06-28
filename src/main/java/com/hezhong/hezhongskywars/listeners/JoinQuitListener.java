package com.hezhong.hezhongskywars.listeners;

import com.hezhong.hezhongskywars.HezhongSkywars;
import com.hezhong.hezhongskywars.config.ConfigValues;
import com.hezhong.hezhongskywars.game.Game;
import com.hezhong.hezhongskywars.manager.SwPlayerManager;
import com.hezhong.hezhongskywars.player.SwPlayer;
import com.hezhong.hezhongskywars.task.PlayerScoreBoardTask;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinQuitListener implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        World lobbyWorld = Bukkit.getWorld(ConfigValues.lobbyWorld);
        if (lobbyWorld != null) {
            player.teleport(lobbyWorld.getSpawnLocation());
        } else {
            HezhongSkywars.INSTANCE.getLogger().warning("HSW LobbyWorld is Null?");
        }
        SwPlayerManager.addPlayer(player);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event){
        // 先处理游戏退出
        SwPlayer sp = SwPlayerManager.getPlayer(event.getPlayer());
        PlayerScoreBoardTask.cleanPlayer(event.getPlayer());
        if (sp != null && sp.getPlayingGame() != null) {
            Game playing = sp.getPlayingGame();
            playing.processDeath(event.getPlayer(), null, true);
        }
        SwPlayerManager.removePlayer(event.getPlayer());
    }
}
