package com.hezhong.hezhongskywars.utils.type;

import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XItemStack;
import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XPotion;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public final class ChestItem {
    private final XMaterial material;
    private final int count;
    private final int durability;
    private final List<Pair<XEnchantment, Integer>> enchantments; // Pair X；附魔 Y：等级
    private final List<Pair<XPotion, Pair<Integer, Integer>>> potions; // Pair<Integer, Integer X等级（1based） Y时长（s）
    public ChestItem(XMaterial material, Integer count, Integer durability, List<Pair<XEnchantment, Integer>> enchantments, List<Pair<XPotion, Pair<Integer, Integer>>> potions) {
        this.material = material;
        this.count = count;
        this.durability = durability;
        this.enchantments = enchantments;
        this.potions = potions;
        // 耐久度-1即为满耐久
    }
    public ItemStack toItem() {
        ItemStack item = material.parseItem();
        if (item == null) return null;
        if (durability != -1)
            item.setDurability((short) durability);
        item.setAmount(count);
        if (enchantments != null) {
            for (Pair<XEnchantment, Integer> pair : enchantments) {
                item.addEnchantment(pair.getX().get(), pair.getY());
            }
        }
        if (potions != null && !potions.isEmpty()) {;
            if (material == XMaterial.POTION || material == XMaterial.SPLASH_POTION ||
                    material == XMaterial.LINGERING_POTION || material == XMaterial.TIPPED_ARROW) {
                if (item.getItemMeta() instanceof PotionMeta meta) {
                    meta.clearCustomEffects();
                    for (Pair<XPotion, Pair<Integer, Integer>> potionPair : potions) {
                        XPotion xPotion = potionPair.getX();
                        int level = potionPair.getY().getX();
                        int duration = potionPair.getY().getY(); // 秒
                        PotionEffectType type = xPotion.get();
                        PotionEffect effect = new PotionEffect(type, duration * 20, level - 1, false);
                        meta.addCustomEffect(effect, true); // b是啥参数啊我去。。
                    }
                    item.setItemMeta(meta);
                }
            }
        }
        return item;

    }
}
