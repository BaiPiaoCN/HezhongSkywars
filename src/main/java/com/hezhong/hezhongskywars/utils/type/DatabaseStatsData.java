package com.hezhong.hezhongskywars.utils.type;


import com.hezhong.hezhongskywars.config.ConfigValues;
import com.hezhong.hezhongskywars.player.SwPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DatabaseStatsData {
    public UUID uuid = null;
    public String playerName = "";
    public int kills = 0;
    public int deaths = 0;
    public int gamesPlayed = 0;
    public int wins = 0;
    public int coins = 0;
    public int exps = 0;
    // 不存Level，这个动态计算。
    public List<SwPlayer.SwPlayerKit> kits = new ArrayList<>();
    public DatabaseStatsData(UUID uuid, String playerName) {
        this.uuid = uuid;
        this.playerName = playerName;
    }

    public void addKills() {
        kills++;
        exps += ConfigValues.expKillAdd;
        coins += ConfigValues.coinsKillAdd;
    }
    public void addWins() {
        wins++;
        exps += ConfigValues.expWinAdd;
        coins += ConfigValues.coinsWinAdd;
    }

}
