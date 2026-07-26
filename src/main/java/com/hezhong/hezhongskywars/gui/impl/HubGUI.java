package com.hezhong.hezhongskywars.gui.impl;

import com.cryptomorin.xseries.XMaterial;
import com.hezhong.hezhongskywars.HezhongSkywars;
import com.hezhong.hezhongskywars.config.ConfigValues;
import com.hezhong.hezhongskywars.config.KitConfig;
import com.hezhong.hezhongskywars.gui.GUIListener;
import com.hezhong.hezhongskywars.gui.HezhongSkywarsGUI;
import com.hezhong.hezhongskywars.player.SwPlayer;
import com.hezhong.hezhongskywars.utils.ColorT;
import com.hezhong.hezhongskywars.utils.type.CustomItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HubGUI extends HezhongSkywarsGUI {

    private static final int ROWS = 3;
    private final Map<Integer, Options> slotMap = new HashMap<>();

    public HubGUI(Player player, SwPlayer swPlayer) {
        super(player, swPlayer, "&8选择职业", ROWS);
        build();
    }

    private void build() {
        // 按顺序构建GUI
        // 物品|功能
        // 指南针|统计信息
        // 物品展示框|排行榜
        // 其中第一行和第三行都是空着的
        for (int row = 1; row <= ROWS; row++) {
            for (int column = 1; column <= 9; column++) {
                if (row == 1 || row == 3) continue;
                ItemStack item = new ItemStack(XMaterial.AIR.get());
                Options opt = Options.NULL;
                if (column == 3) {
                    item = new ItemStack(XMaterial.COMPASS.get());
                    ItemMeta meta = item.getItemMeta();
                    meta.setDisplayName(ColorT.t("&b玩家信息"));
                    item.setItemMeta(meta);
                    opt = Options.STATS;
                }
                if (column == 4) {
                    item = new ItemStack(XMaterial.ITEM_FRAME.get());
                    ItemMeta meta = item.getItemMeta();
                    meta.setDisplayName(ColorT.t("&c排行榜"));
                    item.setItemMeta(meta);
                    opt = Options.RANKING;
                }
                if (column == 5) {
                    item = new ItemStack(XMaterial.DIAMOND_SWORD.get());
                    ItemMeta meta = item.getItemMeta();
                    meta.setDisplayName(ColorT.t("&e职业查看"));
                    item.setItemMeta(meta);
                    opt = Options.KIT;
                }
                int slot = 9 * row + column - 10; // 9 * (row - 1) + column - 1化简
                inventory.setItem(slot, item);
                slotMap.put(slot, opt);
            }
        }

    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        int slot = event.getSlot();
        Options opt = slotMap.getOrDefault(slot, Options.NULL);
        if (opt == Options.STATS) {
            HezhongSkywarsGUI gui = new PlayerInfoGUI(owner, ownerSp);
            gui.open();
        }
        if (opt == Options.RANKING) {
            HezhongSkywarsGUI gui = new RankingGUI(owner, ownerSp);
            gui.open();
        }
        if (opt == Options.KIT) {
            HezhongSkywarsGUI gui = new KitSelectGUI(owner, ownerSp);
            gui.open();
        }
    }

    public enum Options {
        STATS,
        RANKING,
        NULL,
        KIT
    }

}