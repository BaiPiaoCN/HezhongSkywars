package com.hezhong.hezhongskywars;

import com.hezhong.hezhongskywars.command.CommandProcessor;
import com.hezhong.hezhongskywars.config.ConfigManager;
import com.hezhong.hezhongskywars.game.GameListener;
// import com.hezhong.hezhongskywars.game.gui.GUIListener;
import com.hezhong.hezhongskywars.listeners.JoinQuitListener;
import com.hezhong.hezhongskywars.manager.GameManager;
import com.hezhong.hezhongskywars.manager.ListenerManager;
import com.hezhong.hezhongskywars.setup.SetupListener;
import com.hezhong.hezhongskywars.task.PlayerScoreBoardTask;
import com.hezhong.hezhongskywars.utils.ColorT;
import lombok.Getter;
import org.bukkit.event.HandlerList;

import java.util.logging.Logger;

@Getter
public enum HezhongSkywars {
    INSTANCE;

    private HezhongSkywarsLoader plugin;
    private ConfigManager configManager;
    private GameManager gameManager;
    private Logger logger;

    public void start(HezhongSkywarsLoader plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();

        logger.info(ColorT.t("&aStarting &bHSW"));

        configManager = new ConfigManager(plugin);
        configManager.loadConfig();
        logger.info(ColorT.t("Config OK"));
        gameManager = new GameManager(plugin);
        gameManager.init();

        // 监听器

        if (ListenerManager.gameListener == null)
            ListenerManager.gameListener = new GameListener();
        if (ListenerManager.joinQuitListener == null)
            ListenerManager.joinQuitListener = new JoinQuitListener();
        if (ListenerManager.setupListener == null)
            ListenerManager.setupListener = new SetupListener();
        /*
        if (ListenerManager.guiListener == null)
            ListenerManager.guiListener = new GUIListener();

         */

        plugin.getServer().getPluginManager().registerEvents(ListenerManager.gameListener, plugin);
        plugin.getServer().getPluginManager().registerEvents(ListenerManager.joinQuitListener, plugin);
        plugin.getServer().getPluginManager().registerEvents(ListenerManager.setupListener, plugin);
        // plugin.getServer().getPluginManager().registerEvents(ListenerManager.guiListener, plugin);

        // 命令
        plugin.getCommand("hsw").setExecutor(new CommandProcessor());

        // 任务
        new PlayerScoreBoardTask().runTaskTimer(plugin, 0, 10);

        logger.info(ColorT.t("Listeners OK"));
        logger.info(ColorT.t("&b&lHSW &a&lStarted successfully!"));


    }

    public void stop() {
        // 注销监听器
        HandlerList.unregisterAll(plugin);
        logger.info("HSW Stopped.");
    }
}
