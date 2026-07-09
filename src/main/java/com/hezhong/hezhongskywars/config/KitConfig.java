package com.hezhong.hezhongskywars.config;

import com.hezhong.hezhongskywars.utils.type.CustomItem;
import lombok.Getter;

import java.util.List;

@Getter
public class KitConfig {
    private final int coins;
    private final String permission;
    private final String material;
    private final List<CustomItem> items;
    public KitConfig(int coins, String permission, String material, List<CustomItem> items) {
        this.coins = coins;
        this.permission = permission;
        this.material = material;
        this.items = items;
    }
}
