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
        RESET_CHEST {
            @Override
            public String getEventName() {
                return "重置箱子";
            }
        },
        STOP_GAME {
            @Override
            public String getEventName() {
                return "结束游戏";
            }
        };

        public abstract String getEventName();
    }
}
