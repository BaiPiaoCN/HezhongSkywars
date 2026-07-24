package com.hezhong.hezhongskywars.gui;

import com.hezhong.hezhongskywars.player.SwPlayer;
import com.hezhong.hezhongskywars.utils.ColorT;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

@Getter
public abstract class HezhongSkywarsGUI {

    protected final Player owner;
    protected final SwPlayer ownerSp;
    protected final Inventory inventory;

    public HezhongSkywarsGUI(Player owner, SwPlayer ownerSp, String title, int rows) {
        this.owner = owner;
        this.ownerSp = ownerSp;
        this.inventory = Bukkit.createInventory(null, rows * 9, ColorT.t(title));
    }



    // 打开GUI
    public void open() {
        GUIListener.openGUI(this);
    }

    // 拓展的点击逻辑
    public abstract void handleClick(InventoryClickEvent event);
}