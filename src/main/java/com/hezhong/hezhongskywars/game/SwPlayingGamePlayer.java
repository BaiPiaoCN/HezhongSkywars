package com.hezhong.hezhongskywars.game;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

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

    public SwPlayingGamePlayer(Player pp) {
        this.player = pp;
    }
}