package com.hezhong.hezhongskywars.manager;

import com.hezhong.hezhongskywars.game.Game;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

public class GameManager {
    private final Plugin serverPlugin;
    private final Map<String, Game> games = new HashMap<>();
    public GameManager(Plugin serverPlugin) {
        this.serverPlugin = serverPlugin;
    }

    // Config没搞好，这个等会
    /*
    public void addGame(String mapName, World world) {
        games.put(mapName, new Game(mapName, world));
    }

     */
}
