package com.hezhong.hezhongskywars.command;

import com.hezhong.hezhongskywars.config.ConfigValues;
import com.hezhong.hezhongskywars.config.MapConfig;
import com.hezhong.hezhongskywars.utils.ColorT;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Map;

public class ListMapsCommand extends HezhongSkywarsCommand {
    public ListMapsCommand() {
        super("listMaps", true, "", "列出地图");
    }

    @Override
    public void runCommand(CommandSender cs, Command command, String label, String[] args) {
        cs.sendMessage(ColorT.t("&a当前地图"));
        for (Map.Entry<String, MapConfig> entry : ConfigValues.mapConfigs.entrySet()) {
            String mapName = entry.getKey();
            MapConfig mc = entry.getValue();
            String copyWorldName = mc.getCopyWorld();
            int maxPlayers = mc.getMaxPlayers();
            boolean ok = mc.isOk();
            World world = Bukkit.getWorld(copyWorldName);
            cs.sendMessage(ColorT.t(String.format("&b地图 %s%s &e最大玩家&a%s %s", ok ? "&a√" : "&c×", mapName, maxPlayers, (world == null) ? "&c世界不存在" : "&a世界存在")));
        }
    }
}
