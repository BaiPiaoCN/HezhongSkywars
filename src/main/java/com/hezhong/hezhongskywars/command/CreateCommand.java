package com.hezhong.hezhongskywars.command;

import com.hezhong.hezhongskywars.HezhongSkywars;
import com.hezhong.hezhongskywars.utils.ColorT;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class CreateCommand extends HezhongSkywarsCommand {
    public CreateCommand() {
        super("create", true, "<mapName> <originalWorldName> <copyWorldName>", "新建新地图");
    }

    @Override
    public void runCommand(CommandSender cs, Command command, String label, String[] args) {
        if (args.length < 4) {
            cs.sendMessage(ColorT.t("&c命令参数不足"));
            return;
        }
        final String mapName = args[1];
        final String original = args[2];
        final String copy = args[3];
        HezhongSkywars.INSTANCE.getConfigManager().createMap(mapName, original, copy);
        cs.sendMessage(ColorT.t("&a地图 " + mapName + "已在配置文件中创建。正在创建世界......"));
        Bukkit.getScheduler().runTaskAsynchronously(HezhongSkywars.INSTANCE.getPlugin(), () -> {
            try {
                HezhongSkywars.INSTANCE.getGameManager().resetGame(mapName);
                cs.sendMessage(ColorT.t("&a&l世界创建成功，游戏已加载！"));
            } catch (Exception e) {
                cs.sendMessage(ColorT.t("&c&l地图创建失败，请查看内部报错"));
                HezhongSkywars.INSTANCE.getLogger().warning("HSW Failed to reset game");
                e.printStackTrace();
            }
        });
    }
}
