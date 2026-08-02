package com.hezhong.hezhongskywars.gui.impl;

import com.cryptomorin.xseries.XMaterial;
import com.hezhong.hezhongskywars.HezhongSkywars;
import com.hezhong.hezhongskywars.gui.HezhongSkywarsGUI;
import com.hezhong.hezhongskywars.player.SwPlayer;
import com.hezhong.hezhongskywars.utils.ColorT;
import com.hezhong.hezhongskywars.utils.SimpleMath;
import com.hezhong.hezhongskywars.utils.type.DatabaseStatsData;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class PlayerInfoGUI extends HezhongSkywarsGUI {

    private static final int ROWS = 3;
    private final Map<Integer, Options> slotMap = new HashMap<>();

    public PlayerInfoGUI(Player owner, SwPlayer ownerSp) {
        super(owner, ownerSp, "&b玩家信息", ROWS);
        build();
    }

    private void build() {
        for (int row = 1; row <= ROWS; row++) {
            for (int column = 1; column <= 9; column++) {
                ItemStack item = new ItemStack(XMaterial.AIR.get());
                Options opt = Options.NULL;
                if (row == 1 && column == 1) {
                    item = new ItemStack(XMaterial.PLAYER_HEAD.get());
                    ItemMeta meta = item.getItemMeta();
                    meta.setDisplayName(ColorT.t("&e" + owner.getName()));
                    item.setItemMeta(meta);
                }
                if (row == 2) {
                    if (column == 3) {
                        DatabaseStatsData data = ownerSp.getStats();
                        item = new ItemStack(XMaterial.BOOK.get());
                        ItemMeta meta = item.getItemMeta();
                        meta.setDisplayName(ColorT.t("&b基本统计"));
                        meta.setLore(ColorT.t(Arrays.asList("&f总游戏 &7" + data.gamesPlayed,
                                "&a胜场 &7" + data.wins,
                                "&c败场 &7" + (data.gamesPlayed - data.wins),
                                "&fW/L &7" + SimpleMath.formatScale((data.gamesPlayed - data.wins) == 0 ? -1 : ((double) (data.wins) / (data.gamesPlayed - data.wins)), 2),
                                "&e硬币 &7" + data.coins,
                                "&b经验 &7" + data.exps)));
                        item.setItemMeta(meta);
                    }
                    if (column == 5) {
                        DatabaseStatsData data = ownerSp.getStats();
                        item = new ItemStack(XMaterial.IRON_SWORD.get());
                        ItemMeta meta = item.getItemMeta();
                        meta.setDisplayName(ColorT.t("&cPVP统计"));
                        meta.setLore(ColorT.t(Arrays.asList("&c总死亡 &7" + data.deaths,
                                "&a总击杀 &7" + data.kills,
                                "&fK/D &7" + SimpleMath.formatScale(data.deaths == 0 ? -1 : ((double) data.kills / data.deaths), 2))));
                        item.setItemMeta(meta);
                    }
                }
                if (row == 3) {
                    if (column == 1) {
                        item = new ItemStack(XMaterial.REDSTONE_BLOCK.get());
                        item = new ItemStack(XMaterial.IRON_SWORD.get());
                        ItemMeta meta = item.getItemMeta();
                        meta.setDisplayName(ColorT.t("&c返回"));
                        item.setItemMeta(meta);
                        opt = Options.BACK;
                    }
                }

                int slot = 9 * row + column - 10;
                inventory.setItem(slot, item);
                slotMap.put(slot, opt);

            }
        }
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        int slot = event.getSlot();
        Options opt = slotMap.getOrDefault(slot, Options.NULL);
        if (opt == Options.BACK) {
            HezhongSkywarsGUI gui = new HubGUI(owner, ownerSp);
            gui.open();
        }
    }

    public enum Options {
        BACK,
        NULL
    }
}
