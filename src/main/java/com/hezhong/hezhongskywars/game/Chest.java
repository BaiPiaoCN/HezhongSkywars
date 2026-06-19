package com.hezhong.hezhongskywars.game;

import com.hezhong.hezhongskywars.utils.RandomUtil;
import com.hezhong.hezhongskywars.utils.type.ChestItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Chest {
    private final String mapName;
    // 最小填充量和最大填充量
    private final int minFilled;
    private final int maxFilled;
    // id只是拿来标记的
    private final int chestId;
    // XMaterial包装 箱子物品和概率
    private final Map<ChestItem, Integer> itemProbability; // K 物品 V 权重
    private int weightTotal; // 权重总和
    public Chest(String mapName, int chestId, int minFilled, int maxFilled) {
        this.mapName = mapName;
        this.chestId = chestId;
        this.itemProbability = new HashMap<>();
        this.minFilled = minFilled;
        this.maxFilled = maxFilled;
        weightTotal = 0;
    }
    public void addChestItem(int prob, ChestItem item) {
        // 添加箱子物品，配置加载时使用
        this.itemProbability.put(item, prob);
        weightTotal += prob;
    }
    // 用于填充箱子，随机生成物品
    public List<ChestItem> randomGenerateItems() {
        if (weightTotal < 1) {
            return new ArrayList<>();
        }
        int generated = 0;
        List<ChestItem> items = new ArrayList<>();
        int toGenerate = RandomUtil.randomInt(minFilled, maxFilled);
        for (int i = 0; i < toGenerate; i++) {
            int randomN = RandomUtil.randomInt(1, weightTotal);
            int tW = 0;
            for (Map.Entry<ChestItem, Integer> entry : itemProbability.entrySet()) {
                if (randomN > tW && randomN <= (tW + entry.getValue())) {
                    items.add(entry.getKey());
                    generated++;
                    break;
                }
                tW += entry.getValue();
            }
        }
        return items;
    }
}
