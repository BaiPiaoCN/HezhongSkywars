package com.hezhong.hezhongskywars.game;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Game {
    private final String mapName;
    private final World world;
    private GameStatus gameStatus;
    private List<Player> allPlayers;
    private List<Player> spectators;
    private final Map<Location, Chest> chests;
    public Game(String mapName, World world, Map <Location, Chest> chests) {
        this.mapName = mapName;
        this.world = world;
        gameStatus = GameStatus.RESETTING;
        allPlayers = new ArrayList<>();
        spectators = new ArrayList<>();
        this.chests = chests;
    }
    public void startGame() {

    }
    private void createCage(Player player) {

    }
    private void processDeath(Player killed, Player killer) {

    }
    private void normalEnd() {

    }
    private void refreshChest(Location location) {
        if (location.getWorld() != world) {
            return;
        }
        Block block = location.getBlock();
        if (!(block instanceof org.bukkit.block.Chest)) return;
        org.bukkit.block.Chest chest = (org.bukkit.block.Chest) block;

    }

}
