package com.hezhong.hezhongskywars.db.sqlite;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hezhong.hezhongskywars.db.IDataBase;
import com.hezhong.hezhongskywars.utils.type.DatabaseStatsData;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.entity.Player;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SQLiteDataBase implements IDataBase {
    private final String databasePath;
    private final String tableNamePrefix;
    private final Gson gson;
    private HikariDataSource dataSource;


    // 数据库文件路径
    public SQLiteDataBase(String databasePath, String tableNamePrefix) {
        this.databasePath = databasePath;
        this.tableNamePrefix = tableNamePrefix;
        this.gson = new GsonBuilder().create();
    }

    @Override
    public void connect() {
        try {
            // 确保数据库文件所在目录存在
            File dbFile = new File(databasePath);
            File parentDir = dbFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            HikariConfig config = new HikariConfig();
            config.setDriverClassName("org.sqlite.JDBC");
            String url = "jdbc:sqlite:" + databasePath;
            config.setJdbcUrl(url);
            config.setConnectionTimeout(5000L);
            config.setMaximumPoolSize(4);
            config.setMinimumIdle(1);
            config.setIdleTimeout(300000L);
            config.setMaxLifetime(1800000L);
            config.setConnectionTestQuery("SELECT 1");
            config.setPoolName("HezhongSW-SQLite-Pool");
            // SQLite 连接属性
            dataSource = new HikariDataSource(config);

            try (Connection connection = dataSource.getConnection();
                 Statement stmt = connection.createStatement()) {
                stmt.execute("PRAGMA journal_mode=WAL");
                stmt.execute("PRAGMA foreign_keys=ON");
                stmt.execute("PRAGMA busy_timeout=5000");
                String sql = "CREATE TABLE IF NOT EXISTS " + tableNamePrefix + "_stats (" +
                        "uuid VARCHAR(36) PRIMARY KEY, " +
                        "stats TEXT NOT NULL" +
                        ")";
                stmt.executeUpdate(sql);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to SQLite database: " + e.getMessage(), e);
        }
    }

    @Override
    public void disconnect() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    @Override
    public String getUser() {
        return ""; // SQLite 无需用户
    }

    @Override
    public String getPassword() {
        return ""; // SQLite 无需密码
    }

    @Override
    public Object getConnectionPool() {
        return dataSource;
    }

    @Override
    public String getDatabaseName() {
        return databasePath;
    }

    @Override
    public String getTableNamePrefix() {
        return tableNamePrefix;
    }

    @Override
    public DatabaseStatsData getDatabaseStats(UUID pp) {
        String sql = "SELECT stats FROM " + tableNamePrefix + "_stats WHERE uuid = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, pp.toString());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String json = rs.getString("stats");
                    return gson.fromJson(json, DatabaseStatsData.class);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get database stats", e);
        }
        return null;
    }

    @Override
    public void setDatabaseStats(UUID pp, DatabaseStatsData stats) {
        String json = gson.toJson(stats);
        String sql = "INSERT OR REPLACE INTO " + tableNamePrefix + "_stats (uuid, stats) VALUES (?, ?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, pp.toString());
            pstmt.setString(2, json);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to set database stats", e);
        }
    }

    @Override
    public void setAllDatabaseStats(Map<UUID, DatabaseStatsData> allStats) {
        String sql = "INSERT OR REPLACE INTO " + tableNamePrefix + "_stats (uuid, stats) VALUES (?, ?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            for (Map.Entry<UUID, DatabaseStatsData> entry : allStats.entrySet()) {
                String json = gson.toJson(entry.getValue());
                pstmt.setString(1, entry.getKey().toString());
                pstmt.setString(2, json);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to set all database stats", e);
        }
    }

    @Override
    public List<DatabaseStatsData> getAllDatabaseStats() {
        String sql = "SELECT uuid, stats FROM " + tableNamePrefix + "_stats";
        List<DatabaseStatsData> list = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                String json = rs.getString("stats");
                DatabaseStatsData stats = gson.fromJson(json, DatabaseStatsData.class);
                if (stats != null) {
                    list.add(stats);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get all database stats", e);
        }
        return list;
    }
}