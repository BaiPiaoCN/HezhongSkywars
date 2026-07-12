package com.hezhong.hezhongskywars.config;

import com.hezhong.hezhongskywars.db.DataBaseType;
import lombok.Getter;

@Getter
public class DataBaseConfig {
    private final DataBaseType type;
    private final String host;
    private final int port;
    private final String user;
    private final String password;
    private final String database;
    private final String tablePrefix;
    private final int reconnectTimeout;
    public DataBaseConfig(DataBaseType type, String host, int port, String user, String password, String database, String tablePrefix, int reconnectTimeout) {
        this.type = type;
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
        this.database = database;
        this.tablePrefix = tablePrefix;
        this.reconnectTimeout = reconnectTimeout;
    }
}
