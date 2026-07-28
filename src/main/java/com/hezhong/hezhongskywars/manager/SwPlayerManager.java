package com.hezhong.hezhongskywars.manager;

import com.hezhong.hezhongskywars.player.SwPlayer;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SwPlayerManager {
    // 管理所有玩家
    @Getter
    private static final Map<UUID, SwPlayer> players =  new HashMap<>();
    public static SwPlayer getPlayer(UUID uuid) {
        return players.getOrDefault(uuid, null);
    }
    public static SwPlayer getPlayer(Player player) {
        if (player == null) return null;
        return players.getOrDefault(player.getUniqueId(), null);
    }
    public static void addPlayer(Player player) {
        players.put(player.getUniqueId(), new SwPlayer(player));
    }
    public static void removePlayer(Player player) {
        players.remove(player.getUniqueId());
    }
}
