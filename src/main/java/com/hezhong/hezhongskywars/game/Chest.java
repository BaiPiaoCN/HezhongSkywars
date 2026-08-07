package com.hezhong.hezhongskywars.game;

import com.hezhong.hezhongskywars.utils.RandomUtil;
import com.hezhong.hezhongskywars.utils.type.CustomItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Chest {
    // 每个箱子都有一个Type，每次游戏加载时，去加载配置中对应type的箱子
    private final String mapName;
    // 最小填充量和最大填充量
    private final int minFilled;
    private final int maxFilled;
    // XMaterial包装 箱子物品和概率
    private final Map<CustomItem, Integer> itemProbability; // K 物品 V 权重
    private int weightTotal; // 权重总和
    public Chest(String mapName, int minFilled, int maxFilled) {
        this.mapName = mapName;
        this.itemProbability = new HashMap<>();
        this.minFilled = minFilled;
        this.maxFilled = maxFilled;
        weightTotal = 0;
    }
    public void addChestItem(int prob, CustomItem item) {
        // 添加箱子物品，配置加载时使用
        this.itemProbability.put(item, prob);
        weightTotal += prob;
    }
    // 用于填充箱子，随机生成物品
    public List<CustomItem> randomGenerateItems() {
        if (weightTotal < 1) {
            return new ArrayList<>();
        }
        int generated = 0;
        List<CustomItem> items = new ArrayList<>();
        int toGenerate = RandomUtil.randomInt(minFilled, maxFilled);
        // 能跑就行
        // 我也不知道我是怎么写出的这些屎山，反正能跑就行！
        for (int i = 0; i < toGenerate; i++) {
            int randomN = RandomUtil.randomInt(1, weightTotal);
            int tW = 0; // totalWeight 相当于区间的左端点
            for (Map.Entry<CustomItem, Integer> entry : itemProbability.entrySet()) {
                if (randomN > tW && randomN <= (tW + entry.getValue())) { // 区间概率计算
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
