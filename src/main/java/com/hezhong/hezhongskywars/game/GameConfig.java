package com.hezhong.hezhongskywars.game;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameConfig {
    private final String mapName;
    private final List<Location> spawnLocations;
    public GameConfig(String mapName) {
        this.mapName = mapName;
        this.spawnLocations = new ArrayList<>();
    }

}
