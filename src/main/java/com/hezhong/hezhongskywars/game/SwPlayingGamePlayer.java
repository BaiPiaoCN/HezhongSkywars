package com.hezhong.hezhongskywars.game;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.*;

@Getter
@Setter
public class SwPlayingGamePlayer {

    public enum PlayerStatus {
        ALIVE,
        DEAD,
        SPECTATE,
        QUIT
    }

    private final Player player;

    private int kills = 0;
    private PlayerStatus status = PlayerStatus.ALIVE;
    private String selectedKit = "None";

    // 使用UUID作为键，避免玩家退出导致Player失效
    // 此为被攻击的伤害
    private Map<UUID, Double> damageByAttack = new HashMap<>();
    public double totalDamage; // 总伤害

    // 最开始就是旁观者
    private final boolean asSpectator;

    public SwPlayingGamePlayer(Player pp, boolean asSpectator) {
        this.player = pp;
        this.asSpectator = asSpectator;
    }

    public void addDamage(UUID uuid, double dmg) {
        totalDamage += dmg;
        double pastDmg = damageByAttack.getOrDefault(uuid, 0d);
        damageByAttack.put(uuid, pastDmg + dmg);
    }
}