package com.hezhong.hezhongskywars.listeners;


import com.hezhong.hezhongskywars.config.ConfigValues;
import com.hezhong.hezhongskywars.gui.GUIListener;
import com.hezhong.hezhongskywars.gui.HezhongSkywarsGUI;
import com.hezhong.hezhongskywars.gui.impl.KitSelectGUI;
import com.hezhong.hezhongskywars.utils.ColorT;
import com.hezhong.hezhongskywars.utils.SpecialItems;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class GeneralListener implements Listener {
    @EventHandler
    public void onChangeWorld(PlayerChangedWorldEvent e) {
        Player p = e.getPlayer();

        if (Objects.equals(p.getWorld().getName(), ConfigValues.lobbyWorld)) {
            ItemStack kitSelector = SpecialItems.kitSelector();
            p.getInventory().addItem(kitSelector);
        }
    }
    // 用于处理右键物品的效果
    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        Action action = e.getAction();
        if (action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR) {
            // 获取手持物品
            ItemStack held = p.getItemInHand();
            if (SpecialItems.isKitSelector(held)) {
                p.performCommand("hsw kitSelectorGUI");
            }
            if (SpecialItems.isLobbyTeleporter(held)) {
                p.performCommand("hsw hub");
                p.sendMessage(ColorT.t("&a传送到大厅"));
            }
        }
    }
}
