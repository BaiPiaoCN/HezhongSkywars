package com.hezhong.hezhongskywars.gui.impl;

import com.cryptomorin.xseries.XMaterial;
import com.hezhong.hezhongskywars.HezhongSkywars;
import com.hezhong.hezhongskywars.config.ConfigValues;
import com.hezhong.hezhongskywars.config.KitConfig;
import com.hezhong.hezhongskywars.gui.GUIListener;
import com.hezhong.hezhongskywars.gui.MultiPageGUI;
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

public class KitSelectGUI extends MultiPageGUI {

    private static final int ROWS = 6;
    private final Map<Integer, String> slotKitMap = new HashMap<>();
    private final List<String> kitOrder = new ArrayList<>();

    public KitSelectGUI(Player player, SwPlayer swPlayer) {
        super(player, swPlayer, "&8选择职业", ROWS, createPageItem("&a下一页"), createPageItem("&a上一页"));
        build();
    }
    private void build() {
        int slot = 0;
        for (Map.Entry<String, KitConfig> entry : ConfigValues.kitConfigs.entrySet()) {
            String name = entry.getKey();
            KitConfig config = entry.getValue();

            multiPageInventory.put(slot, buildDisplayItem(name, config));
            kitOrder.add(name); // 按顺序，所有职业的列表
            slot++;
        }
        showPage();
    }

    @Override
    protected void showPage() {
        slotKitMap.clear();
        super.showPage();

        // 我怎么忘了计算这玩意


        int itemsPerPage = (ROWS - 1) * 9; // MultiPageGUI占用最后一行，因此我们只用rows - 1行
        int start = currentPage * itemsPerPage; // 0-based，不需要+1
        int end = Math.min(start + itemsPerPage, kitOrder.size());

        for (int i = start; i < end; i++) {
            slotKitMap.put(i - start, kitOrder.get(i));
        }
    }

    private ItemStack buildDisplayItem(String name, KitConfig config) {
        String matStr = config.getMaterial();
        ItemStack item;
        if (matStr == null || matStr.isEmpty()) {
            item = new ItemStack(Material.BOOK);
        } else {
            item = XMaterial.matchXMaterial(matStr).orElse(XMaterial.BOOK).parseItem();
        }

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ColorT.t("&a" + name));

        List<String> lore = new ArrayList<>();
        lore.add("");
        if (config.getCoins() > 0) {
            lore.add(ColorT.t("&7价格: &e" + config.getCoins() + " 硬币"));
        } else {
            lore.add(ColorT.t("&a默认拥有"));
        }
        if (config.getPermission() != null && !config.getPermission().isEmpty()) {
            lore.add(ColorT.t("&7权限: &b" + config.getPermission()));
        } else {
            lore.add(ColorT.t("&7无需权限"));
        }
        lore.add(ColorT.t("&7物品数量: &f" + config.getItems().size()));
        lore.add("");
        if (ownerSp.getPlayingGame() != null) {
            lore.add(ColorT.t("&a左键选择 &7| &e右键预览"));
        } else {
            lore.add(ColorT.t("&a左键购买 &7| &e右键预览"));
        }

        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public void handleMultiPageClick(InventoryClickEvent event) {
        int slot = event.getSlot();

        String kitName = slotKitMap.get(slot);
        if (kitName == null) return;

        Player player = owner;

        if (event.isLeftClick()) {
            player.closeInventory();
            player.performCommand("hsw selectKit " + kitName);

        } else if (event.isRightClick()) {
            player.closeInventory();
            Bukkit.getScheduler().runTask(HezhongSkywars.INSTANCE.getPlugin(), () -> {
                KitConfig config = ConfigValues.kitConfigs.get(kitName);
                if (config == null) return;
                openPreview(kitName, config);
            });
        }
    }

    private void openPreview(String kitName, KitConfig config) {
        int rows = 4;
        Inventory preview = Bukkit.createInventory(null, rows * 9, ColorT.t("&8预览 " + kitName));

        int slot = 0;
        for (CustomItem customItem : config.getItems()) {
            ItemStack item = customItem.toItem();
            if (item != null) {
                preview.setItem(slot++, item);
            }
        }

        owner.openInventory(preview);
        GUIListener.previews.add(preview);
    }
}
