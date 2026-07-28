package com.hezhong.hezhongskywars.utils.bukkit;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public class CTask {
    private final Plugin plugin;
    private final Runnable task;
    public volatile boolean cancelled = false;

    @Getter
    private volatile BukkitTask bukkitTask;

    public CTask(Plugin plugin, Runnable task) {
        this.plugin = plugin;
        this.task = task;
    }

    public void asyncRunTimer(long period, long startDelay) {
        if (isCancelled()) {
            bukkitTask = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, task, startDelay, period);
            cancelled = false;
        }
    }
    public void runTimer(long period, long startDelay) {
        if (isCancelled()) {
            bukkitTask = Bukkit.getScheduler().runTaskTimer(plugin, task, startDelay, period);
            cancelled = false;
        }
    }
    public void asyncRun() {
        if (isCancelled()) {
            bukkitTask = Bukkit.getScheduler().runTaskAsynchronously(plugin, task);
            cancelled = false;
        }
    }
    public void run() {
        if (isCancelled()) {
            bukkitTask = Bukkit.getScheduler().runTask(plugin, task);
            cancelled = false;
        }
    }
    public void asyncRunLater(long delay) {
        if (isCancelled()) {
            bukkitTask = Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, task, delay);
            cancelled = false;
        }
    }
    public void runLater(long delay) {
        if (isCancelled()) {
            bukkitTask = Bukkit.getScheduler().runTaskLater(plugin, task, delay);
            cancelled = false;
        }
    }

    public boolean isCancelled() {
        return cancelled || bukkitTask == null;
    }

    public void cancel() {
        if (!isCancelled()) {
            bukkitTask.cancel();
            cancelled = true;
        }
    }


}
