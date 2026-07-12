package com.hezhong.hezhongskywars.utils.type;


import java.util.UUID;

public class DatabaseStatsData {
    public UUID uuid = null;
    public String playerName = "";
    public int kills = 0;
    public int deaths = 0;
    public int gamesPlayed = 0;
    public int wins = 0;
    public int coins = 0;
    public DatabaseStatsData(UUID uuid, String playerName) {
        this.uuid = uuid;
        this.playerName = playerName;
    }

}
