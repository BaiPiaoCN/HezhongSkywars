package com.hezhong.hezhongskywars.manager;

import com.hezhong.hezhongskywars.HezhongSkywars;
import com.hezhong.hezhongskywars.config.ChestConfig;
import com.hezhong.hezhongskywars.config.ConfigValues;
import com.hezhong.hezhongskywars.config.MapConfig;
import com.hezhong.hezhongskywars.game.Chest;
import com.hezhong.hezhongskywars.game.Game;
import com.hezhong.hezhongskywars.utils.ColorT;
import com.hezhong.hezhongskywars.utils.type.ChestItem;
import lombok.Getter;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.FileUtil;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class GameManager {
    private final Plugin serverPlugin;
    @Getter
    private final Map<String, Game> games = new HashMap<>();
    // K:V => 地图名 : 游戏实例
    public GameManager(Plugin serverPlugin) {
        this.serverPlugin = serverPlugin;
    }


    // 此代码需要异步执行 Async
    public void init() {
        // 必须主线程运行
        // 必须确保配置已加载
        Bukkit.getScheduler().runTaskAsynchronously(serverPlugin, () -> {
            try {
                for (Map.Entry<String, MapConfig> entry : ConfigValues.mapConfigs.entrySet()) {
                    // 遍历，读取世界
                    resetGame(entry.getKey());
                }
            } catch (Exception e) {
                HezhongSkywars.INSTANCE.getLogger().warning("HSW Failed to init games");
                e.printStackTrace();
            }
        });

    }
    public void resetGame(String mapName) throws FileNotFoundException {
        try {
        // 主线程执行会导致死锁主线程，绝对不能主线程执行
        if (Bukkit.isPrimaryThread()) {
            HezhongSkywars.INSTANCE.getLogger().warning("HSW resetGame called from main thread!");
            return;
        }
        if (ConfigValues.mapConfigs.containsKey(mapName)) {
            MapConfig mc = ConfigValues.mapConfigs.get(mapName); // 配置
            // 去寻找原世界
            File originalWorld = new File(serverPlugin.getDataFolder(), "maps/" + mc.getOriginalWorld());
            if (!originalWorld.exists()) {
                throw new FileNotFoundException("Cannot find original world " + mc.getOriginalWorld());
            }
            CompletableFuture<Boolean> futureUnload = new CompletableFuture<>();
            Bukkit.getScheduler().runTask(HezhongSkywars.INSTANCE.getPlugin(), () -> {
                if (Bukkit.getWorld(mc.getCopyWorld()) != null) {
                    Bukkit.unloadWorld(mc.getCopyWorld(), false);
                }
                futureUnload.complete(true);
            });
            futureUnload.join();
            copyWorld(mc.getOriginalWorld(), mc.getCopyWorld());
            // 文件复制后，加载世界
            CompletableFuture<World> futureLoad = new CompletableFuture<>();
            Bukkit.getScheduler().runTask(HezhongSkywars.INSTANCE.getPlugin(), () -> {
                WorldCreator worldCreator = new WorldCreator(mc.getCopyWorld());
                World w = worldCreator.createWorld();
                if (w != null) {
                    // 禁止自然刷怪
                    w.setGameRuleValue("doMobSpawning", "false");
                }
                futureLoad.complete(w);
            });
            // 等着
            futureLoad.join();
            World w = futureLoad.get();
            // 此时全部载入完成
            if (w != null) {
                Map<Location, Chest> chests = new HashMap<>();
                for (Map.Entry<Vector, String> entry : mc.getChests().entrySet()) {
                    // 初始化真正的箱子

                    ChestConfig cc = ConfigValues.chestConfigs.get(entry.getValue());
                    Chest chest = new Chest(mapName, cc.getMinFilled(), cc.getMaxFilled());
                    // 遍历所有的物品
                    for (Map.Entry<ChestItem, Integer> itemEntry : cc.getItem().entrySet()) {
                        chest.addChestItem(itemEntry.getValue(), itemEntry.getKey());
                    }
                    chests.put(new Location(w, entry.getKey().getX(), entry.getKey().getY(), entry.getKey().getZ()), chest);
                }
                List<Location> spawns = new ArrayList<>();
                for (Vector v : mc.getSpawns()) {
                    spawns.add(new Location(w, v.getX(), v.getY(), v.getZ()));
                }
                games.put(mapName, new Game(mapName, w, chests, spawns, mc.getGameEvents()));
            }
            return;

        }
        } catch (ExecutionException | InterruptedException e) {
            HezhongSkywars.INSTANCE.getLogger().warning("HSW Failed to reset world");
            e.printStackTrace();
        }
    }
    private void copyWorld(String original, String target) {
        // 必须确保世界已经卸载
        // 此代码必须异步执行
        try {
            // 删除世界
            File originalWorld = new File(serverPlugin.getDataFolder(), "maps/" + original);
            File targetWorld = new File(Bukkit.getWorldContainer(), target);
            if (targetWorld.exists()) FileUtils.deleteDirectory(targetWorld);
            FileUtils.copyDirectory(originalWorld, targetWorld);
            // 删session.lock
            File sessionLock = new File(targetWorld, "session.lock");
            if (sessionLock.exists()) {
                FileUtils.delete(sessionLock);
            }
        } catch (Exception e) {
            HezhongSkywars.INSTANCE.getLogger().warning("HSW Failed to copy world");
            e.printStackTrace();
        }


    }
}
