package com.hezhong.hezhongskywars.events;


import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class HSWGameStartEvent extends Event {
    private final String mapName;

    public HSWGameStartEvent(String mapName) {
        super(false);
        this.mapName = mapName;
    }
    private static final HandlerList handlers = new HandlerList();

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
