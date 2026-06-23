package com.hezhong.hezhongskywars.game;

import com.connorlinfoot.titleapi.TitleAPI;
import com.cryptomorin.xseries.XSound;
import com.hezhong.hezhongskywars.HezhongSkywars;
import com.hezhong.hezhongskywars.HezhongSkywarsLoader;
import com.hezhong.hezhongskywars.config.ConfigValues;
import com.hezhong.hezhongskywars.events.HSWGameStartEvent;
import com.hezhong.hezhongskywars.manager.GameManager;
import com.hezhong.hezhongskywars.manager.SwPlayerManager;
import com.hezhong.hezhongskywars.player.SwPlayer;
import com.hezhong.hezhongskywars.utils.ColorT;
import com.hezhong.hezhongskywars.utils.type.ChestItem;
import com.hezhong.hezhongskywars.utils.type.Pair;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

@Getter
public class Game {
    // 运行的游戏实例。
    // 其实大部分配置还是现场从配置读
    private final String mapName;
    private final World world;
    private final int maxPlayers;
    private GameStatus gameStatus;
    private List<Player> allPlayers;
    private List<Player> allAlivePlayers;
    private List<Player> spectators;
    private int runnedTime = 0;
    private final Map<Location, Chest> chests;
    private final List<Pair<Location, Player>> spawns; // y 占用的玩家
    private final List<GameEvent> events;
    private BukkitTask countdownTask;
    private BukkitTask eventRunnerTask;

