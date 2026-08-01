package com.hezhong.hezhongskywars.utils.type;


import com.hezhong.hezhongskywars.config.ConfigValues;
import com.hezhong.hezhongskywars.player.SwPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DatabaseStatsData {
    public UUID uuid = null;
    public String playerName = "";
    public boolean REFRESHED = false;

    public int kills = 0;
    public int assists = 0;
    public int deaths = 0;
    public int gamesPlayed = 0;
    public int wins = 0;
    public int coins = 0;
    public int exps = 0;
    // 不存Level，这个动态计算
    public List<SwPlayer.SwPlayerKit> kits = new ArrayList<>();
    public DatabaseStatsData(UUID uuid, String playerName) {
        this.uuid = uuid;
        this.playerName = playerName;
    }

    // 集成击杀和胜利方法

    public int getInGameLevel() {
        // 直接靠经验计算等级
        int level = 1;
        for (int i = 0; i < (ConfigValues.levelNeedExps.size() - 1); i++) {
            int needExps = ConfigValues.levelNeedExps.get(i);
            int nextLevelNeedExps = ConfigValues.levelNeedExps.get(i + 1);
            if (exps >= needExps && exps < nextLevelNeedExps) {
                level = (i + 1);
            }
        }
        return level;
    }

}
