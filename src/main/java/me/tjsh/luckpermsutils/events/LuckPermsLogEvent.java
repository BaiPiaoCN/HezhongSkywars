package me.tjsh.luckpermsutils.events;

import lombok.Getter;
import net.luckperms.api.actionlog.ActionLog;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public class LuckPermsLogEvent extends Event {
    private final ActionLog actionLog;
    private static final HandlerList HANDLERS = new HandlerList();
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
    public LuckPermsLogEvent(ActionLog actionLog) {
        super(true);
        this.actionLog = actionLog;
    }
}
