package com.hezhong.hezhongskywars.utils.type;

import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XMaterial;

import java.util.List;

public class ChestItem {
    private final XMaterial material;
    private final String name;
    private final Integer count;
    private final Integer durability;
    private final List<XEnchantment> enchantments;
    public ChestItem(XMaterial material,  String name, Integer count, Integer durability, List<XEnchantment> enchantments) {
        this.material = material;
        this.name = name;
        this.count = count;
        this.durability = durability;
        this.enchantments = enchantments;
    }
}
