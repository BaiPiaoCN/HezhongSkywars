package com.hezhong.hezhongskywars.setup;

import com.hezhong.hezhongskywars.config.ConfigValues;
import com.hezhong.hezhongskywars.manager.SwPlayerManager;
import com.hezhong.hezhongskywars.player.SwPlayer;
import com.hezhong.hezhongskywars.utils.ColorT;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class SetupListener implements Listener {
    // 交互方块
    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player pp = e.getPlayer();
        SwPlayer sp = SwPlayerManager.getPlayer(pp);
        if (sp == null) return;
        if (sp.isSettingUpMap() && pp.getWorld().getName().equals(ConfigValues.mapConfigs.get(sp.getSetUpMapName()).getCopyWorld())) {
            if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                e.setCancelled(true);
                Block block = e.getClickedBlock();
                Location loc = block.getLocation();
                sp.getSetupMapStatus().setControllingBlock(block);
                pp.sendMessage(ColorT.t("&a选中方块 X=" + loc.getX() + " Y=" + loc.getY() + " Z=" + loc.getZ()));
            }
        }
    }
}