    private int countdown = 30;
    public Game(String mapName, World world, Map <Location, Chest> chests, List<Location> spawns, List<GameEvent> events) {
        this.mapName = mapName;
        this.world = world;
        gameStatus = GameStatus.WAITING;
        allPlayers = new ArrayList<>();
        spectators = new ArrayList<>();
        allAlivePlayers = new ArrayList<>();
        this.chests = chests;
        this.spawns = new ArrayList<>();
        this.events = events;
        for (Location location : spawns) {
            Location newLocation = location.clone();
            newLocation.setX(location.getBlockX() + 0.5);
            newLocation.setY(location.getBlockY());
            newLocation.setZ(location.getBlockZ() + 0.5);
            this.spawns.add(new Pair<>(newLocation, null));
        }
        // maxPlayers
        maxPlayers = Math.min(spawns.size(), ConfigValues.mapConfigs.get(mapName).getMaxPlayers());
        countdown = ConfigValues.mapConfigs.get(mapName).getCountdown();

        // 对events，按照时间排序
        Collections.sort(events, Comparator.comparingInt(GameEvent::getTime));

    }
    public boolean addPlayer(Player player) {
        // 一定记得设置SwPlayer状态！应该只有#SwPlayer.joinGame可以调用这玩意
        if (allPlayers.size() >= maxPlayers) {
            return false;
        }
        allPlayers.add(player);
        allAlivePlayers.add(player);
        // 将玩家传送
        Location selectedLocation = selectSpawnPoint(player); // 不可能会null的
        assert selectedLocation != null : "?"; // 我操，我就不信这tm能是null了！
        createCage(selectedLocation);
        player.teleport(selectedLocation);

        return true;
    }
    public void startGame() {
        // 接收到event，开始游戏！
        for (Pair<Location, Player> spawn : spawns) {
            Location spawnLocation = spawn.getX();
            removeCage(spawnLocation);
        }
        gameStatus = GameStatus.PLAYING; // 设置游戏状态
        sendMessage("&c&l战斗！");
        for (Player player : allPlayers) {
            TitleAPI.sendTitle(player, 0, 60, 20, ColorT.t("&c&l战斗！"));
        }


    }
    private void createCage(Location spawnPoint) {
        World world = spawnPoint.getWorld();
        int baseX = spawnPoint.getBlockX();
        int baseY = spawnPoint.getBlockY() - 1;
        int baseZ = spawnPoint.getBlockZ();

        int height = 3;

        for (int y = 0; y <= height + 1; y++) {
            int currentY = baseY + y;
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    boolean isEdge = (Math.abs(x) == 1 || Math.abs(z) == 1);
                    boolean isTopOrBottom = (y == 0 || y == height + 1);

                    if (isEdge || isTopOrBottom) {
                        Location blockLoc = new Location(world, baseX + x, currentY, baseZ + z);
                        blockLoc.getBlock().setType(Material.GLASS);
                    }
                }
            }
        }
    }
    private void removeCage(Location spawnPoint) {
        World world = spawnPoint.getWorld();
        int baseX = spawnPoint.getBlockX();
        int baseY = spawnPoint.getBlockY() - 1;
        int baseZ = spawnPoint.getBlockZ();

        int height = 3;

        for (int y = 0; y <= height + 1; y++) {
            int currentY = baseY + y;
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    boolean isEdge = (Math.abs(x) == 1 || Math.abs(z) == 1);
                    boolean isTopOrBottom = (y == 0 || y == height + 1);

                    if (isEdge || isTopOrBottom) {
                        Location blockLoc = new Location(world, baseX + x, currentY, baseZ + z);
                        if (blockLoc.getBlock().getType() == Material.GLASS) {
                            blockLoc.getBlock().setType(Material.AIR);
                        }
                    }
                }
            }
        }
    }
    private void processDeath(Player killed, Player killer, boolean exit) {
        // killer为null代表玩家不是被杀的
        // disconnect为true代表玩家是自己退的
    }
    private void normalEnd() {
        gameStatus = GameStatus.STOPPED;
        List<Pair<Integer, Player>> mostKilled =  new ArrayList<>();
        // 按击杀数排序
        for (Player player : allPlayers) {
            SwPlayer sp = SwPlayerManager.getPlayer(player);
            mostKilled.add(new Pair<>(sp.getGameStatus().getKilled(), player));
        }
        // 从大到小排
        Collections.sort(mostKilled, Comparator.comparingInt(Pair::getX));
        Collections.reverse(mostKilled);
        // 展示击杀Top3
        int i = 1;

        sendMessage("&7====================&a&l统计&7====================");

        for (Pair<Integer, Player> pair : mostKilled) {
            if (i > 3) break;
            sendMessage(String.format("&cTop %s &7%s &a击杀 %s", i, pair.getY().getName(), pair.getX()));
            i++;
        }
        for (Player winner : allAlivePlayers) {
            // 还活着就是赢了
            TitleAPI.sendTitle(winner, 0, 90, 10, ColorT.t("&e&lVICTORY"));
        }

        BukkitRunnable fireworkTask = new BukkitRunnable() {
            private int count = 0;  // 计数器

            @Override
            public void run() {
                playSound(XSound.ENTITY_FIREWORK_ROCKET_SHOOT);
                if (++count >= 5) {
                    this.cancel();
                }
            }
        };
        fireworkTask.runTaskTimer(HezhongSkywars.INSTANCE.getPlugin(), 0, 20);

        Bukkit.getScheduler().runTaskLater(HezhongSkywars.INSTANCE.getPlugin(), () -> {
            for (Player player : allAlivePlayers) {
                player.teleport(Bukkit.getWorld(ConfigValues.lobbyWorld).getSpawnLocation());
            }
            gameStatus = GameStatus.RESETTING;
            Bukkit.getScheduler().runTaskAsynchronously(HezhongSkywars.INSTANCE.getPlugin(), () -> {
                try {
                    HezhongSkywars.INSTANCE.getGameManager().resetGame(mapName);
                } catch (Exception e) {
                    HezhongSkywars.INSTANCE.getLogger().warning("HSW Failed to reset game");
                    e.printStackTrace();
                }
            });

        }, 20 * 20);

    }
    private void refreshAllChests() {
        for (Map.Entry<Location, Chest> entry : chests.entrySet()) {
            Location loc = entry.getKey();
            refreshChest(loc);
        }
    }
    private void refreshChest(Location location) {

        // 希望能跑！
        if (location.getWorld() != world) {
            return;
        }
        Chest gameChest = chests.get(location);
        if (gameChest == null) {
            HezhongSkywars.INSTANCE.getLogger().warning("HSW Game Chest is null!");
            throw new RuntimeException("[HSW] Cannot get Game Chest");
        }
        List<ChestItem> generated = gameChest.randomGenerateItems();
        // ChestItem转ItemStack
        List<ItemStack> items = new ArrayList<>();
        for  (ChestItem item : generated) items.add(item.toItem());

        Block block = location.getBlock();
        if (!(block instanceof org.bukkit.block.Chest)) return;
        org.bukkit.block.Chest chest = (org.bukkit.block.Chest) block;

        Inventory inv = chest.getInventory();
        int size = inv.getSize();

        List<Integer> slots = new ArrayList<>();
        for (int i = 0; i < size; i++) slots.add(i);
        Collections.shuffle(slots);

        for (int i = 0; i < items.size(); i++) {
            inv.setItem(slots.get(i), items.get(i));
        }

    }
    private Location selectSpawnPoint(Player pp) {
        for (Pair<Location, Player> pair : spawns) {
            if (pair.getY() == null) {
                // 设置出生点被占用
                pair.setY(pp);
                return pair.getX();
            }
        }
        return null; // 出生点被占满了
    }

    private void countdownAndAutoStart() {
        countdownTask = Bukkit.getScheduler().runTaskTimer(HezhongSkywars.INSTANCE.getPlugin(), () -> {
            int time = countdown;
            if (time > 20 && time % 10 == 0) {
                for (Player player : allPlayers) {
                    TitleAPI.sendTitle(player, 0, 60, 20, ColorT.t("&a" + time));
                }
            } else if (time <= 20 && time > 5 && time % 5 == 0) {
                for (Player player : allPlayers) {
                    TitleAPI.sendTitle(player, 0, 60, 20, ColorT.t("&e" + time));
                }
            } else if (time <= 5 && time > 0) {
                for (Player player : allPlayers) {
                    TitleAPI.sendTitle(player, 0, 60, 20, ColorT.t("&e" + time), ColorT.t("&e准备战斗！"));
                }
            }
            if (time == 0) {
                Bukkit.getPluginManager().callEvent(new HSWGameStartEvent(mapName));
                // 退出
                countdownTask.cancel();

            }
        }, 0, 20);
    }
    private void gameEventRunner() {
        eventRunnerTask = Bukkit.getScheduler().runTaskTimer(HezhongSkywars.INSTANCE.getPlugin(), () -> {
            runnedTime++;
            for (GameEvent e : events) {
                if (e.getTime() == runnedTime) {
                    // if else if else if else if else......
                    if (e.getType().equals(GameEvent.EventType.RESETCHEST)) {
                        refreshAllChests();
                        playSound(XSound.BLOCK_CHEST_OPEN);
                        // 计分板是独立的......
                    }
                }
            }
        }, 0, 20);
    }

    private void playSound(XSound sound) {
        for (Player player : getPlayersInWorld()) {
            sound.play(player);
        }
    }
    private void sendMessage(String message) {
        for (Player player : getPlayersInWorld()) {
            player.sendMessage(ColorT.t(message));
        }
    }
    private List<Player> getPlayersInWorld() {
        return world.getPlayers();
    }

}
