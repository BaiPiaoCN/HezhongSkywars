package com.hezhong.hezhongskywars.game;

import com.connorlinfoot.titleapi.TitleAPI;
import com.cryptomorin.xseries.XSound;
import com.hezhong.hezhongskywars.HezhongSkywars;
import com.hezhong.hezhongskywars.config.ConfigValues;
import com.hezhong.hezhongskywars.events.HSWGameStartEvent;
import com.hezhong.hezhongskywars.manager.SwPlayerManager;
import com.hezhong.hezhongskywars.player.SwPlayer;
import com.hezhong.hezhongskywars.utils.ColorT;
import com.hezhong.hezhongskywars.utils.type.ChestItem;
import com.hezhong.hezhongskywars.utils.type.Pair;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
public class Game {

    private final String mapName;
    private final World world;
    private final int maxPlayers;
    private GameStatus gameStatus;
    private List<Player> allPlayers;
    private Map<UUID, SwPlayingGamePlayer> playingPlayerStatus;
    private int runnedTime = 0;
    private final Map<Location, Chest> chests;
    private final List<Pair<Location, Player>> spawns; // y 占用出生点的玩家，用于分配
    private final List<GameEvent> events;
    private final List<GameEvent> eventsInFuture; // 还没执行的事件，[0]即下一个事件，用于计分板
    // 游戏需要的Tasks
    private BukkitTask countdownTask;
    private BukkitTask eventRunnerTask;

    private final int countdown;
    private int countdownRemaining;


    public Game(String mapName, World world, Map<Location, Chest> chests, List<Location> spawns, List<GameEvent> events) {
        this.mapName = mapName;
        this.world = world;
        gameStatus = GameStatus.WAITING;
        allPlayers = new ArrayList<>();
        playingPlayerStatus = new HashMap<>();
        this.chests = chests;
        this.spawns = new ArrayList<>();
        this.events = events;
        this.eventsInFuture = new ArrayList<>(events);
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
        // 必须设置SwPlayer状态。不能重复调用。
        if (gameStatus == GameStatus.WAITING) {
            if (allPlayers.size() >= maxPlayers) {
                return false;
            }
            restoreHide(player);
            allPlayers.add(player);
            playingPlayerStatus.put(player.getUniqueId(), new SwPlayingGamePlayer(player));
            // 将玩家传送到出生点
            Location selectedLocation = selectSpawnPoint(player); // 选择出生点
            assert selectedLocation != null : "?";
            createCage(selectedLocation); // 出生点笼子
            player.teleport(selectedLocation);
            player.getInventory().clear();
            player.getInventory().setArmorContents(null);
            player.setHealth(20);
            player.setFoodLevel(20);
            player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, Integer.MAX_VALUE, 1));
            player.setGameMode(GameMode.SURVIVAL);

