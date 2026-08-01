package com.hezhong.hezhongskywars.listeners;


import com.cryptomorin.xseries.XMaterial;
import com.hezhong.hezhongskywars.config.ConfigValues;
import com.hezhong.hezhongskywars.utils.SpecialItems;
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
        Player player = e.getPlayer();

        if (Objects.equals(player.getWorld().getName(), ConfigValues.lobbyWorld)) {
            player.setHealth(20);

            player.getInventory().clear();
            player.getInventory().setArmorContents(null);

            player.getInventory().setItem(0, SpecialItems.hubGUI());
            ItemStack kitSelector = SpecialItems.kitSelector();
            player.getInventory().setItem(1, kitSelector);
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
            if (held == null) return;
            if (held.getType().equals(XMaterial.AIR.get())) return;
            if (SpecialItems.isKitSelector(held)) {
                p.performCommand("hsw gui kitSelector");
            }
            if (SpecialItems.isLobbyTeleporter(held)) {
                p.performCommand("hsw hub");
            }
            if (SpecialItems.isHubGUI(held)) {
                p.performCommand("hsw gui main");
            }
        }
    }
}
