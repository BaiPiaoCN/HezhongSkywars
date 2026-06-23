package com.hezhong.hezhongskywars.config;

import com.hezhong.hezhongskywars.game.GameEvent;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Map;

@Getter
public final class MapConfig {
    private final String originalWorld;
    private final String copyWorld;
    private final int minPlayersToAutostart;
    private final int maxPlayers;
    private final int countdown;
    private final boolean ok;
    private final List<Vector> spawns;
    private final Map<Vector, String> chests; // K 位置 V 类型
    private final List<GameEvent> gameEvents;
    public MapConfig(String originalWorld, String copyWorld, int minPlayersToAutostart, int maxPlayers, int countdown, boolean ok, List<Vector> spawns, Map<Vector, String> chests, List<GameEvent> gameEvents) {
        this.originalWorld = originalWorld;
        this.copyWorld = copyWorld;
        this.spawns = spawns;
        this.chests = chests;
        this.minPlayersToAutostart = minPlayersToAutostart;
        this.maxPlayers = maxPlayers;
        this.countdown = countdown;
        this.ok = ok;
        this.gameEvents = gameEvents;
    }
}
