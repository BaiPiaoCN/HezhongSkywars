package com.hezhong.hezhongskywars.listeners;

import com.hezhong.hezhongskywars.HezhongSkywars;
import com.hezhong.hezhongskywars.config.ConfigValues;
import com.hezhong.hezhongskywars.manager.SwPlayerManager;
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
        SwPlayerManager.removePlayer(event.getPlayer());
    }
}
