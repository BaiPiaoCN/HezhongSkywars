package com.hezhong.hezhongskywars.utils.type;

import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XPotion;

import java.util.List;

public final class ChestItem {
    private final XMaterial material;
    private final Integer count;
    private final Integer durability;
    private final List<Pair<XEnchantment, Integer>> enchantments; // Pair X；附魔 Y：等级
    private final List<Pair<XPotion, Integer>> potions;
    public ChestItem(XMaterial material, Integer count, Integer durability, List<Pair<XEnchantment, Integer>> enchantments, List<Pair<XPotion, Integer>> potions) {
        this.material = material;
        this.count = count;
        this.durability = durability;
        this.enchantments = enchantments;
        this.potions = potions;
        // 耐久度-1即为满耐久
    }
}
