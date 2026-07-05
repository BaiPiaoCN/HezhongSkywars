package com.hezhong.hezhongskywars.game;

import com.hezhong.hezhongskywars.HezhongSkywars;
import com.hezhong.hezhongskywars.events.HSWGameStartEvent;
import com.hezhong.hezhongskywars.manager.GameManager;
import com.hezhong.hezhongskywars.manager.SwPlayerManager;
import com.hezhong.hezhongskywars.player.SwPlayer;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class GameListener implements Listener {
    @EventHandler
    public void onStart(HSWGameStartEvent e) {
        String mapName = e.getMapName();
        Game game = HezhongSkywars.INSTANCE.getGameManager().getGames().get(mapName);
        if (game != null) {
            game.startGame();
        }
    }
    @EventHandler
    public void onBreak(BlockBreakEvent e){
        Player p = e.getPlayer();
        SwPlayer sp = SwPlayerManager.getPlayer(p);
        if (sp == null) return;
        Game playingGame = sp.getPlayingGame();
        if (playingGame != null) {
            SwPlayingGamePlayer swpgp = playingGame.getPlayingPlayer(p.getUniqueId());
            if (swpgp.getStatus() != SwPlayingGamePlayer.PlayerStatus.ALIVE || playingGame.getGameStatus() != GameStatus.PLAYING) {
                e.setCancelled(true);
            }
        } else {
            if (!p.hasPermission("hsw.modifyMap")) {
                e.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        Player p = e.getPlayer();
        SwPlayer sp = SwPlayerManager.getPlayer(p);
        if (sp == null) return;
        Game playingGame = sp.getPlayingGame();
        if (playingGame != null) {
            SwPlayingGamePlayer swpgp = playingGame.getPlayingPlayer(p.getUniqueId());
            if (swpgp.getStatus() != SwPlayingGamePlayer.PlayerStatus.ALIVE || playingGame.getGameStatus() != GameStatus.PLAYING) {
                e.setCancelled(true);
            }
        } else {
            if (!p.hasPermission("hsw.modifyMap")) {
                e.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void onDeath(PlayerDeathEvent e){
        Player p = e.getEntity();
        if (p != null) {
            SwPlayer sp = SwPlayerManager.getPlayer(p);
            if (sp == null) return;
            Game playingGame = sp.getPlayingGame();
            if (playingGame == null) return;
            String msg = playingGame.processDeath(p, p.getKiller(), false);
            e.setDeathMessage(msg);

        }
    }
    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        Player p = e.getPlayer();
        if (p != null) {
            SwPlayer sp = SwPlayerManager.getPlayer(p);
            if (sp == null) return;
            Game playingGame = sp.getPlayingGame();
            if (playingGame == null) {
                e.setRespawnLocation(HezhongSkywars.INSTANCE.getLobbySpawnLocation());
            } else {
                e.setRespawnLocation(sp.getNextSpawnLocation());
            }

        }
    }
    @EventHandler
    public void onTp(PlayerChangedWorldEvent e) {
        Player p = e.getPlayer();
        SwPlayer sp = SwPlayerManager.getPlayer(p);
        if (sp == null) return;
        if (sp.getPlayingGame() != null) {
            Game playingGame = sp.getPlayingGame();
            playingGame.processDeath(p, null, true);
        }
    }
    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (e.getTo().getY() < 0) {
            Player p = e.getPlayer();
            SwPlayer sp = SwPlayerManager.getPlayer(p);
            if (sp == null) return;
            Game playingGame = sp.getPlayingGame();
            if (playingGame != null) {
                SwPlayingGamePlayer swpgp = playingGame.getPlayingPlayer(p.getUniqueId());
                if (playingGame.getGameStatus() == GameStatus.PLAYING) { // 存活则击杀
                    p.setHealth(0d);
                }
            }
        }
    }
    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (e.getCause() == EntityDamageEvent.DamageCause.SUICIDE ||  e.getCause() == EntityDamageEvent.DamageCause.CUSTOM) {
            // 六六六Kill为什么无法击杀
            return;
        }
        Entity ent = e.getEntity();
        if (ent instanceof Player) {
            Player p = (Player) ent;
            SwPlayer sp = SwPlayerManager.getPlayer(p);
            if (sp == null) return;
            Game playingGame = sp.getPlayingGame();
            if (playingGame != null) {
                SwPlayingGamePlayer swpgp = playingGame.getPlayingPlayer(p.getUniqueId());
                if (playingGame.getGameStatus() != GameStatus.PLAYING || playingGame.getRunnedTime() <= 5 || swpgp.getStatus() != SwPlayingGamePlayer.PlayerStatus.ALIVE) {
                    e.setCancelled(true);
                }
            } else {
                e.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void onDamageByOther(EntityDamageByEntityEvent e) {
        Entity ent = e.getEntity();
        if (ent instanceof Player) {
            Player p = (Player) ent;
            Entity damager = e.getDamager();
            if (damager instanceof Player) {
                Player damagerPlayer = (Player) damager;
                SwPlayer sp = SwPlayerManager.getPlayer(p);
                if (sp == null) return;
                Game playingGame = sp.getPlayingGame();
                if (playingGame != null) {
                    SwPlayingGamePlayer swpgp = playingGame.getPlayingPlayer(damagerPlayer.getUniqueId());
                    if (swpgp.getStatus() != SwPlayingGamePlayer.PlayerStatus.ALIVE) e.setCancelled(true);
                }
            }
        }
    }

    // Quit处理在#com.hezhong.hezhongskywars.listeners.JoinQuitListener
}
