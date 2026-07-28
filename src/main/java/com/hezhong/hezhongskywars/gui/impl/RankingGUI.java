package com.hezhong.hezhongskywars.gui.impl;

import com.cryptomorin.xseries.XMaterial;
import com.hezhong.hezhongskywars.HezhongSkywars;
import com.hezhong.hezhongskywars.gui.MultiPageGUI;
import com.hezhong.hezhongskywars.player.SwPlayer;
import com.hezhong.hezhongskywars.utils.ColorT;
import com.hezhong.hezhongskywars.utils.type.DatabaseStatsData;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.stream.Collectors;

public class RankingGUI extends MultiPageGUI {

    private static final int ROWS = 6;

    public RankingGUI(Player owner, SwPlayer ownerSp) {
        super(owner, ownerSp, "&8排行榜", ROWS,
                createPageItem("&a下一指标"),
                createPageItem("&a上一指标"));
        build();
    }

    // 新增枚举定义
    public enum MetricType {
        KILLS("击杀") {
            @Override
            public int getValue(DatabaseStatsData data) {
                return data.kills;
            }
        },
        WINS("胜场") {
            @Override
            public int getValue(DatabaseStatsData data) {
                return data.wins;
            }
        },
        COINS("硬币") {
            @Override
            public int getValue(DatabaseStatsData data) {
                return data.coins;
            }
        },
        EXPERIENCE("经验") {
            @Override
            public int getValue(DatabaseStatsData data) {
                return data.exps;
            }
        };

        private final String name;

        MetricType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public abstract int getValue(DatabaseStatsData data);
    }

    private void build() {
        multiPageInventory.clear();

        List<DatabaseStatsData> allDatas = new ArrayList<>(
            HezhongSkywars.INSTANCE.getDatabase().allDatas.values()
        );
        if (allDatas == null || allDatas.isEmpty()) {
            showPage();
            return;
        }

        int slot = 1;
        MetricType[] metrics = MetricType.values(); // 替代原 METRICS 数组
        for (int m = 0; m < metrics.length; m++) {
            MetricType metric = metrics[m];

            // 从大到小排序
            List<DatabaseStatsData> sorted = new ArrayList<>(allDatas);
            sorted.sort((a, b) -> Integer.compare(metric.getValue(b), metric.getValue(a)));

            ItemStack metricFlag = new ItemStack(XMaterial.PAPER.get());
            ItemMeta meta = metricFlag.getItemMeta();
            meta.setDisplayName(ColorT.t("&6当前指标：&e" + metric.getName()));
            String prev = "无";
            String next = "无";
            if (m < metrics.length - 1) {
                next = metrics[m + 1].getName();
            }
            if (m >= 1) {
                prev = metrics[m - 1].getName();
            }

            meta.setLore(ColorT.t(Arrays.asList("&7上一个：&f" + prev, "&7下一个：&f" + next)));
            metricFlag.setItemMeta(meta);
            multiPageInventory.put(slot++, metricFlag);

            int remainSlots = 9 * (ROWS - 1) - 1; // -1是有指标
            for (DatabaseStatsData data : sorted) {
                ItemStack item = new ItemStack(XMaterial.PLAYER_HEAD.get());
                ItemMeta pMeta = item.getItemMeta();
                pMeta.setDisplayName(ColorT.t("&e" + data.playerName));
                pMeta.setLore(ColorT.t(Collections.singletonList("&7" + metric.getName() + "：&f" + metric.getValue(data))));
                item.setItemMeta(pMeta);
                multiPageInventory.put(slot++, item);
                remainSlots--;
            }
            if (remainSlots > 0) {
                for (int i = 1; i <= remainSlots; i++) {
                    multiPageInventory.put(slot++, new ItemStack(XMaterial.AIR.get()));
                }
            }
        }

        showPage();
    }

    @Override
    protected void handleMultiPageClick(InventoryClickEvent event) {
    }
}