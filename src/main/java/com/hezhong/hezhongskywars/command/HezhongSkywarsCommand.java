package com.hezhong.hezhongskywars.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public abstract class HezhongSkywarsCommand {
    protected final String commandName;
    protected final boolean permission;
    protected final String needArgs;
    protected final String description;

    public HezhongSkywarsCommand(String commandName, boolean permission, String needArgs, String description) {
        this.commandName = commandName;
        this.permission = permission;
        this.needArgs = needArgs;
        this.description = description;
    }

    public boolean enoughPermission(CommandSender cs) {
        if (!permission) return true;
        return cs.hasPermission("hsw.command." + commandName);
    }

    public abstract void runCommand(CommandSender cs, Command command, String label, String[] args);

    public String getCommandName() {
        return commandName;
    }

    public boolean isPermission() {
        return permission;
    }

    public String getNeedArgs() {
        return needArgs;
    }

    public String getDescription() {
        return description;
    }
}
