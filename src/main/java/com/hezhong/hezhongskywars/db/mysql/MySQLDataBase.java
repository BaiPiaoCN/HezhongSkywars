package com.hezhong.hezhongskywars.db.mysql;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hezhong.hezhongskywars.db.IDataBase;
import com.hezhong.hezhongskywars.utils.type.DatabaseStatsData;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MySQLDataBase implements IDataBase {
    private final String host;
    private final int port;
    private final String userName;
    private final String password;
    private final String databaseName;
    private final String tableNamePrefix;
    private final Gson gson;
    private final int timeout;
    private HikariDataSource dataSource;

    public MySQLDataBase(String host, int port, String userName,
                         String password, String databaseName, String tableNamePrefix, int timeout) {
        this.host = host;
        this.port = port;
        this.userName = userName;
        this.password = password;
        this.databaseName = databaseName;
        this.tableNamePrefix = tableNamePrefix;
        this.timeout = timeout;
        this.gson = new GsonBuilder().create();
    }

    @Override
    public void connect() {
        try {
            HikariConfig config = new HikariConfig();
            config.setDriverClassName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://" + host + ":" + port + "/" + databaseName +
                    "?useUnicode=true" +
                    "&characterEncoding=UTF-8" +
                    "&useSSL=false" +
                    "&autoReconnect=true";
            config.setJdbcUrl(url);
            config.setUsername(userName);
            config.setPassword(password);
            config.setConnectionTimeout(timeout * 1L); // ms
            config.setMaximumPoolSize(8);
            config.setMinimumIdle(2);
            config.setIdleTimeout(300000L);
            config.setMaxLifetime(1800000L);
            config.setConnectionTestQuery("SELECT 1");
            config.setPoolName("HezhongSW-MySQL-Pool");

            dataSource = new HikariDataSource(config);

            try (Connection connection = dataSource.getConnection();
                 Statement stmt = connection.createStatement()) {
                String sql = "CREATE TABLE IF NOT EXISTS " + tableNamePrefix + "_stats (" +
                        "uuid VARCHAR(36) PRIMARY KEY, " +
                        "stats TEXT NOT NULL" +
                        ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";
                stmt.executeUpdate(sql);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to MySQL database: " + e.getMessage(), e);
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
        return userName;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Object getConnectionPool() {
        return dataSource;
    }

    @Override
    public String getDatabaseName() {
        return databaseName;
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
        String sql = "INSERT INTO " + tableNamePrefix + "_stats (uuid, stats) VALUES (?, ?) " +
                "ON DUPLICATE KEY UPDATE stats = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, pp.toString());
            pstmt.setString(2, json);
            pstmt.setString(3, json);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to set database stats", e);
        }
    }

    @Override
    public void setAllDatabaseStats(Map<UUID, DatabaseStatsData> allStats) {
        String sql = "INSERT INTO " + tableNamePrefix + "_stats (uuid, stats) VALUES (?, ?) " +
                "ON DUPLICATE KEY UPDATE stats = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            for (Map.Entry<UUID, DatabaseStatsData> entry : allStats.entrySet()) {
                String json = gson.toJson(entry.getValue());
                pstmt.setString(1, entry.getKey().toString());
                pstmt.setString(2, json);
                pstmt.setString(3, json);
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

    @Override
    public ArrayList<String> getRawAllDatabaseStats() {
        String sql = "SELECT stats FROM " + tableNamePrefix + "_stats";
        ArrayList<String> list = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                list.add(rs.getString("stats"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get raw all database stats", e);
        }
        return list;
    }
}