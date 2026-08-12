package com.hezhong.hezhongskywars.db;

import com.hezhong.hezhongskywars.HezhongSkywars;
import com.hezhong.hezhongskywars.config.ConfigValues;
import com.hezhong.hezhongskywars.db.h2.H2DataBase;
import com.hezhong.hezhongskywars.db.mysql.MySQLDataBase;
import com.hezhong.hezhongskywars.manager.SwPlayerManager;
import com.hezhong.hezhongskywars.player.SwPlayer;
import com.hezhong.hezhongskywars.utils.type.DatabaseStatsData;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
@Getter
public class DataBaseController {

    private final IDataBase db;
    public final Set<UUID> writingPlayers = new CopyOnWriteArraySet<>(); // 用于存储退出后，写数据库还没完成的玩家信息
    // 在数据库写完前，不允许玩家进入服务器，避免数据丢失

    @Setter
    public volatile Map<UUID, DatabaseStatsData> allDatas = new ConcurrentHashMap<>();


    public DataBaseController() {
        DataBaseType type = ConfigValues.dataBaseConfig.getType();
        if (type == DataBaseType.H2) {
            db = new H2DataBase(ConfigValues.dataBaseConfig.getH2File(), ConfigValues.dataBaseConfig.getH2TablePrefix(), ConfigValues.dataBaseConfig.getH2MaxPoolSize(), ConfigValues.dataBaseConfig.getH2MinIdle());
        } else if (type == DataBaseType.MYSQL) {
            db = new MySQLDataBase(ConfigValues.dataBaseConfig.getMysqlHost(), ConfigValues.dataBaseConfig.getMysqlPort(), ConfigValues.dataBaseConfig.getMysqlUser(), ConfigValues.dataBaseConfig.getMysqlPassword(),
                    ConfigValues.dataBaseConfig.getMysqlDatabase(), ConfigValues.dataBaseConfig.getMysqlTablePrefix(), ConfigValues.dataBaseConfig.getMysqlReconnectTimeout());
        } else {
            db = null;
            throw new UnsupportedOperationException("Unsupported database " + type);
        }
    }
    public void connect() {
        if (db != null) {
            db.connect();
            HezhongSkywars.INSTANCE.getLogger().info("Connected to Database.");
        }
    }
    public void disconnect() {
        if (db != null) {
            db.disconnect();
            HezhongSkywars.INSTANCE.getLogger().info("Disconnected to Database.");
        }
    }
    public DatabaseStatsData getDatabaseStats(UUID pp) {
        if (Bukkit.isPrimaryThread()) {
            HezhongSkywars.INSTANCE.getLogger().severe("Call database method with MAIN THREAD???");
            return null;
        }
        if (db != null) {
            return db.getDatabaseStats(pp);
        } else return null;
    }
    public void setDatabaseStats(UUID pp, DatabaseStatsData stats) {
        if (Bukkit.isPrimaryThread()) {
            HezhongSkywars.INSTANCE.getLogger().severe("Call database method with MAIN THREAD???");
            return;
        }
        if (db != null) {
            db.setDatabaseStats(pp, stats);
        }
    }
    public void setAllDatabaseStats(Map<UUID, DatabaseStatsData> allStats) {
        if (Bukkit.isPrimaryThread()) {
            HezhongSkywars.INSTANCE.getLogger().severe("Call database method with MAIN THREAD???");
            return;
        }
        if (db != null) {
            db.setAllDatabaseStats(allStats);
        }
    }

    public List<DatabaseStatsData> getAllDatabaseStats() {
        if (Bukkit.isPrimaryThread()) {
            HezhongSkywars.INSTANCE.getLogger().severe("Call database method with MAIN THREAD???");
            return null;
        }
        if (db != null) {
            return db.getAllDatabaseStats();
        } else return new ArrayList<>();
    }

    public void refreshAllDatasCache() {
        List<DatabaseStatsData> all = getAllDatabaseStats();
        for (DatabaseStatsData stats : all) {
            UUID uuid = stats.uuid;
            allDatas.put(uuid, stats);
        }
        for (SwPlayer sp : SwPlayerManager.getPlayers().values()) {
            UUID uuid = sp.getPlayer().getUniqueId();
            allDatas.put(uuid, sp.getStats());
        }
    }
}