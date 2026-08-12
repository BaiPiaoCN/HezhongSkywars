package com.hezhong.hezhongskywars.config;

import com.hezhong.hezhongskywars.db.DataBaseType;
import lombok.Getter;

@Getter
public class DataBaseConfig {

    private final DataBaseType type;

    // H2-specific config
    private final String h2File;
    private final String h2TablePrefix;
    private final int h2MaxPoolSize;
    private final int h2MinIdle;

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
            String h2File, String h2TablePrefix, int h2MaxPoolSize, int h2MinIdle,
            String mysqlHost, int mysqlPort, String mysqlUser,
            String mysqlPassword, String mysqlDatabase,
            String mysqlTablePrefix, int mysqlReconnectTimeout) {
        this.type = type;
        this.h2File = h2File;
        this.h2TablePrefix = h2TablePrefix;
        this.h2MaxPoolSize = h2MaxPoolSize;
        this.h2MinIdle = h2MinIdle;
        this.mysqlHost = mysqlHost;
        this.mysqlPort = mysqlPort;
        this.mysqlUser = mysqlUser;
        this.mysqlPassword = mysqlPassword;
        this.mysqlDatabase = mysqlDatabase;
        this.mysqlTablePrefix = mysqlTablePrefix;
        this.mysqlReconnectTimeout = mysqlReconnectTimeout;
    }
}