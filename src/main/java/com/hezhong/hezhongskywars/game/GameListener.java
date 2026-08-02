package com.hezhong.hezhongskywars.game;

import com.hezhong.hezhongskywars.HezhongSkywars;
import com.hezhong.hezhongskywars.events.HSWGameStartEvent;
import com.hezhong.hezhongskywars.manager.SwPlayerManager;
import com.hezhong.hezhongskywars.player.SwPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

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
    public void onChangeWorld(PlayerChangedWorldEvent e) {
        Player p = e.getPlayer();
        SwPlayer sp = SwPlayerManager.getPlayer(p);
        if (sp == null) return;
        if (sp.getPlayingGame() != null) {
            // 换世界了，就是退游戏了
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
                if (playingGame.getGameStatus() == GameStatus.PLAYING) {
                    p.damage(10000);
                } else {
                    p.teleport(playingGame.getWorld().getSpawnLocation());
                }
            }
        }
    }
    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (e.getCause() == EntityDamageEvent.DamageCause.SUICIDE ||  e.getCause() == EntityDamageEvent.DamageCause.CUSTOM) {
            // 六六六 我说为什么Kill杀不了人了
            // 必须加这两个原因的豁免，避免玩家不死
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
                    // 开局短暂免伤
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
                    // 1 不允许死去的玩家攻击
                    SwPlayingGamePlayer swpgpDamager = playingGame.getPlayingPlayer(damagerPlayer.getUniqueId());
                    if (swpgpDamager.getStatus() != SwPlayingGamePlayer.PlayerStatus.ALIVE) {
                        e.setCancelled(true);
                        return; // 不继续执行下面的代码，避免统计错误
                    } else {
                        // 保险起见，也是为了控制流清晰，加else
                        // 没人改掉吧？

                        // 2 保存伤害数据
                        SwPlayingGamePlayer swpgpDamaged = playingGame.getPlayingPlayer(p.getUniqueId());
                        swpgpDamaged.totalDamage += e.getDamage();
                        swpgpDamaged.getDamageByAttack().put(damagerPlayer.getUniqueId(), e.getDamage());
                    }
                }
            }
        }
    }
    @EventHandler
    public void onDropItem(PlayerDropItemEvent e) {
        Player p = e.getPlayer();
        SwPlayer sp = SwPlayerManager.getPlayer(p);
        if (sp == null) return;
        Game playingGame = sp.getPlayingGame();
        if (playingGame != null) {
            SwPlayingGamePlayer swpgp = playingGame.getPlayingPlayer(p.getUniqueId());
            if (playingGame.getGameStatus() != GameStatus.PLAYING || playingGame.getRunnedTime() <= 1 || swpgp.getStatus() != SwPlayingGamePlayer.PlayerStatus.ALIVE) {
                // 禁止丢去物品
                e.setCancelled(true);
            }
        } else {
            // 不在游戏中时，禁止丢去物品
            e.setCancelled(true);
        }
    }

    // Join/Quit处理集中在#com.hezhong.hezhongskywars.listeners.JoinQuitListener
}
