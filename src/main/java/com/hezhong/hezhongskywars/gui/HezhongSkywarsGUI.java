package com.hezhong.hezhongskywars.gui;

import com.hezhong.hezhongskywars.utils.ColorT;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public abstract class HezhongSkywarsGUI {

    protected final Player owner;
    protected final Inventory inventory;

    public HezhongSkywarsGUI(Player owner, String title, int rows) {
        this.owner = owner;
        this.inventory = Bukkit.createInventory(null, rows * 9, ColorT.t(title));
    }

    public Player getOwner() { return owner; }
    public Inventory getInventory() { return inventory; }

    // 打开GUI
    public void open() {
        GUIListener.openGUI(this);
    }

    // 拓展的点击逻辑
    public abstract void handleClick(InventoryClickEvent event);
}