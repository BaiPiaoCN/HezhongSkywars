package com.hezhong.hezhongskywars.config;

import com.hezhong.hezhongskywars.utils.type.CustomItem;
import lombok.Getter;

import java.util.Map;

@Getter
public final class ChestConfig {
    private final String type;
    private final int minFilled;
    private final int maxFilled;
    private final Map<CustomItem, Integer> item; // 依旧 物品:权重
    public ChestConfig(String type, int minFilled, int maxFilled, Map<CustomItem, Integer> item) {
        this.type = type;
        this.minFilled = minFilled;
        this.maxFilled = maxFilled;
        this.item = item;
    }
}
