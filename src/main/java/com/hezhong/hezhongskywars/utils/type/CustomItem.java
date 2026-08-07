package com.hezhong.hezhongskywars.utils.type;

import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XPotion;
import com.hezhong.hezhongskywars.utils.MathUtil;
import lombok.EqualsAndHashCode;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode
public final class CustomItem {
    private final XMaterial material;
    private final int count;
    private final int durability;
    private final List<Pair<XEnchantment, Integer>> enchantments; // Pair X；附魔 Y：等级
    private final List<Pair<XPotion, Pair<Integer, Integer>>> potions; // Pair<Integer, Integer X等级（1based） Y时长（s）

    public CustomItem(XMaterial material, Integer count, Integer durability, List<Pair<XEnchantment, Integer>> enchantments, List<Pair<XPotion, Pair<Integer, Integer>>> potions) {
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
        if (potions != null && !potions.isEmpty()) {
            ;
            if (material == XMaterial.POTION || material == XMaterial.SPLASH_POTION ||
                    material == XMaterial.LINGERING_POTION || material == XMaterial.TIPPED_ARROW) {
                if (item.getItemMeta() instanceof PotionMeta meta) {
                    meta.clearCustomEffects();
                    for (Pair<XPotion, Pair<Integer, Integer>> potionPair : potions) {
                        XPotion xPotion = potionPair.getX();
                        int level = potionPair.getY().getX();
                        int duration = potionPair.getY().getY(); // 单位：秒。需要*20变为游戏刻！
                        PotionEffectType type = xPotion.get();
                        PotionEffect effect = new PotionEffect(type, duration * 20, level - 1, false);
                        meta.addCustomEffect(effect, true); // b是啥参数啊我去。。SpigotMC你赢了
                    }
                    item.setItemMeta(meta);
                }
            }
        }
        return item;

    }


    public static CustomItem parseItem(String input) {
        input = input.replaceAll("\\s", "");
        String[] parts = input.split(":");
        if (parts.length < 2) {
            return null;
        }

        XMaterial mat = XMaterial.matchXMaterial(parts[0]).orElse(null);
        if (mat == null) return null;

        int number = Integer.parseInt(parts[1]);

        List<Pair<XEnchantment, Integer>> enchantments = new ArrayList<>();
        List<Pair<XPotion, Pair<Integer, Integer>>> potions = new ArrayList<>();

        // 附魔解析 (parts[2] 和 parts[3])
        if (parts.length >= 4) {
            String[] rawEnchantments = parts[2].split(";");
            String[] rawEnchantmentsLevel = parts[3].split(";");
            for (int e = 0; e < rawEnchantmentsLevel.length; e++) {
                String enchantment = rawEnchantments[e].toUpperCase();
                int level = Integer.parseInt(rawEnchantmentsLevel[e]);
                XEnchantment.of(enchantment).ifPresent(xe ->
                        enchantments.add(new Pair<>(xe, level)));
            }
        }

        // 耐久/药水解析 (parts[4] ~ parts[6])
        int durability = -1;
        if (parts.length >= 5) {
            String rawData = parts[4];
            if (MathUtil.isNumeric(rawData)) {
                durability = Integer.parseInt(rawData);
            } else if (parts.length >= 7) {
                String[] rawPotions = rawData.split(";");
                String[] rawPotionLevels = parts[5].split(";");
                String[] rawPotionTimes = parts[6].split(";");
                for (int p = 0; p < rawPotions.length; p++) {
                    String potion = rawPotions[p].toUpperCase();
                    int level = Integer.parseInt(rawPotionLevels[p]);
                    int time = Integer.parseInt(rawPotionTimes[p]);
                    XPotion xP = XPotion.valueOf(potion);
                    potions.add(new Pair<>(xP, new Pair<>(level, time)));
                }
            }
        }

        return new CustomItem(mat, number, durability, enchantments, potions);
    }
}
