package com.hezhong.hezhongskywars.multiworld;

import com.hezhong.hezhongskywars.config.ConfigValues;
import com.hezhong.hezhongskywars.manager.SwPlayerManager;
import com.hezhong.hezhongskywars.player.SwPlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashSet;
import java.util.Set;

public class IndependentWorldManager implements Listener {
    @EventHandler
    public void handleAsyncChat(AsyncPlayerChatEvent event) {
        if (!ConfigValues.multiWorldIndependentChat) return;
        Player p = event.getPlayer();
        World current = p.getWorld();

        Set<Player> worldPlayers = new HashSet<>(current.getPlayers());

        try {

            event.getRecipients().retainAll(worldPlayers);
        } catch (UnsupportedOperationException e) {
            // JavaDoc说“不保证可变”，兜底
            event.getRecipients().clear();
            event.getRecipients().addAll(worldPlayers);
        }
    }

    @EventHandler
    public void handleChangeWorld(PlayerChangedWorldEvent event) {
        Player p = event.getPlayer();
        SwPlayer sp = SwPlayerManager.getPlayer(p);
        if (sp != null) {
            processTab(sp, p.getWorld());
        }

    }

    public void handlePostJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        SwPlayer sp = SwPlayerManager.getPlayer(p);
        if (sp != null) {
            processTab(sp, p.getWorld());
        }
    }

    public void processTab(SwPlayer sp, World current) {
        if (!ConfigValues.multiWorldIndependentTab) return;
        for (SwPlayer hideSp : SwPlayerManager.getPlayers().values()) {
            // 双向隐藏
            // 不走SwPlayer的hidePlayer。直接走Bukkit，不记录，强行隐藏。
            if (hideSp.getPlayer().getWorld() != current) {
                hideSp.getPlayer().hidePlayer(sp.getPlayer());
                sp.getPlayer().hidePlayer(hideSp.getPlayer());
            } else {
                if (!hideSp.getHideList().contains(sp.getPlayer())) {
                    hideSp.getPlayer().showPlayer(sp.getPlayer());
                }
                if (!sp.getHideList().contains(hideSp.getPlayer())) {
                    sp.getPlayer().showPlayer(hideSp.getPlayer());
                }
            }
        }
    }
}
