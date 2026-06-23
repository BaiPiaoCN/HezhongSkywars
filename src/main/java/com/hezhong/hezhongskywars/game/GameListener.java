package com.hezhong.hezhongskywars.game;

import com.hezhong.hezhongskywars.manager.SwPlayerManager;
import com.hezhong.hezhongskywars.player.SwPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class GameListener implements Listener {
    @EventHandler
    public void onBreak(BlockBreakEvent e){
        Player p = e.getPlayer();
        SwPlayer sp = SwPlayerManager.getPlayer(p);
        if (sp == null) return;
    }
    @EventHandler
    public void onPlace(BlockPlaceEvent e){
        Player p = e.getPlayer();
        SwPlayer sp = SwPlayerManager.getPlayer(p);
        if (sp == null) return;
    }
    @EventHandler
    public void onDeath(EntityDeathEvent e){
        Entity ent = e.getEntity();
        if (ent instanceof Player) {
            Player p = (Player) ent;
            SwPlayer sp = SwPlayerManager.getPlayer(p);
            if (sp == null) return;
        }
    }
    @EventHandler
    public void onTp(PlayerTeleportEvent e) {
        Player p = e.getPlayer();
        SwPlayer sp = SwPlayerManager.getPlayer(p);
        if (sp == null) return;
    }
}
