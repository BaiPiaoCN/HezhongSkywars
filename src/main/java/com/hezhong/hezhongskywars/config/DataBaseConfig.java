package com.hezhong.hezhongskywars.config;

import com.hezhong.hezhongskywars.db.DataBaseType;
import lombok.Getter;

@Getter
public class DataBaseConfig {

    private final DataBaseType type;

    // SQLite-specific config
    private final String sqliteFile;
    private final String sqliteTablePrefix;

    // MySQL-specific config
    private final String mysqlHost;
    private final int mysqlPort;
    private final String mysqlUser;
    private final String mysqlPassword;
    private final String mysqlDatabase;
    private final String mysqlTablePrefix;
    private final int mysqlReconnectTimeout;

    public DataBaseConfig(
            DataBaseType type,
            String sqliteFile, String sqliteTablePrefix,
            String mysqlHost, int mysqlPort, String mysqlUser,
            String mysqlPassword, String mysqlDatabase,
            String mysqlTablePrefix, int mysqlReconnectTimeout) {
        this.type = type;
        this.sqliteFile = sqliteFile;
        this.sqliteTablePrefix = sqliteTablePrefix;
        this.mysqlHost = mysqlHost;
        this.mysqlPort = mysqlPort;
        this.mysqlUser = mysqlUser;
        this.mysqlPassword = mysqlPassword;
        this.mysqlDatabase = mysqlDatabase;
        this.mysqlTablePrefix = mysqlTablePrefix;
        this.mysqlReconnectTimeout = mysqlReconnectTimeout;
    }
}
