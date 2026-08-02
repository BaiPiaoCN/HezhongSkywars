package com.hezhong.hezhongskywars.utils;

import com.cryptomorin.xseries.XMaterial;
import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.changeme.nbtapi.iface.ReadableNBT;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SpecialItems {
    public static ItemStack kitSelector() {
        ItemStack i = XMaterial.DIAMOND_AXE.parseItem();
        ItemMeta m = i.getItemMeta();
        m.setDisplayName(ColorT.t("&a&l职业选择"));
        i.setItemMeta(m);
        NBT.modify(i, nbt -> {
            nbt.setBoolean("kitSelector", true);
        });
        return i;
    }
    public static boolean isKitSelector(ItemStack i) {
        ReadableNBT nbt = NBT.readNbt(i);
        return nbt.getBoolean("kitSelector");
    }
    public static ItemStack lobbyTeleporter() {
        ItemStack i = XMaterial.REDSTONE.parseItem();
        ItemMeta m = i.getItemMeta();
        m.setDisplayName(ColorT.t("&c&l返回大厅"));
        i.setItemMeta(m);
        NBT.modify(i, nbt -> {
            nbt.setBoolean("lobbyTeleporter", true);
        });
        return i;
    }
    public static boolean isLobbyTeleporter(ItemStack i) {
        ReadableNBT nbt = NBT.readNbt(i);
        return nbt.getBoolean("lobbyTeleporter");
    }
    public static ItemStack hubGUI() {
        ItemStack i = XMaterial.COMPASS.parseItem();
        ItemMeta m = i.getItemMeta();
        m.setDisplayName(ColorT.t("&a&l面板"));
        i.setItemMeta(m);
        NBT.modify(i, nbt -> {
            nbt.setBoolean("hubGUI", true);
        });
        return i;
    }
    public static boolean isHubGUI(ItemStack i) {
        ReadableNBT nbt = NBT.readNbt(i);
        return nbt.getBoolean("hubGUI");
    }
    public static ItemStack toPlay() {
        ItemStack i = XMaterial.ENDER_EYE.parseItem();
        ItemMeta m = i.getItemMeta();
        m.setDisplayName(ColorT.t("&a自动匹配"));
        i.setItemMeta(m);
        NBT.modify(i, nbt -> {
            nbt.setBoolean("toPlay", true);
        });
        return i;
    }
    public static boolean isToPlay(ItemStack i) {
        ReadableNBT nbt = NBT.readNbt(i);
        return nbt.getBoolean("toPlay");
    }
    public static ItemStack mapSelector() {
        ItemStack i = XMaterial.PAPER.parseItem();
        ItemMeta m = i.getItemMeta();
        m.setDisplayName(ColorT.t("&a查看地图"));
        i.setItemMeta(m);
        NBT.modify(i, nbt -> {
            nbt.setBoolean("mapSelector", true);
        });
        return i;
    }
    public static boolean isMapSelector(ItemStack i) {
        ReadableNBT nbt = NBT.readNbt(i);
        return nbt.getBoolean("mapSelector");
    }

}
