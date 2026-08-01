package com.hezhong.hezhongskywars.db;

import com.hezhong.hezhongskywars.utils.type.DatabaseStatsData;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface IDataBase {
    // Getter
    String getUser();
    String getPassword();
    Object getConnectionPool();
    String getDatabaseName();
    String getTableNamePrefix();

    // 设置器
    DatabaseStatsData getDatabaseStats(UUID pp);
    void setDatabaseStats(UUID pp, DatabaseStatsData stats);
    void setAllDatabaseStats(Map<UUID, DatabaseStatsData> allStats);
    List<DatabaseStatsData> getAllDatabaseStats();
    ArrayList<String> getRawAllDatabaseStats();

    // 状态
    void connect();
    void disconnect();
    // 必须内部保持连接
}