            if (getAlivePlayers().size() >= ConfigValues.mapConfigs.get(mapName).getMinPlayersToAutostart()) {
                // 准备开始
                countdownAndAutoStart();
            }
        } else if (gameStatus == GameStatus.PLAYING || gameStatus == GameStatus.STOPPED) {
            // 中途进入，直接旁观
            if (!playingPlayerStatus.containsKey(player.getUniqueId())) {
                playingPlayerStatus.put(player.getUniqueId(), new SwPlayingGamePlayer(player));
            }
            toSpectate(player);
        }
        return true;
    }

    public void startGame() {
        // 接收到了StartEvent，开始游戏
        countdownRemaining = 0;
        countdownTask.cancel();
        refreshAllChests();
        for (Pair<Location, Player> spawn : spawns) {
            Location spawnLocation = spawn.getX();
            removeCage(spawnLocation);
        }
        gameStatus = GameStatus.PLAYING;
        sendMessage("&c&l战斗！");
        for (Player player : allPlayers) {
            TitleAPI.sendTitle(player, 0, 60, 20, ColorT.t("&c&l战斗！"));
        }
        gameEventRunner();
    }

    private void createCage(Location spawnPoint) {
        // 大粪，能跑就行，大不了再写一个
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

    public String processDeath(Player killed, Player killer, boolean quit) {
        // quit说明不是被杀的，是自己退的。ChangeWorld和QuitEvent都算
        // 因为QuitEvent会清除SwPlayer，因此我们把QuitEvent的处理和SwPlayer的销毁放在一起，注意先后顺序
        Location location = killed.getLocation();
        SwPlayer sp = SwPlayerManager.getPlayer(killed);
        assert sp != null : "?"; // 不会吧？
        sp.setNextSpawnLocation(location);
        if (gameStatus == GameStatus.PLAYING) { // 此时游戏还在进行
            killed.getInventory().clear();
            if (killer != null && !quit) { // 被杀了
                SwPlayingGamePlayer swpgpKilled = playingPlayerStatus.get(killed.getUniqueId());
                if (swpgpKilled.getStatus() == SwPlayingGamePlayer.PlayerStatus.ALIVE) {
                    SwPlayingGamePlayer swpgpKiller = playingPlayerStatus.get(killer.getUniqueId());
                    swpgpKiller.setKills(swpgpKiller.getKills() + 1);
                    swpgpKilled.setStatus(SwPlayingGamePlayer.PlayerStatus.DEAD);
                    // TODO: 此处预留，需要持久化保存统计数据
                    killed.getInventory().clear();
                    killed.spigot().respawn();
                    killed.teleport(location);
                }
                toSpectate(killed);
                if (getAlivePlayers().size() <= 1) {
                    normalEnd();
                }
                return ColorT.t("&7" + killed.getName() + " &e被 &7" + killer.getName() + " &e杀死了！");
            } else if (killer == null && !quit) {
                SwPlayingGamePlayer swpgpKilled = playingPlayerStatus.get(killed.getUniqueId());
                if (swpgpKilled.getStatus() == SwPlayingGamePlayer.PlayerStatus.ALIVE) {
                    swpgpKilled.setStatus(SwPlayingGamePlayer.PlayerStatus.DEAD);
                    // 此处预留，需要持久化保存统计数据
                    killed.spigot().respawn();
                    killed.teleport(location);
                }
                toSpectate(killed);
                // 直接默认返回值（击杀语）
            } else if (quit) {
                restoreHide(killed);
                allPlayers.remove(killed);
                SwPlayingGamePlayer swpgpKilled = playingPlayerStatus.get(killed.getUniqueId());
                if (swpgpKilled.getStatus() == SwPlayingGamePlayer.PlayerStatus.ALIVE) {
                    sendMessage("&7" + killed.getName() + " &e退出了。");
                }
                swpgpKilled.setStatus(SwPlayingGamePlayer.PlayerStatus.QUIT);
                sp.setPlayingGame(null);
                if (getAlivePlayers().size() <= 1) {
                    normalEnd();
                }
                return "";
            }

        } else if (gameStatus == GameStatus.WAITING || gameStatus == GameStatus.STOPPED) {
            if (!quit) {
                killed.spigot().respawn();
                killed.teleport(location);
            } else {
                allPlayers.remove(killed);
                playingPlayerStatus.remove(killed.getUniqueId());
                sp.setPlayingGame(null);
            }
        }
        if (getAlivePlayers().size() <= 1) {
            normalEnd();
        }
        return ColorT.t("&7" + killed.getName() + " &e死了。");
    }

    private void normalEnd() {
        if (gameStatus != GameStatus.PLAYING) return;
        gameStatus = GameStatus.STOPPED;
        List<Pair<Integer, Player>> mostKilled = new ArrayList<>();
        // 按击杀数排序
        for (Player player : allPlayers) {
            SwPlayingGamePlayer swpgp = playingPlayerStatus.get(player.getUniqueId());
            mostKilled.add(new Pair<>(swpgp.getKills(), player));
        }
        // 从大到小排
        Collections.sort(mostKilled, Comparator.comparingInt(Pair::getX));
        Collections.reverse(mostKilled);
        // 展示击杀Top3
        int i = 1;

        sendMessage("&7====================&a&l统计&7====================");

        // 展示数据前3的人
        for (Pair<Integer, Player> pair : mostKilled) {
            if (i > 3) break;
            sendMessage(String.format("&cTop %s &7%s &a击杀 %s", i, pair.getY().getName(), pair.getX()));
            i++;
        }
        // 发送胜利标题
        for (Player winner : getAlivePlayers()) {
            TitleAPI.sendTitle(winner, 0, 90, 10, ColorT.t("&e&lVICTORY"));
        }

        // 烟花声庆祝
        // 其实可用生成烟花，但这个先不搞，画个大饼先
        // TODO: 生成烟花
        BukkitRunnable fireworkTask = new BukkitRunnable() {
            private int count = 0;

            @Override
            public void run() {
                playSound(XSound.ENTITY_FIREWORK_ROCKET_SHOOT);
                if (++count >= 20 || allPlayers.size() == 0) {
                    this.cancel();
                }
            }
        };
        fireworkTask.runTaskTimer(HezhongSkywars.INSTANCE.getPlugin(), 0, 20);

        Bukkit.getScheduler().runTaskLater(HezhongSkywars.INSTANCE.getPlugin(), () -> {
            for (Player player : allPlayers) {
                SwPlayer sp = SwPlayerManager.getPlayer(player);
                sp.setPlayingGame(null);
                restoreHide(player);
                player.getInventory().clear();
                player.getInventory().setArmorContents(null);
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
        // 匹狼刷新全图箱子
        for (Map.Entry<Location, Chest> entry : chests.entrySet()) {
            Location loc = entry.getKey();
            refreshChest(loc);
        }
    }

    private void refreshChest(Location location) {
        Chest gameChest = chests.get(location);
        if (gameChest == null) {
            HezhongSkywars.INSTANCE.getLogger().warning("HSW Game Chest is null!");
            throw new RuntimeException("[HSW] Cannot get Game Chest");
        }
        // 随机生成一些箱子物品
        List<ChestItem> generated = gameChest.randomGenerateItems();
        List<ItemStack> items = new ArrayList<>();
        // 构造并转成ItemStack
        for (ChestItem item : generated) items.add(item.toItem());

        Block block = location.getBlock();
        if (!(block.getState() instanceof org.bukkit.block.Chest)) return;
        org.bukkit.block.Chest chest = (org.bukkit.block.Chest) block.getState();

        Inventory inv = chest.getInventory();
        int size = inv.getSize();

        // 打乱，然后塞进箱子
        List<Integer> slots = new ArrayList<>();
        for (int i = 0; i < size; i++) slots.add(i);
        Collections.shuffle(slots);

        inv.clear();
        for (int i = 0; i < items.size(); i++) {
            inv.setItem(slots.get(i), items.get(i));
        }
    }

    private Location selectSpawnPoint(Player pp) {
        for (Pair<Location, Player> pair : spawns) {
            if (pair.getY() == null) {
                pair.setY(pp);
                return pair.getX();
            }
        }
        return null;
    }

    private void countdownAndAutoStart() {
        if (countdownTask == null || countdownTask.isCancelled()) {
            AtomicInteger time = new AtomicInteger(countdown);
            countdownTask = Bukkit.getScheduler().runTaskTimer(HezhongSkywars.INSTANCE.getPlugin(), () -> {
                if (getAlivePlayers().size() < ConfigValues.mapConfigs.get(mapName).getMinPlayersToAutostart()) {
                    for (Player player : allPlayers) {
                        TitleAPI.sendTitle(player, 0, 60, 20, ColorT.t("&e倒计时取消"), ColorT.t("&c人数不足！至少需要 " + ConfigValues.mapConfigs.get(mapName).getMinPlayersToAutostart() + " 人"));
                        player.sendMessage(ColorT.t("&e倒计时取消 &c人数不足！至少需要 " + ConfigValues.mapConfigs.get(mapName).getMinPlayersToAutostart() + " 人"));
                    }
                    countdownTask.cancel();
                    return;
                }
                if (time.get() > 20 && time.get() % 10 == 0) {
                    for (Player player : allPlayers) {
                        TitleAPI.sendTitle(player, 0, 60, 20, ColorT.t("&a" + time));
                        player.sendMessage(ColorT.t("&a倒计时 " + time));
                    }
                } else if (time.get() <= 20 && time.get() > 5 && time.get() % 5 == 0) {
                    for (Player player : allPlayers) {
                        TitleAPI.sendTitle(player, 0, 60, 20, ColorT.t("&e" + time));
                        player.sendMessage(ColorT.t("&e倒计时 " + time));
                    }
                } else if (time.get() <= 5 && time.get() > 0) {
                    for (Player player : allPlayers) {
                        TitleAPI.sendTitle(player, 0, 60, 20, ColorT.t("&c" + time), ColorT.t("&e准备战斗！"));
                        player.sendMessage(ColorT.t("&c倒计时 " + time));
                    }
                }
                if (time.get() == 0) {
                    Bukkit.getPluginManager().callEvent(new HSWGameStartEvent(mapName));
                    countdownTask.cancel();
                }
                countdownRemaining = time.get();
                time.getAndDecrement();
            }, 0, 20);
        }
    }

    private void gameEventRunner() {
        eventRunnerTask = Bukkit.getScheduler().runTaskTimer(HezhongSkywars.INSTANCE.getPlugin(), () -> {
            runnedTime++;
            for (GameEvent e : events) {
                if (e.getTime() == runnedTime) {
                    if (e.getType().equals(GameEvent.EventType.RESETCHEST)) {
                        refreshAllChests();
                        playSound(XSound.BLOCK_CHEST_OPEN);
                    }
                    if (e.getType().equals(GameEvent.EventType.STOPGAME)) {
                        normalEnd();
                    }
                    eventsInFuture.remove(e);
                }
            }
            for (Player p : allPlayers) {
                p.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, Integer.MAX_VALUE, 1));
            }
        }, 0, 20);
    }

    private void toSpectate(Player pp) {
        SwPlayingGamePlayer swpgp = playingPlayerStatus.get(pp.getUniqueId());
        swpgp.setStatus(SwPlayingGamePlayer.PlayerStatus.SPECTATE);
        pp.setGameMode(GameMode.ADVENTURE);
        pp.setAllowFlight(true);
        pp.setFlying(true);
        for (Player aP : getAlivePlayers()) {
            aP.hidePlayer(pp);
        }
        TitleAPI.sendTitle(pp, 0, 40, 20, ColorT.t("&a你是旁观者"));
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


    public List<Player> getAlivePlayers() {
        List<Player> alive = new ArrayList<>();
        for (SwPlayingGamePlayer swpgp : playingPlayerStatus.values()) {
            if (swpgp.getStatus() == SwPlayingGamePlayer.PlayerStatus.ALIVE) {
                alive.add(swpgp.getPlayer());
            }
        }
        return alive;
    }

    public List<Player> getSpectators() {
        List<Player> specs = new ArrayList<>();
        for (SwPlayingGamePlayer swpgp : playingPlayerStatus.values()) {
            if (swpgp.getStatus() == SwPlayingGamePlayer.PlayerStatus.SPECTATE) {
                specs.add(swpgp.getPlayer());
            }
        }
        return specs;
    }

    public SwPlayingGamePlayer getPlayingPlayer(UUID uuid) { // 暴露给外
        return  playingPlayerStatus.get(uuid);
    }
    private void restoreHide(Player p) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            p.showPlayer(player);
        }
    }
}
