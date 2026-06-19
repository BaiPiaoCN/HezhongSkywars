package com.hezhong.hezhongskywars;

import com.hezhong.hezhongskywars.config.ConfigManager;
import com.hezhong.hezhongskywars.game.GameListener;
import com.hezhong.hezhongskywars.game.gui.GUIListener;
import com.hezhong.hezhongskywars.listeners.JoinQuitListener;
import com.hezhong.hezhongskywars.manager.ListenerManager;
import com.hezhong.hezhongskywars.setup.SetupListener;
import com.hezhong.hezhongskywars.utils.ColorT;
import lombok.Getter;

import java.util.logging.Logger;

@Getter
public enum HezhonSkywars {
    INSTANCE;

    private HezhongSkywarsLoader plugin;
    private ConfigManager configManager;
    private Logger logger;

    public void start(HezhongSkywarsLoader plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();

        logger.info(ColorT.t("&aStarting &bHSW"));

        configManager = new ConfigManager(plugin);
        logger.info(ColorT.t("Config OK"));

        if (ListenerManager.gameListener == null)
            ListenerManager.gameListener = new GameListener();
        if (ListenerManager.joinQuitListener == null)
            ListenerManager.joinQuitListener = new JoinQuitListener();
        if (ListenerManager.setupListener == null)
            ListenerManager.setupListener = new SetupListener();
        if (ListenerManager.guiListener != null)
            ListenerManager.guiListener = new GUIListener();

        plugin.getServer().getPluginManager().registerEvents(ListenerManager.gameListener, plugin);
        plugin.getServer().getPluginManager().registerEvents(ListenerManager.joinQuitListener, plugin);
        plugin.getServer().getPluginManager().registerEvents(ListenerManager.setupListener, plugin);
        plugin.getServer().getPluginManager().registerEvents(ListenerManager.guiListener, plugin);

        logger.info(ColorT.t("Listeners OK"));
        logger.info(ColorT.t("&b&lHSW &a&lStarted successfully!"));


    }
}
