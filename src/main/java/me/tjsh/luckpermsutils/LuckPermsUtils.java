package me.tjsh.luckpermsutils;

import me.tjsh.luckpermsutils.events.LuckPermsLogEvent;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.actionlog.Action;
import net.luckperms.api.actionlog.ActionLog;
import net.luckperms.api.event.EventBus;
import net.luckperms.api.event.user.UserLoadEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;

public final class LuckPermsUtils extends JavaPlugin {
    public static final String Prefix = "&bLuckPerms&aUtils&7> ";
    public static LuckPerms provider;
    public static LuckPermsUtils instance;
    public static CompletableFuture<ActionLog> logFuture;
    public static Executor executor; // 这个方法用于接收LuckPerms的Log异步回调


    @Override
    public void onEnable() {
        instance = this;
        // Plugin startup logic
        provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class).getProvider();
        if (provider == null) {
            sayToConsole(ChatColor.translateAlternateColorCodes('&', Prefix + "&cLuckPerms not found!"));
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        logFuture = provider.getActionLogger().getLog();
        executor = runnable -> Bukkit.getScheduler().runTask(this, runnable);
        sayToConsole(ChatColor.translateAlternateColorCodes('&', Prefix + "&aPlugin started."));
        logFuture.whenCompleteAsync((log, exception) -> { // 使用lambda也可以实现，接收到log就拉起事件
            if (exception != null) {
                LuckPermsLogEvent event = new LuckPermsLogEvent(log);
                Bukkit.getPluginManager().callEvent(event);
            }
        }, executor);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void sayToConsole(String message) {
        getLogger().info(message);
    }
}
