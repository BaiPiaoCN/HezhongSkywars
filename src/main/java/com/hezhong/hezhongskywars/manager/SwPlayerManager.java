package com.hezhong.hezhongskywars.manager;

import com.hezhong.hezhongskywars.player.SwPlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SwPlayerManager {
    // 管理所有玩家
    private static final Map<UUID, SwPlayer> players =  new HashMap<>();
    public static SwPlayer getPlayer(UUID uuid) {
        return players.get(uuid);
    }
    public static SwPlayer getPlayer(Player player) {
        return players.get(player.getUniqueId());
    }
    public static void addPlayer(Player player) {
        players.put(player.getUniqueId(), new SwPlayer(player));
    }
    public static void removePlayer(Player player) {
        players.remove(player.getUniqueId());
    }
}
