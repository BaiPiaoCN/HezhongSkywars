package com.hezhong.hezhongskywars.db;

import com.hezhong.hezhongskywars.HezhongSkywars;
import com.hezhong.hezhongskywars.config.ConfigValues;
import com.hezhong.hezhongskywars.db.mysql.MySQLDataBase;
import com.hezhong.hezhongskywars.db.sqlite.SQLiteDataBase;
import com.hezhong.hezhongskywars.utils.type.DatabaseStatsData;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

public class DataBaseController {
    @Getter
    private final IDataBase db;
    public final Set<UUID> writingPlayers = new CopyOnWriteArraySet<>(); // 用于存储退出后，写数据库还没完成的玩家信息
    // 在数据库写完前，不允许玩家进入服务器，避免数据丢失


    public DataBaseController() {
        DataBaseType type = ConfigValues.dataBaseConfig.getType();
        if (type == DataBaseType.SQLITE) {
            db = new SQLiteDataBase(ConfigValues.dataBaseConfig.getHost(), ConfigValues.dataBaseConfig.getTablePrefix());
        } else if (type == DataBaseType.MYSQL) {
            db = new MySQLDataBase(ConfigValues.dataBaseConfig.getHost(), ConfigValues.dataBaseConfig.getPort(), ConfigValues.dataBaseConfig.getUser(), ConfigValues.dataBaseConfig.getPassword(),
                    ConfigValues.dataBaseConfig.getDatabase(), ConfigValues.dataBaseConfig.getTablePrefix(), ConfigValues.dataBaseConfig.getReconnectTimeout());
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
    public List<DatabaseStatsData> getAllDatabaseStats() {
        if (Bukkit.isPrimaryThread()) {
            HezhongSkywars.INSTANCE.getLogger().severe("Call database method with MAIN THREAD???");
            return null;
        }
        if (db != null) {
            return db.getAllDatabaseStats();
        } else return new ArrayList<>();
    }
}
