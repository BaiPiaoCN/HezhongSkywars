package com.hezhong.hezhongskywars.game;

import lombok.Getter;

@Getter
public final class GameEvent {
    private final int time;
    private final EventType type;
    public GameEvent(int time, EventType type) {
        this.time = time;
        this.type = type;
    }

    public enum EventType {
        RESETCHEST,
        STOPGAME;
    }
}
