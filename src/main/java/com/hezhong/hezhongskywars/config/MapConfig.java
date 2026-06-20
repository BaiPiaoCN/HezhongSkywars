package com.hezhong.hezhongskywars.config;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Map;

@Getter
public final class MapConfig {
    private final String originalWorld;
    private final String copyWorld;
    private final List<Vector> spawns;
    private final Map<Vector, String> chests; // K 位置 V 类型
    public MapConfig(String originalWorld, String copyWorld, List<Vector> spawns, Map<Vector, String> chests) {
        this.originalWorld = originalWorld;
        this.copyWorld = copyWorld;
        this.spawns = spawns;
        this.chests = chests;
    }
}
