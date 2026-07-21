package com.hezhong.hezhongskywars.listeners;

import com.hezhong.hezhongskywars.HezhongSkywars;
import com.hezhong.hezhongskywars.config.ConfigValues;
import com.hezhong.hezhongskywars.game.Game;
import com.hezhong.hezhongskywars.manager.SwPlayerManager;
import com.hezhong.hezhongskywars.player.SwPlayer;
import com.hezhong.hezhongskywars.task.PlayerScoreBoardTask;
import com.hezhong.hezhongskywars.utils.ColorT;
import com.hezhong.hezhongskywars.utils.type.DatabaseStatsData;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class JoinQuitListener implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        if (HezhongSkywars.INSTANCE.getDatabase().writingPlayers.contains(player.getUniqueId())) {
            player.kickPlayer(ColorT.t("&b&lHSW &a你的数据还未刷新，请等待一下再进入！"));
        }
        World lobbyWorld = Bukkit.getWorld(ConfigValues.lobbyWorld);
        if (lobbyWorld != null) {
            player.teleport(lobbyWorld.getSpawnLocation());
        } else {
            HezhongSkywars.INSTANCE.getLogger().warning("HSW LobbyWorld is Null?");
        }

        SwPlayerManager.addPlayer(player);
        SwPlayer p = SwPlayerManager.getPlayer(player);
        p.setNextSpawnLocation(lobbyWorld.getSpawnLocation());
        Bukkit.getScheduler().runTaskAsynchronously(HezhongSkywars.INSTANCE.getPlugin(), () -> {
            p.setStats(HezhongSkywars.INSTANCE.getDatabase().getDatabaseStats(player.getUniqueId()));
        });
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
        // 写数据（持有sp对象）
        DatabaseStatsData data = sp.getStats();
        UUID uuid = event.getPlayer().getUniqueId();
        Bukkit.getScheduler().runTaskAsynchronously(HezhongSkywars.INSTANCE.getPlugin(), () -> {
            HezhongSkywars.INSTANCE.getDatabase().writingPlayers.add(uuid);
            HezhongSkywars.INSTANCE.getDatabase().setDatabaseStats(uuid, data);
            HezhongSkywars.INSTANCE.getDatabase().writingPlayers.remove(uuid);
        });
    }
}
